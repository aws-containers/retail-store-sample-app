package com.amazon.sample.orders.web.payload;

import com.amazon.sample.orders.entities.ShippingAddressEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShippingAddressMapper {
  ShippingAddress toShippingAddress(ShippingAddressEntity entity);

  ShippingAddressEntity toShippingAddressEntity(ShippingAddress item);
}
