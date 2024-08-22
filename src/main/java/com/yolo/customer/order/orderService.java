package com.yolo.customer.order;

import org.springframework.stereotype.Service;

@Service
public class orderService {

    private final orderRepository OrderRepository;

    public orderService(orderRepository OrderRepository){
        this.OrderRepository=OrderRepository;
    }
}
