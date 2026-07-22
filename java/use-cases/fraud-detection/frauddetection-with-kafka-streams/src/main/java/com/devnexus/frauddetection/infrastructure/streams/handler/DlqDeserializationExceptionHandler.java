package com.devnexus.frauddetection.infrastructure.streams.handler;

import com.devnexus.frauddetection.infrastructure.message.support.KafkaHeaders;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.errors.DeserializationExceptionHandler;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

public class DlqDeserializationExceptionHandler implements DeserializationExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(DlqDeserializationExceptionHandler.class);

    public static final String DLQ_TOPIC_CONFIG = "dlq.topic.name";

    private Producer<String, byte[]> producer;
    private String dlqTopic;

    @Override
    public void configure(Map<String, ?> configs) {
        this.dlqTopic = (String) configs.get(DLQ_TOPIC_CONFIG);

        if (this.dlqTopic == null) {
            throw new IllegalArgumentException("DLQ topic not configured. Set '" + DLQ_TOPIC_CONFIG + "' property.");
        }

        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, configs.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());

        this.producer = new KafkaProducer<>(producerProps);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Closing DLQ producer");
            producer.close();
        }));

        log.info("DLQ handler configured with topic: {}", dlqTopic);
    }

    @Override
    public DeserializationHandlerResponse handle(ProcessorContext context,
                                                  ConsumerRecord<byte[], byte[]> record,
                                                  Exception exception) {
        log.error("Deserialization error on topic={}, partition={}, offset={}",
                record.topic(), record.partition(), record.offset(), exception);

        try {
            Headers headers = KafkaHeaders.forDlq(record.topic(), exception);

            ProducerRecord<String, byte[]> dlqRecord = new ProducerRecord<>(
                    dlqTopic,
                    null,
                    null,
                    null,
                    record.value(),
                    headers
            );

            producer.send(dlqRecord, (metadata, ex) -> {
                if (ex != null) {
                    log.error("Failed to send to DLQ topic={}", dlqTopic, ex);
                } else {
                    log.info("Sent to DLQ topic={}, partition={}, offset={}",
                            metadata.topic(), metadata.partition(), metadata.offset());
                }
            });
        } catch (Exception e) {
            log.error("Error sending to DLQ", e);
        }

        return DeserializationHandlerResponse.CONTINUE;
    }

}