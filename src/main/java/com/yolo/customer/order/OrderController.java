package com.yolo.customer.order;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
public class OrderController {

    private final OrderService OrderService;

    public OrderController(OrderService OrderService){
        this.OrderService = OrderService;
    }

    @PatchMapping("/users/orders/{trackingid}")
    public ResponseEntity<?> updateOrder(@PathVariable Integer trackingID){


        return null;
    }

    @GetMapping("/users/orders/")
    public ResponseEntity<?> getOrderList(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                          @RequestParam(name = "size", defaultValue = "10") Integer size){


        return null;
    }

}
