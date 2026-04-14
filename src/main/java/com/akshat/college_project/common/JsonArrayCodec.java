package com.akshat.college_project.common;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class JsonArrayCodec {

    public String toJson(List<String> values) {
        List<String> safeValues = values == null
                ? Collections.emptyList()
                : values.stream().filter(Objects::nonNull).toList();
        if (safeValues.isEmpty()) {
            return "[]";
        }
        return safeValues.stream()
                .map(this::quote)
                .collect(Collectors.joining(",", "[", "]"));
    }

    public List<String> fromJson(String jsonArray) {
        if (jsonArray == null || jsonArray.isBlank()) {
            return new ArrayList<>();
        }
        String trimmed = jsonArray.trim();
        if ("[]".equals(trimmed)) {
            return new ArrayList<>();
        }
        if (!trimmed.startsWith("[") || !trimmed.endsWith("]")) {
            throw new BadRequestException("Stored JSON array is invalid");
        }

        String body = trimmed.substring(1, trimmed.length() - 1).trim();
        if (body.isBlank()) {
            return new ArrayList<>();
        }

        String[] parts = body.split(",");
        List<String> result = new ArrayList<>();
        for (String part : parts) {
            String token = part.trim();
            if (token.startsWith("\"") && token.endsWith("\"") && token.length() >= 2) {
                token = token.substring(1, token.length() - 1);
            }
            token = token.replace("\\\"", "\"").replace("\\\\", "\\");
            if (!token.isBlank()) {
                result.add(token);
            }
        }
        return result;
    }

    private String quote(String value) {
        String escaped = value.replace("\\", "\\\\").replace("\"", "\\\"");
        return "\"" + escaped + "\"";
    }
}
