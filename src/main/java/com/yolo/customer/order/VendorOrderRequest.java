package com.yolo.customer.order;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class VendorOrderRequest {
    private OrderDetails order;

    @Getter
    @Setter
    public static class OrderDetails {
        private BigInteger totalPrice;
        private String currencyCode;
        private String orderCode;
        private String customerContactNumber;
        private Address address;
        private List<OrderItem> orderItems;

        @Getter
        @Setter
        public static class Address {
            private String house;
            private int street;
            private String area;
            private String zipCode;
            private String city;
            private String country;
        }

        @Getter
        @Setter
        public static class OrderItem {
            private int quantity;
            private BigInteger price;
            private String recipeCode;
        }
    }
}
