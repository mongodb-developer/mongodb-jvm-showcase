package com.devnexus.frauddetection.infrastructure.config;

import com.devnexus.frauddetection.application.usecase.ProduceTransactionUseCase;
import com.devnexus.frauddetection.application.usecase.SaveSuspiciousTransactionUseCase;
import com.devnexus.frauddetection.application.usecase.VectorScoringUseCase;
import com.devnexus.frauddetection.domain.port.FraudPatternSearchPort;
import com.devnexus.frauddetection.domain.port.TransactionEmbedderPort;
import com.devnexus.frauddetection.domain.port.TransactionPersistencePort;
import com.devnexus.frauddetection.domain.port.TransactionProducerPort;
import com.devnexus.frauddetection.infrastructure.streams.config.VectorFraudProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public VectorScoringUseCase scoreTransactionUseCase(
            TransactionEmbedderPort embedder,
            FraudPatternSearchPort fraudPatternSearch,
            TransactionPersistencePort persistence,
            VectorFraudProperties vectorFraudProperties
    ) {
        return new VectorScoringUseCase(
                embedder,
                fraudPatternSearch,
                persistence,
                vectorFraudProperties.similarityThreshold()
        );
    }

    @Bean
    public ProduceTransactionUseCase produceTransactionUseCase(
            TransactionProducerPort transactionProducerPort
    ) {
        return new ProduceTransactionUseCase(transactionProducerPort);
    }

    @Bean
    public SaveSuspiciousTransactionUseCase saveSuspiciousTransactionUseCase(
            TransactionPersistencePort transactionPersistencePort
    ) {
        return new SaveSuspiciousTransactionUseCase(transactionPersistencePort);
    }

 }
