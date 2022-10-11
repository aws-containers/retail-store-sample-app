package com.amazon.sample.ui.services.catalog.model;

import com.amazon.sample.ui.clients.catalog.model.ModelProduct;
import com.amazon.sample.ui.clients.catalog.model.ModelTag;
import org.mapstruct.Mapper;

@Mapper
public interface CatalogMapper {
    Product product(ModelProduct product);

    ProductTag tag(ModelTag tag);
}