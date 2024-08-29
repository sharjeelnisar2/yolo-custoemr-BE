package com.yolo.customer.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yolo.customer.enums.Order_Status;
import com.yolo.customer.order.orderItem.OrderItem;
import com.yolo.customer.order.orderItem.OrderItemRepository;
import com.yolo.customer.order.orderStatus.OrderStatus;
import com.yolo.customer.order.orderStatus.OrderStatusRepository;
import com.yolo.customer.recipe.Recipe;
import com.yolo.customer.recipe.RecipeRepository;
import com.yolo.customer.user.User;
import com.yolo.customer.user.UserRepository;
import com.yolo.customer.address.Address;
import com.yolo.customer.address.AddressRepository;
import com.yolo.customer.userProfile.UserProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.yolo.customer.utils.GetContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final OrderItemRepository orderItemRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final AddressRepository addressRepository;

    public OrderService(OrderRepository orderRepository, OrderStatusRepository orderStatusRepository, OrderItemRepository orderItemRepository,
                        RecipeRepository recipeRepository, UserRepository userRepository,  UserProfileRepository userProfileRepository, AddressRepository addressRepository){
        this.orderRepository=orderRepository;
        this.orderStatusRepository=orderStatusRepository;
        this.orderItemRepository = orderItemRepository;
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.addressRepository = addressRepository;
    }

    public List<Order> findAll(Integer page, Integer size, String status) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = GetContextHolder.getUsernameFromAuthentication(authentication);
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

        if (orderDto == null) {
            throw new IllegalArgumentException("Order cannot be empty.");
        }

        if (orderDto.getOrderItems() == null || orderDto.getOrderItems().isEmpty()) {
            throw new IllegalArgumentException("Order items must not be empty.");
        }

        Map<String, List<OrderRequest.OrderItemDto>> itemsByChefCode = groupOrderItemsByChefCode(orderDto);

        // Create orders
        List<VendorOrderRequest.OrderDetails> vendorOrders = createOrdersAndPrepareVendorOrders(itemsByChefCode);

        boolean vendorApiSuccess = callVendorApi(vendorOrders);
        if (!vendorApiSuccess) {
            throw new RuntimeException("Vendor API call failed. ");
        }

        return true;
    }

    private Map<String, List<OrderRequest.OrderItemDto>> groupOrderItemsByChefCode(OrderRequest.OrderDto orderDto) {
        Map<String, List<OrderRequest.OrderItemDto>> itemsByChefCode = new HashMap<>();

        for (OrderRequest.OrderItemDto itemDto : orderDto.getOrderItems()) {
            Optional<Recipe> recipeOpt = recipeRepository.findById(itemDto.getRecipeId());
            if (recipeOpt.isPresent()) {
                Recipe recipe = recipeOpt.get();
                String chefCode = recipe.getChefCode();
                itemsByChefCode.computeIfAbsent(chefCode, k -> new ArrayList<>()).add(itemDto);
            } else {
                throw new EntityNotFoundException("Recipe with ID " + itemDto.getRecipeId() + " does not exist.");
            }
        }

        return itemsByChefCode;
    }


    private List<VendorOrderRequest.OrderDetails> createOrdersAndPrepareVendorOrders(Map<String, List<OrderRequest.OrderItemDto>> itemsByChefCode) {
        List<VendorOrderRequest.OrderDetails> vendorOrders = new ArrayList<>();

        for (Map.Entry<String, List<OrderRequest.OrderItemDto>> entry : itemsByChefCode.entrySet()) {
            String chefCode = entry.getKey();
            List<OrderRequest.OrderItemDto> items = entry.getValue();

            BigInteger totalPrice = calculateTotalPrice(items);

            String orderCode = generateUniqueCode();

            Order order = new Order();
            order.setCode(orderCode);
            order.setPrice(totalPrice);
            order.setOrderStatusId(1);
            order.setUserId(2);

            Order savedOrder = orderRepository.save(order);

            for (OrderRequest.OrderItemDto itemDto : items) {
                OrderItem orderItem = new OrderItem();
                orderItem.setQuantity(itemDto.getQuantity());
                orderItem.setPrice(itemDto.getPrice());
                orderItem.setRecipeId(itemDto.getRecipeId());
                orderItem.setOrderId(savedOrder.getId());
                orderItemRepository.save(orderItem);
            }

            // Prepare order details for vendor API
            VendorOrderRequest.OrderDetails orderDetails = prepareVendorOrderDetails(orderCode, totalPrice, items);
            vendorOrders.add(orderDetails);
        }

        return vendorOrders;
    }

    private VendorOrderRequest.OrderDetails prepareVendorOrderDetails(String orderCode, BigInteger totalPrice, List<OrderRequest.OrderItemDto> items) {
        VendorOrderRequest.OrderDetails orderDetails = new VendorOrderRequest.OrderDetails();
        orderDetails.setTotalPrice(totalPrice);
        orderDetails.setCurrencyCode("USD"); // Or use orderDto.getCurrencyCode() if available
        orderDetails.setOrderCode(orderCode);
        orderDetails.setCustomerContactNumber("+1234567890"); // This should be fetched from userProfile if needed


        int userId = 1;
        Address address = addressRepository.findById(userId).orElseThrow(() ->
                new RuntimeException("Address not found for userId: " + userId));

        VendorOrderRequest.OrderDetails.Address vendorAddress = new VendorOrderRequest.OrderDetails.Address();
        vendorAddress.setHouse(address.getHouse());
        vendorAddress.setStreet(address.getStreet());
        vendorAddress.setArea(address.getArea());
        vendorAddress.setZipCode(address.getZipCode());
        vendorAddress.setCity(address.getCity());
        vendorAddress.setCountry(address.getCountry());

        orderDetails.setAddress(vendorAddress);

        List<VendorOrderRequest.OrderDetails.OrderItem> vendorOrderItems = items.stream()
                .map(item -> {
                    VendorOrderRequest.OrderDetails.OrderItem vendorItem = new VendorOrderRequest.OrderDetails.OrderItem();
                    vendorItem.setQuantity(item.getQuantity());
                    vendorItem.setPrice(item.getPrice());
                    vendorItem.setRecipeCode(String.valueOf(item.getRecipeId()));
                    return vendorItem;
                })
                .collect(Collectors.toList());

        orderDetails.setOrderItems(vendorOrderItems);

        return orderDetails;
    }

    private boolean callVendorApi(List<VendorOrderRequest.OrderDetails> orders) {
        ObjectMapper objectMapper = new ObjectMapper();

        for (VendorOrderRequest.OrderDetails orderDetails : orders) {
            try {
                String requestBody = objectMapper
                        .writerWithDefaultPrettyPrinter()
                        .writeValueAsString(orderDetails);
                System.out.println("Vendor API Request body: " + requestBody);

                // Simulate sending the request to the vendor API

            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    private BigInteger calculateTotalPrice(List<OrderRequest.OrderItemDto> items) {
        return items.stream()
                .map(OrderRequest.OrderItemDto::getPrice)
                .reduce(BigInteger.ZERO, BigInteger::add);
    }


    private String generateUniqueCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

}