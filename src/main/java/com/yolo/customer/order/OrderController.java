package com.yolo.customer.order;

import com.yolo.customer.utils.ErrorResponse;
import com.yolo.customer.utils.ResponseObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
public class OrderController {

    private final OrderService OrderService;

    public OrderController(OrderService OrderService){
        this.OrderService = OrderService;
    }

    @PatchMapping("/users/orders/{order_code}")
    public ResponseEntity<?> updateOrder(@PathVariable Integer trackingID){

        return null;
    }

    @GetMapping("/users/orders")
    public ResponseEntity<?> getOrderList(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                          @RequestParam(name = "size", defaultValue = "10") Integer size,
                                          @RequestParam(name = "status", defaultValue = "10") String status){

        //status can be PLACED, INPROCESS, DISPATCHED
        try {
            List<Order> orders = OrderService.findAll(page, size, status);
            return ResponseEntity.ok(new ResponseObject<>(true,"orders", orders));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.create(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage()));
        }
    }

}
