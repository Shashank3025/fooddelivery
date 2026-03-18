package com.fooddelivery.order.controller;

import java.util.List;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fooddelivery.order.entity.Order;
import com.fooddelivery.order.service.OrderCommandService;
import com.fooddelivery.order.service.OrderQueryService;


import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

	private final OrderCommandService commandService;
    private final OrderQueryService queryService;

    @PostMapping
    public Order createOrder(@RequestBody Order order){
        return commandService.createOrder(order);
    }

    @GetMapping("/user/{userId}")
    public List<Order> getUserOrders(@PathVariable Long userId){
        return queryService.getUserOrders(userId);
    }

}