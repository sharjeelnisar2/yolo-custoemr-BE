package com.yolo.customer.order;

import com.yolo.customer.enums.OrderStatusEnum;
import com.yolo.customer.order.orderItem.OrderItem;
import com.yolo.customer.order.orderItem.OrderItemRepository;
import com.yolo.customer.order.orderStatus.OrderStatus;
import com.yolo.customer.order.orderStatus.OrderStatusRepository;
import com.yolo.customer.recipe.RecipeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final OrderItemRepository orderItemRepository;
    private final RecipeRepository recipeRepository;

    public OrderService(OrderRepository orderRepository, OrderStatusRepository orderStatusRepository, OrderItemRepository orderItemRepository, RecipeRepository recipeRepository){
        this.orderRepository=orderRepository;
        this.orderStatusRepository=orderStatusRepository;
        this.orderItemRepository = orderItemRepository;
        this.recipeRepository = recipeRepository;
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
            OrderStatus statusObj = orderStatusRepository.findIdByCode(orderStatusEnum.toString());
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

    @Transactional
    public boolean placeOrder(OrderRequest orderRequest) {
        OrderRequest.OrderDto orderDto = orderRequest.getOrder();

        if (orderDto.getTotalPrice() < 0) {
            throw new IllegalArgumentException("Total price must not be less than 0.");
        }

        for (OrderRequest.OrderItemDto itemDto : orderDto.getOrderItems()) {
            if (itemDto.getQuantity() < 1) {
                throw new IllegalArgumentException("Quantity must not be less than 1" );
            }
            if (itemDto.getPrice() < 0) {
                throw new IllegalArgumentException("Price must not be less than 0" );
            }
        }

        Order order = new Order();
        order.setCode(generateUniqueCode());
        order.setPrice(orderDto.getTotalPrice());
        order.setOrderStatusId(1);

        order.setUserId(1); // Hardcoded user ID for now

        orderRepository.save(order);

        for (OrderRequest.OrderItemDto itemDto : orderDto.getOrderItems()) {

            if (recipeRepository.existsById(itemDto.getRecipeId())) {
                OrderItem orderItem = new OrderItem();
                orderItem.setQuantity(itemDto.getQuantity());
                orderItem.setPrice(itemDto.getPrice());
                orderItem.setRecipeId(itemDto.getRecipeId());
                orderItem.setOrderId(order.getId());
                orderItemRepository.save(orderItem);
            } else {
                throw new EntityNotFoundException("Recipe with ID " + itemDto.getRecipeId() + " does not exist.");
            }
        }

        return true;
    }

    private boolean callVendorApi(OrderRequest orderRequest) {
        // Simulating the vendor API call
        // You can add your logic here later, for now it just returns true
        return true;
    }

    private String generateUniqueCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
