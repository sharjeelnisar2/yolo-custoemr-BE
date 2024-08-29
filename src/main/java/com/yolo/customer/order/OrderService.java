package com.yolo.customer.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yolo.customer.enums.Order_Status;
import com.yolo.customer.order.orderItem.OrderItem;
import com.yolo.customer.order.orderItem.OrderItemRepository;
import com.yolo.customer.order.orderStatus.OrderStatus;
import com.yolo.customer.order.orderStatus.OrderStatusRepository;
import com.yolo.customer.recipe.RecipeRepository;
import com.yolo.customer.user.User;
import com.yolo.customer.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
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
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, OrderStatusRepository orderStatusRepository, OrderItemRepository orderItemRepository, RecipeRepository recipeRepository, UserRepository userRepository){
        this.orderRepository=orderRepository;
        this.orderStatusRepository=orderStatusRepository;
        this.orderItemRepository = orderItemRepository;
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
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

    @Transactional
    public boolean placeOrder(OrderRequest orderRequest) {
        OrderRequest.OrderDto orderDto = orderRequest.getOrder();
        System.out.println("OrderDto: " + orderDto);

        if (orderDto == null) {
            throw new IllegalArgumentException("Order cannot be null.");
        }

        if (orderDto.getOrderItems() == null || orderDto.getOrderItems().isEmpty()) {
            throw new IllegalArgumentException("Order items must not be null or empty.");
        }

        if (orderDto.getTotalPrice().compareTo(BigInteger.ZERO) < 0) {
            throw new IllegalArgumentException("Total price must not be less than 0.");
        }

        for (OrderRequest.OrderItemDto itemDto : orderDto.getOrderItems()) {
            if (itemDto.getQuantity() < 1) {
                throw new IllegalArgumentException("Quantity must not be less than 1");
            }
            if (itemDto.getPrice().compareTo(BigInteger.ZERO) < 0) {
                throw new IllegalArgumentException("Price must not be less than 0");
            }
        }

        String orderCode = generateUniqueCode();

        BigInteger totalPrice = orderDto.getTotalPrice();

        Order order = new Order();
        order.setCode(orderCode);
        order.setPrice(totalPrice);
        order.setOrderStatusId(1);
        order.setUserId(1);

        Order savedOrder = orderRepository.save(order);
        System.out.println("Saved Order: " + savedOrder);

        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderRequest.OrderItemDto itemDto : orderDto.getOrderItems()) {
            if (recipeRepository.existsById(itemDto.getRecipeId())) {
                OrderItem orderItem = new OrderItem();
                orderItem.setQuantity(itemDto.getQuantity());
                orderItem.setPrice(itemDto.getPrice());
                orderItem.setRecipeId(itemDto.getRecipeId());
                orderItem.setOrderId(order.getId());
                OrderItem savedOrderItem = orderItemRepository.save(orderItem);
                System.out.println("Saved OrderItem: " + savedOrderItem);
                orderItems.add(orderItem);
            } else {
                throw new EntityNotFoundException("Recipe with ID " + itemDto.getRecipeId() + " does not exist.");
            }
        }

        System.out.println("Order Items: " + orderItems);

        // Call the vendor API with the generated order code and order items
        boolean vendorApiSuccess = callVendorApi(orderCode, totalPrice, orderItems);

        if (!vendorApiSuccess) {
            // Throw an exception to trigger transaction rollback
            throw new RuntimeException("Vendor API call failed. Rolling back transaction.");
        }

        return true;
    }


    private boolean callVendorApi(String orderCode, BigInteger totalPrice, List<OrderItem> orderItems) {
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