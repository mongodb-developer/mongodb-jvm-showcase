package com.devnexus.frauddetection.infrastructure.streams.topology;

import com.devnexus.frauddetection.domain.model.FraudDetectionState;
import com.devnexus.frauddetection.domain.model.SuspiciousAlert;
import com.devnexus.frauddetection.domain.model.Transaction;
import com.devnexus.frauddetection.domain.rules.ImpossibleTravelValidator;
import com.devnexus.frauddetection.domain.rules.VelocityCheckValidator;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Branched;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class FraudDetectionStreamProcessor {

    private static final Logger log = LoggerFactory.getLogger(FraudDetectionStreamProcessor.class);

    private final ImpossibleTravelValidator impossibleTravelValidator;
    private final VelocityCheckValidator velocityCheckValidator;

    public FraudDetectionStreamProcessor(
            ImpossibleTravelValidator impossibleTravelValidator,
            VelocityCheckValidator velocityCheckValidator
    ) {
        this.impossibleTravelValidator = impossibleTravelValidator;
        this.velocityCheckValidator = velocityCheckValidator;
    }

    public KStream<String, FraudDetectionState> buildTopology(
            StreamsBuilder builder,
            String transactionsTopic,
            String suspiciousTopic,
            String toScoreTopic,
            Serde<Transaction> transactionSerde,
            Serde<FraudDetectionState> stateSerde,
            Serde<SuspiciousAlert> suspiciousEventSerde
    ) {
        KStream<String, Transaction> stream = builder.stream(
                transactionsTopic,
                Consumed.with(Serdes.String(), transactionSerde)
        );

        KStream<String, FraudDetectionState> processedStream = applyFraudRules(stream, transactionSerde, stateSerde);

        branchResults(processedStream, suspiciousTopic, toScoreTopic, transactionSerde, suspiciousEventSerde);

        return processedStream;
    }

    private KStream<String, FraudDetectionState> applyFraudRules(
            KStream<String, Transaction> stream,
            Serde<Transaction> transactionSerde,
            Serde<FraudDetectionState> stateSerde
    ) {
        return stream
                .filter((key, tx) -> tx != null)
                .selectKey((key, tx) -> tx.cardNumber())
                .groupByKey(Grouped.with(Serdes.String(), transactionSerde))
                .aggregate(
                        FraudDetectionState::empty,
                        (cardNumber, newTransaction, currentState) -> {
                            SuspiciousAlert alert = null;

                            log.info("[GUARDRAILS] Fraud rule validations started");

                            Transaction previousTx = currentState.lastTransaction();
                            if (previousTx != null) {
                                alert = impossibleTravelValidator.validate(previousTx, newTransaction).orElse(null);
                            }

                            if (alert == null) {
                                long count = currentState.countTransactionsInWindow(velocityCheckValidator.getWindowMinutes()) + 1;
                                alert = velocityCheckValidator.validate(count, newTransaction).orElse(null);
                            }

                            return currentState.withTransaction(newTransaction, alert);
                        },
                        Materialized.<String, FraudDetectionState, KeyValueStore<Bytes, byte[]>>as("fraud-detection-store")
                                .withKeySerde(Serdes.String())
                                .withValueSerde(stateSerde)
                )
                .toStream()
                .peek((cardNumber, state) -> log.info("[GUARDRAILS] Fraud check — card={}, hasFraud={}",
                        cardNumber, state.hasFraudAlert()));
    }

    private void branchResults(
            KStream<String, FraudDetectionState> processedStream,
            String suspiciousTopic,
            String toScoreTopic,
            Serde<Transaction> transactionSerde,
            Serde<SuspiciousAlert> suspiciousEventSerde
    ) {
        processedStream
                .split(Named.as("fraud-"))
                .branch(
                        (cardNumber, state) -> state.hasFraudAlert(),
                        Branched.withConsumer(fraudStream ->
                                fraudStream
                                        .mapValues(state -> new SuspiciousAlert(
                                                state.lastTransaction(),
                                                state.suspiciousAlert().ruleId(),
                                                state.suspiciousAlert().description(),
                                                Instant.now()
                                        ))
                                        .peek((card, evt) -> log.info(
                                                "[GUARDRAILS] Blocked — txId={}, rule={}",
                                                evt.transaction().transactionId(),
                                                evt.ruleId()))
                                        .to(suspiciousTopic,
                                                Produced.with(Serdes.String(), suspiciousEventSerde))
                        )
                )
                .defaultBranch(
                        Branched.withConsumer(passedStream ->
                                passedStream
                                        .mapValues(FraudDetectionState::lastTransaction)
                                        .peek((cardNumber, tx) -> log.info(
                                                "[GUARDRAILS] Approved — card={}, txId={}",
                                                cardNumber,
                                                tx.transactionId()))
                                        .to(toScoreTopic,
                                                Produced.with(Serdes.String(), transactionSerde))
                        )
                );
    }
}
