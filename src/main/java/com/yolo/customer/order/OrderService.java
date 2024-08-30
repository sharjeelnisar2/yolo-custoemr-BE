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
import com.yolo.customer.utils.GetContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public List<Order> findAll(Integer page, Integer size, String status) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = GetContextHolder.getUsernameFromAuthentication(authentication);

        User loggedInUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User with given username does not exists: " + username));

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

            OrderStatus statusObj = orderStatusRepository.findByCode(orderStatus.toString())
                    .orElseThrow(() -> new EntityNotFoundException("No status found for: " + status));

            pageOrders = orderRepository.findByOrderStatusIdAndUserIdOrderByCreatedAtDesc(statusObj.getId(), userId ,paging);
        }
        return pageOrders.getContent();
    }

    public void updateOrderStatus(String orderCode, String status) {

        Order order = orderRepository.findByCode(orderCode)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with code: " + orderCode));

        Order_Status orderStatusEnum;
        try {
            orderStatusEnum = Order_Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid order status: " + status);
        }

        OrderStatus orderStatus = orderStatusRepository.findByCode(orderStatusEnum.toString())
                .orElseThrow(() -> new EntityNotFoundException("No status found for: " + status));

        order.setOrderStatusId(orderStatus.getId());
        orderRepository.save(order);
    }
}
