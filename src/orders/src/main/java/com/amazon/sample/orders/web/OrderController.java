package com.amazon.sample.orders.web;

import com.amazon.sample.orders.services.OrderService;
import com.amazon.sample.orders.web.payload.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
@Tag(name="orders")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService service;

    @Autowired
    private OrderMapper orderMapper;

    @PostMapping
    @Operation(summary = "Create an order", operationId = "createOrder")
    public ExistingOrder order(@RequestBody Order orderRequest) {
        return this.orderMapper.toExistingOrder(this.service.create(this.orderMapper.toOrderEntity(orderRequest)));
    }

    @GetMapping
    @Operation(summary = "List orders", operationId = "listOrders")
    public List<ExistingOrder> order() {
        return this.service.list().stream().map(this.orderMapper::toExistingOrder).collect(Collectors.toList());
    }
}
