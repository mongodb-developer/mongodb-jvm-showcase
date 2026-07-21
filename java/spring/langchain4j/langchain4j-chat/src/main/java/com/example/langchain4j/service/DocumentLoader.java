package com.example.langchain4j.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.segment.TextSegment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DocumentLoader {

    private record RainforestDoc(String text) {}

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public List<TextSegment> load() throws Exception {
        var resource = new ClassPathResource("rainforest-docs.json");

        RainforestDoc[] docs = objectMapper.readValue(resource.getInputStream(), RainforestDoc[].class);

        return java.util.Arrays.stream(docs)
                .map(doc -> TextSegment.from(doc.text()))
                .toList();
    }
}
