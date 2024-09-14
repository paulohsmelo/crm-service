package com.paulohsmelo.crmservice.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public enum UserRole {

    ROLE_ADMIN("admin"),
    ROLE_USER("user");

    private final String description;

    @JsonCreator
    public static UserRole forValue(String value) {
        return Arrays.stream(values())
                .filter(entry -> entry.description.equals(value))
                .findFirst()
                .orElse(null);
    }

    @JsonValue
    public String toValue() {
        return description;
    }
}
