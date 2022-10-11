package com.amazon.sample.ui.services.catalog.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ProductPage {
    private int page;

    private int size;

    private int totalRecords;

    private List<Product> products;

    public int getTotalPages() {
        return (totalRecords + size - 1) / size;
    }
}
