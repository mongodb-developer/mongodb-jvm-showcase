package com.devnexus.frauddetection.infrastructure.message.consumer;

import com.devnexus.frauddetection.application.usecase.VectorScoringUseCase;
import com.devnexus.frauddetection.domain.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionToScoreConsumer {

    private static final Logger log = LoggerFactory.getLogger(TransactionToScoreConsumer.class);
    private final VectorScoringUseCase vectorScoringUseCase;

    public TransactionToScoreConsumer(
            VectorScoringUseCase vectorScoringUseCase
    ) {
        this.vectorScoringUseCase = vectorScoringUseCase;
    }

    @KafkaListener(
            topics = "${app.kafka.topics.to-score}",
            groupId = "${app.kafka.consumers.to-score.group-id}",
            properties = {
                    "auto.offset.reset=${app.kafka.consumers.to-score.auto-offset-reset}",
                    "key.deserializer=${app.kafka.consumers.to-score.key-deserializer}",
                    "value.deserializer=${app.kafka.consumers.to-score.value-deserializer}",
                    "spring.deserializer.value.delegate.class=${app.kafka.consumers.to-score.delegate-deserializer}",
                    "spring.json.use.type.headers=${app.kafka.consumers.to-score.use-type-headers}",
                    "spring.json.value.default.type=${app.kafka.consumers.to-score.value-type}",
                    "spring.json.trusted.packages=${app.kafka.consumers.to-score.trusted-packages}"
            }
    )
    public void onMessage(Transaction tx) {
        if (tx == null) {
            log.info(">>> TO SCORE: <null tx>");
            return;
        }

        vectorScoringUseCase.score(tx);
    }
}
