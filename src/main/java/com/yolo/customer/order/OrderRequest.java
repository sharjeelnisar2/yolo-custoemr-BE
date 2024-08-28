package com.yolo.customer.order;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequest {

    private OrderDto order;

    @Getter
    @Setter
    public static class OrderDto {
        private long totalPrice;
        private String currencyCode;
        private List<OrderItemDto> orderItems;
    }

    @Getter
    @Setter
    public static class OrderItemDto {
        private int quantity;
        private long price;
        private int recipeId;
    }
}