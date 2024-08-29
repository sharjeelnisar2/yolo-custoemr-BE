package com.yolo.customer.order.orderItem;

import com.yolo.customer.utils.ErrorResponse;
import com.yolo.customer.utils.ResponseObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
public class OrderItemController {

    private final OrderItemService orderItemService;
    public OrderItemController(OrderItemService orderItemService){
        this.orderItemService = orderItemService;
    }

    //add pre-auth for vendor
//    @GetMapping("/users/orders/{id}/orderitems")
//    public ResponseEntity<?> getOrderItemList(@PathVariable("id") Integer orderID) {
//        try {
//            List<Map<String, Object>> orderItemsWithRecipes = orderItemService.getOrderItemsWithRecipeByOrderId(orderID);
//            if (orderItemsWithRecipes.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(ErrorResponse.create(HttpStatus.NOT_FOUND, "No Order Items Found", "No order items found for the provided order ID."));
//            }
//            return ResponseEntity.ok(new ResponseObject<>(true, "orderItems", orderItemsWithRecipes));
//        } catch (Exception ex) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(ErrorResponse.create(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage()));
//        }
//    }
}
