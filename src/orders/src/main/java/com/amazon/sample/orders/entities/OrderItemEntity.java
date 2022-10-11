package com.amazon.sample.orders.entities;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="CUSTOMER_ORDER_ITEM")
@Data
public class OrderItemEntity {
    @Embeddable
    @Data
    public static class Key implements Serializable {
        private String orderId;
        private String productId;
    }

    @EmbeddedId
    @AttributeOverrides({
        @AttributeOverride(name="productId",
          column=@Column(length=64))
    })
    private Key id;

    @Transient
    private String productId;

    private int quantity;

    private int price;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("orderId")
    private OrderEntity order;
}
