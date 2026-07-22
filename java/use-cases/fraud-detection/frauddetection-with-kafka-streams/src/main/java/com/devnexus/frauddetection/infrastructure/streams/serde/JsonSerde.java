package com.devnexus.frauddetection.infrastructure.streams.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.stereotype.Component;

@Component
public class JsonSerde {

	private final ObjectMapper objectMapper = new ObjectMapper()
			.registerModule(new JavaTimeModule())
			.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

	public <T> Serde<T> forClass(Class<T> clazz) {
		Serializer<T> serializer = (topic, data) -> {
			try {
				return objectMapper.writeValueAsBytes(data);
			} catch (Exception e) {
				throw new RuntimeException("Error serializing", e);
			}
		};

		Deserializer<T> deserializer = (topic, data) -> {
			if (data == null) {
				return null;
			}
			try {
				return objectMapper.readValue(data, clazz);
			} catch (Exception e) {
				throw new RuntimeException("Error deserializing", e);
			}
		};

		return Serdes.serdeFrom(serializer, deserializer);
	}
}
