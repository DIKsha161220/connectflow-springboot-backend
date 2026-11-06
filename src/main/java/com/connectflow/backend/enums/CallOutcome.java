package com.connectflow.backend.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Locale;

public enum CallOutcome {
    INTERESTED("Interested"),
    CALL_BACK("Call Back"),
    NO_ANSWER("No Answer"),
    NOT_INTERESTED("Not Interested");

    private final String value;

    CallOutcome(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static CallOutcome fromValue(String value) {
        String normalized = value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(outcome -> outcome.value.toLowerCase(Locale.ROOT).equals(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid call outcome: " + value));
    }
}
