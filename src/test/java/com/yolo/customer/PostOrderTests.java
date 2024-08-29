package com.yolo.customer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yolo.customer.order.OrderController;
import com.yolo.customer.order.OrderRequest;
import com.yolo.customer.order.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

@SpringBootTest
@AutoConfigureMockMvc
public class PostOrderTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService; // Replace with your service class

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testPlaceOrder_Success() throws Exception {
        // Given
        OrderRequest orderRequest = new OrderRequest();
        OrderRequest.OrderDto orderDto = new OrderRequest.OrderDto();
        orderDto.setTotalPrice(19);

        // Mock Order Items
        OrderRequest.OrderItemDto orderItemDto = new OrderRequest.OrderItemDto();
        orderItemDto.setQuantity(2);
        orderItemDto.setPrice(9);
        orderItemDto.setRecipeId(1);

        orderDto.setOrderItems(Collections.singletonList(orderItemDto));
        orderRequest.setOrder(orderDto);

        when(orderService.placeOrder(any(OrderRequest.class))).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/orders") // Replace with your endpoint URL
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk());
    }

    @Test
    public void testPlaceOrder_InvalidTotalPrice() throws Exception {
        // Given
        OrderRequest orderRequest = new OrderRequest();
        OrderRequest.OrderDto orderDto = new OrderRequest.OrderDto();
        orderDto.setTotalPrice(-1); // Invalid total price

        orderRequest.setOrder(orderDto);

        // When & Then
        mockMvc.perform(post("/api/orders") // Replace with your endpoint URL
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPlaceOrder_EmptyOrderItems() throws Exception {
        // Given
        OrderRequest orderRequest = new OrderRequest();
        OrderRequest.OrderDto orderDto = new OrderRequest.OrderDto();
        orderDto.setTotalPrice(19);
        orderDto.setOrderItems(Collections.emptyList()); // Empty order items

        orderRequest.setOrder(orderDto);

        // When & Then
        mockMvc.perform(post("/api/orders") // Replace with your endpoint URL
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest());
    }
}
