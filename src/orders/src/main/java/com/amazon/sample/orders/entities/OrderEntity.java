package com.amazon.sample.orders.entities;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="CUSTOMER_ORDER")
@Data
public class OrderEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    private String firstName;
    private String lastName;
    private String email;

    @OneToMany(
        mappedBy = "order",
        orphanRemoval = true,
        fetch = FetchType.EAGER
    )
    private List<OrderItemEntity> items = new ArrayList<>();
}