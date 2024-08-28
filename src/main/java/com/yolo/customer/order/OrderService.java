package com.yolo.customer.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

//    @Transactional
//    public boolean placeOrder(OrderRequest orderRequest) {
//        OrderRequest.OrderDto orderDto = orderRequest.getOrder();
//
//        if (orderDto.getTotalPrice() < 0) {
//            throw new IllegalArgumentException("Total price must not be less than 0.");
//        }
//
//        for (OrderRequest.OrderItemDto itemDto : orderDto.getOrderItems()) {
//            if (itemDto.getQuantity() < 1) {
//                throw new IllegalArgumentException("Quantity must not be less than 1" );
//            }
//            if (itemDto.getPrice() < 0) {
//                throw new IllegalArgumentException("Price must not be less than 0" );
//            }
//        }
//
//        Order order = new Order();
//        order.setCode(generateUniqueCode());
//        order.setPrice(orderDto.getTotalPrice());
//        order.setOrderStatusId(1);
//
//        order.setUserId(1); // Hardcoded user ID for now
//
//        orderRepository.save(order);
//
//        for (OrderRequest.OrderItemDto itemDto : orderDto.getOrderItems()) {
//
//            if (recipeRepository.existsById(itemDto.getRecipeId())) {
//                OrderItem orderItem = new OrderItem();
//                orderItem.setQuantity(itemDto.getQuantity());
//                orderItem.setPrice(itemDto.getPrice());
//                orderItem.setRecipeId(itemDto.getRecipeId());
//                orderItem.setOrderId(order.getId());
//                orderItemRepository.save(orderItem);
//            } else {
//                throw new EntityNotFoundException("Recipe with ID " + itemDto.getRecipeId() + " does not exist.");
//            }
//        }
//
//        return true;
//    }

    @Transactional
    public boolean placeOrder(OrderRequest orderRequest) {
        OrderRequest.OrderDto orderDto = orderRequest.getOrder();

        if (orderDto.getTotalPrice() < 0) {
            throw new IllegalArgumentException("Total price must not be less than 0.");
        }

        for (OrderRequest.OrderItemDto itemDto : orderDto.getOrderItems()) {
            if (itemDto.getQuantity() < 1) {
                throw new IllegalArgumentException("Quantity must not be less than 1");
            }
            if (itemDto.getPrice() < 0) {
                throw new IllegalArgumentException("Price must not be less than 0");
            }
        }

        String orderCode = generateUniqueCode();
        Long totalPrice = orderDto.getTotalPrice();

        Order order = new Order();
        order.setCode(orderCode);
        order.setPrice(totalPrice);
        order.setOrderStatusId(1);
        order.setUserId(1);

        orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderRequest.OrderItemDto itemDto : orderDto.getOrderItems()) {
            if (recipeRepository.existsById(itemDto.getRecipeId())) {
                OrderItem orderItem = new OrderItem();
                orderItem.setQuantity(itemDto.getQuantity());
                orderItem.setPrice(itemDto.getPrice());
                orderItem.setRecipeId(itemDto.getRecipeId());
                orderItem.setOrderId(order.getId());
                orderItemRepository.save(orderItem);
                orderItems.add(orderItem);
            } else {
                throw new EntityNotFoundException("Recipe with ID " + itemDto.getRecipeId() + " does not exist.");
            }
        }

        // Call the vendor API with the generated order code and order items
        boolean vendorApiSuccess = callVendorApi(orderCode, totalPrice, orderItems);

        if (!vendorApiSuccess) {
            // Throw an exception to trigger transaction rollback
            throw new RuntimeException("Vendor API call failed. Rolling back transaction.");
        }

        return true;
    }

    private boolean callVendorApi(String orderCode, Long totalPrice, List<OrderItem> orderItems) {
        // Dummy data for customer details
        String customerContactNumber = "+1234567890";
        VendorOrderRequest.OrderDetails.Address address = new VendorOrderRequest.OrderDetails.Address();
        address.setHouse("123");
        address.setStreet(456);
        address.setArea("Downtown");
        address.setZipCode("string");
        address.setCity("Metropolis");
        address.setCountry("US");

        VendorOrderRequest.OrderDetails orderDetails = new VendorOrderRequest.OrderDetails();
        orderDetails.setTotalPrice(totalPrice);
        orderDetails.setCurrencyCode("USD");
        orderDetails.setOrderCode(orderCode);
        orderDetails.setCustomerContactNumber(customerContactNumber);
        orderDetails.setAddress(address);

        List<VendorOrderRequest.OrderDetails.OrderItem> vendorOrderItems = orderItems.stream()
                .map(item -> {
                    VendorOrderRequest.OrderDetails.OrderItem vendorItem = new VendorOrderRequest.OrderDetails.OrderItem();
                    vendorItem.setQuantity(item.getQuantity());
                    vendorItem.setPrice(item.getPrice());
                    vendorItem.setRecipeCode(String.valueOf(item.getRecipeId()));
                    return vendorItem;
                })
                .collect(Collectors.toList());

        orderDetails.setOrderItems(vendorOrderItems);

        VendorOrderRequest vendorOrderRequest = new VendorOrderRequest();
        vendorOrderRequest.setOrder(orderDetails);

        try {
            String requestBody = new ObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(vendorOrderRequest);
            System.out.println("Vendor API Request body: " + requestBody);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false; // Simulate failure if request processing fails
        }

        // Simulate success
        return true;
    }



    private String generateUniqueCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
