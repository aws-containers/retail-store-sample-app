package com.amazon.sample.ui.services.catalog.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Product {
    private String id;

    private String name;

    private String description;

    private int count;

    private String imageUrl;

    private int price;

    private List<String> tag = null;
}
