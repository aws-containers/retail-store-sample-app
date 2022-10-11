package com.amazon.sample.ui.web.util;

import com.amazon.sample.ui.clients.catalog.model.ModelCatalogSizeResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageInfo {
    private int page;

    private int size;

    private int totalRecords;

    public int getTotalPages() {
        return (totalRecords + size - 1) / size;
    }
}
