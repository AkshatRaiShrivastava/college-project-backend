package com.akshat.college_project.common;

import java.util.UUID;

public final class IdGenerator {

    private IdGenerator() {
    }

    public static String generate(String prefix) {
        String base = UUID.randomUUID().toString().replace("-", "");
        String value = prefix + base;
        return value.substring(0, Math.min(30, value.length()));
    }
}
