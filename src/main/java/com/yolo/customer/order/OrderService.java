package com.yolo.customer.order;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository){
        this.orderRepository=orderRepository;
    }

    public List<Order> findAll(Integer page, Integer size, String status){
        if (page < 0) {
            page = 0;
        }
        if (size <= 0) {
            size = 10;
        }
        if (size > 1000) {
            size = 1000;
        }
       // return orderRepository.findAll(PageRequest.of(page, size)).getContent();
        return  orderRepository.findAll();
    }
}
