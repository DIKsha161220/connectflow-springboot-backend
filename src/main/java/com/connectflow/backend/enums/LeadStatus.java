package com.connectflow.backend.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Locale;

public enum LeadStatus {
    NEW("New"),
    CONTACTED("Contacted"),
    INTERESTED("Interested"),
    FOLLOW_UP("Follow-up"),
    CONVERTED("Converted"),
    NOT_INTERESTED("Not Interested");

    private final String value;

    LeadStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static LeadStatus fromValue(String value) {
        String normalized = value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(status -> status.value.toLowerCase(Locale.ROOT).equals(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid lead status: " + value));
    }
}
