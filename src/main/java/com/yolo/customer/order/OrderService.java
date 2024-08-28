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
        // Dummy data for customer details
        String customerContactNumber = "+1234567890";
        VendorOrderRequest.Address address = new VendorOrderRequest.Address();
        address.setHouse("123");
        address.setStreet(456);
        address.setArea("Downtown");
        address.setZipCode("string");
        address.setCity("Metropolis");
        address.setCountry("US");

        VendorOrderRequest.OrderDetails orderDetails = new VendorOrderRequest.OrderDetails();
        orderDetails.setTotalPrice(orderRequest.getOrder().getTotalPrice());
        orderDetails.setCurrencyCode(orderRequest.getOrder().getCurrencyCode());
        orderDetails.setOrderCode("ORD00001"); // You can generate or fetch this dynamically
        orderDetails.setCustomerContactNumber(customerContactNumber);
        orderDetails.setAddress(address);

        List<VendorOrderRequest.OrderDetails.OrderItem> orderItems = orderRequest.getOrder().getOrderItems().stream()
                .map(item -> {
                    VendorOrderRequest.OrderDetails.OrderItem orderItem = new VendorOrderRequest.OrderDetails.OrderItem();
                    orderItem.setQuantity(item.getQuantity());
                    orderItem.setPrice(item.getPrice());
                    orderItem.setRecipeCode(String.valueOf(item.getRecipeId())); // Assuming recipeCode is a string
                    return orderItem;
                })
                .collect(Collectors.toList());

        orderDetails.setOrderItems(orderItems);

        VendorOrderRequest vendorOrderRequest = new VendorOrderRequest();
        vendorOrderRequest.setOrder(orderDetails);

        // Prepare HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<VendorOrderRequest> requestEntity = new HttpEntity<>(vendorOrderRequest, headers);

        // Initialize RestTemplate
        RestTemplate restTemplate = new RestTemplate();
        String vendorApiUrl = "http://vendor-api-url.com/orders"; // Replace with the actual URL

        try {
            ResponseEntity<String> response = restTemplate.exchange(vendorApiUrl, HttpMethod.POST, requestEntity, String.class);
            // Handle the response from the vendor API
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            // Handle the exception
            e.printStackTrace();
            return false;
        }
    }

    private String generateUniqueCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
