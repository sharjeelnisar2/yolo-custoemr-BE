package com.yolo.customer.order;

import com.yolo.customer.enums.OrderStatusEnum;
import com.yolo.customer.order.orderStatus.OrderStatus;
import com.yolo.customer.order.orderStatus.OrderStatusRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;

    public OrderService(OrderRepository orderRepository, OrderStatusRepository orderStatusRepository) {
        this.orderRepository = orderRepository;
        this.orderStatusRepository = orderStatusRepository;
    }

    public List<Order> findAll(Integer page, Integer size, String status) {
        if (page < 0) {
            throw new IllegalArgumentException("Page index must not be less than zero.");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Page size must be greater than zero.");
        }
        if (size > 1000) {
            size = 1000;
        }

        Pageable paging = PageRequest.of(page, size);
        Page<Order> pageOrders;

        if (status == null || status.isEmpty()) {
            pageOrders = orderRepository.findAll(paging);
        } else {
            OrderStatusEnum orderStatusEnum;
            try {
                orderStatusEnum = OrderStatusEnum.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid order status: " + status);
            }
            System.out.println(orderStatusEnum);
            OrderStatus statusObj = orderStatusRepository.findByCode(orderStatusEnum.toString());
            if (statusObj == null) {
                throw new EntityNotFoundException("No status found for: " + status);
            }
            pageOrders = orderRepository.findByOrderStatusId(statusObj.getId(), paging);
        }

        if (pageOrders.isEmpty()) {
            throw new EntityNotFoundException("No orders found with the given criteria.");
        }
        return pageOrders.getContent();
    }

    public void updateOrderStatus(String orderCode, String statusString) {
        Order order = orderRepository.findByCode(orderCode);
        if (order == null) {
            throw new EntityNotFoundException("Order not found with code: " + orderCode);
        }

        OrderStatusEnum orderStatusEnum;
        try {
            orderStatusEnum = OrderStatusEnum.valueOf(statusString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid order status: " + statusString);
        }

        OrderStatus orderStatus = orderStatusRepository.findByCode(orderStatusEnum.toString());
        if (orderStatus == null) {
            throw new EntityNotFoundException("No status found for: " + orderStatusEnum);
        }

        order.setOrderStatusId(orderStatus.getId());
        orderRepository.save(order);
    }
}
