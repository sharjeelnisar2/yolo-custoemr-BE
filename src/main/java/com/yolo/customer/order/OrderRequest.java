package com.yolo.customer.order;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
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
        private List<OrderItemDto> orderItems = new ArrayList<>(); // Initialize with an empty list

        // Alternatively, you can ensure non-null list in the setter
        public void setOrderItems(List<OrderItemDto> orderItems) {
            this.orderItems = orderItems != null ? orderItems : new ArrayList<>();
        }
    }

    @Getter
    @Setter
    public static class OrderItemDto {
        private int quantity;
        private long price;
        private int recipeId;
    }
}