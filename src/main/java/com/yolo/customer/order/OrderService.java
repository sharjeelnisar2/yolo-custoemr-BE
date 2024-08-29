package com.yolo.customer.order;

import com.yolo.customer.enums.Order_Status;
import com.yolo.customer.order.orderStatus.OrderStatus;
import com.yolo.customer.order.orderStatus.OrderStatusRepository;
import com.yolo.customer.user.User;
import com.yolo.customer.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;

    public OrderService(UserRepository userRepository, OrderRepository orderRepository, OrderStatusRepository orderStatusRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderStatusRepository = orderStatusRepository;
    }

    public List<Order> findAll(Integer page, Integer size, String status, String username) {

        User loggedInUser = userRepository.findByUsername(username).get();

        if(loggedInUser == null) {
            throw new IllegalArgumentException("User with given username doesnot exists: " + username);
        }

        if (page < 0) {
            throw new IllegalArgumentException("Page index must not be less than zero.");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Page size must be greater than zero.");
        }
        if (size > 1000) {
            size = 1000;
        }

        Integer userId= loggedInUser.getId();
        Pageable paging = PageRequest.of(page, size);
        Page<Order> pageOrders;

        if (status == null || status.isEmpty()) {
            pageOrders = orderRepository. findByUserIdOrderByCreatedAtDesc(userId,paging);
        } else {
            Order_Status orderStatus;
            try {
                orderStatus = Order_Status.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid order status: " + status);
            }

            OrderStatus statusObj = orderStatusRepository.findByCode(orderStatus.toString());
            if (statusObj == null) {
                throw new EntityNotFoundException("No status found for: " + status);
            }
            pageOrders = orderRepository.findByOrderStatusIdAndUserIdOrderByCreatedAtDesc(statusObj.getId(), userId ,paging);
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

        Order_Status orderStatusEnum;
        try {
            orderStatusEnum = Order_Status.valueOf(statusString.toUpperCase());
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
