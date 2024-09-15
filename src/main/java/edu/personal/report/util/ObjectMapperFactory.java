package edu.personal.report.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

public final class ObjectMapperFactory {
    private static ObjectMapper objectMapper;

    private ObjectMapperFactory() {
    }

    public static ObjectMapper getOrCreateObjectMapper() {
        if (objectMapper == null) {
            objectMapper = JsonMapper.builder().build();
        }
        return objectMapper;
    }
}