package com.devnexus.frauddetection.infrastructure.message.consumer;

import com.devnexus.frauddetection.application.usecase.SaveSuspiciousTransactionUseCase;
import com.devnexus.frauddetection.domain.model.SuspiciousAlert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class SuspiciousTransactionConsumer {

    private static final Logger log = LoggerFactory.getLogger(SuspiciousTransactionConsumer.class);

    private final SaveSuspiciousTransactionUseCase saveSuspiciousTransactionUseCase;

    public SuspiciousTransactionConsumer(SaveSuspiciousTransactionUseCase saveSuspiciousTransactionUseCase) {
        this.saveSuspiciousTransactionUseCase = saveSuspiciousTransactionUseCase;
    }

    @KafkaListener(
            topics = "${app.kafka.topics.suspicious}",
            groupId = "${app.kafka.consumers.suspicious.group-id}",
            properties = {
                    "key.deserializer=${app.kafka.consumers.suspicious.key-deserializer}",
                    "value.deserializer=${app.kafka.consumers.suspicious.value-deserializer}",
                    "spring.deserializer.value.delegate.class=${app.kafka.consumers.suspicious.delegate-deserializer}",
                    "spring.json.use.type.headers=${app.kafka.consumers.suspicious.use-type-headers}",
                    "spring.json.value.default.type=${app.kafka.consumers.suspicious.value-type}",
                    "spring.json.trusted.packages=${app.kafka.consumers.suspicious.trusted-packages}"
            }
    )
    public void onMessage(SuspiciousAlert evt) {
        saveSuspiciousTransactionUseCase.execute(evt);
    }
}
