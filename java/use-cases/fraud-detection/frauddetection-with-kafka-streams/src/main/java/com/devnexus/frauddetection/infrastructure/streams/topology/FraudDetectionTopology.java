package com.devnexus.frauddetection.infrastructure.streams.topology;

import com.devnexus.frauddetection.domain.model.FraudDetectionState;
import com.devnexus.frauddetection.domain.model.SuspiciousAlert;
import com.devnexus.frauddetection.domain.model.Transaction;
import com.devnexus.frauddetection.domain.rules.ImpossibleTravelValidator;
import com.devnexus.frauddetection.domain.rules.VelocityCheckValidator;
import com.devnexus.frauddetection.infrastructure.message.config.TopicsProperties;
import com.devnexus.frauddetection.infrastructure.streams.serde.JsonSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FraudDetectionTopology {

    private final TopicsProperties topics;
    private final JsonSerde jsonSerde;
    private final ImpossibleTravelValidator impossibleTravelValidator;
    private final VelocityCheckValidator velocityCheckValidator;

    public FraudDetectionTopology(
            TopicsProperties topics,
            JsonSerde jsonSerde,
            ImpossibleTravelValidator impossibleTravelValidator,
            VelocityCheckValidator velocityCheckValidator
    ) {
        this.topics = topics;
        this.jsonSerde = jsonSerde;
        this.impossibleTravelValidator = impossibleTravelValidator;
        this.velocityCheckValidator = velocityCheckValidator;
    }

    @Bean
    public FraudDetectionStreamProcessor fraudDetectionStreamProcessor() {
        return new FraudDetectionStreamProcessor(impossibleTravelValidator, velocityCheckValidator);
    }

    @Bean
    public KStream<String, FraudDetectionState> fraudDetectionStream(
            StreamsBuilder builder,
            FraudDetectionStreamProcessor processor
    ) {
        Serde<Transaction> transactionSerde = jsonSerde.forClass(Transaction.class);
        Serde<FraudDetectionState> stateSerde = jsonSerde.forClass(FraudDetectionState.class);
        Serde<SuspiciousAlert> suspiciousEventSerde =
                jsonSerde.forClass(SuspiciousAlert.class);

        return processor.buildTopology(
                builder,
                topics.transactions(),
                topics.suspicious(),
                topics.toScore(),
                transactionSerde,
                stateSerde,
                suspiciousEventSerde
        );
    }
}
