package com.yolo.customer.order;

import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository OrderRepository;

    public OrderService(OrderRepository OrderRepository){
        this.OrderRepository=OrderRepository;
    }
}
