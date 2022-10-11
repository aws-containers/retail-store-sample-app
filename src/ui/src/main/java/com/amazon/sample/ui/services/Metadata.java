package com.amazon.sample.ui.services;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Metadata {
    private String environmentType;

    private Map<String, String> attributes = new HashMap<>();

    public Metadata(String environmentType) {
        this.environmentType = environmentType;
    }

    public Metadata add(String name, String value) {
        this.attributes.put(name, value);

        return this;
    }
}
