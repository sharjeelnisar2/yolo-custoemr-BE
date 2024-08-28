package com.yolo.customer.order.orderItem;

import com.yolo.customer.recipe.Recipe;
import com.yolo.customer.recipe.RecipeRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final RecipeRepository recipeRepository;

    public OrderItemService(OrderItemRepository orderItemRepository, RecipeRepository recipeRepository) {
        this.orderItemRepository = orderItemRepository;
        this.recipeRepository = recipeRepository;
    }

    public List<Map<String, Object>> getOrderItemsWithRecipeByOrderId(Integer orderId) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        return orderItems.stream().map(this::mapToOrderItemWithRecipe).toList();
    }

    private Map<String, Object> mapToOrderItemWithRecipe(OrderItem orderItem) {
        Recipe recipe = recipeRepository.findById(orderItem.getRecipeId())
                .orElseThrow(() -> new RuntimeException("Recipe not found with ID: " + orderItem.getRecipeId()));

        Map<String, Object> orderItemWithRecipe = new HashMap<>();
        orderItemWithRecipe.put("orderItem", orderItem);
        orderItemWithRecipe.put("recipe", recipe);

        return orderItemWithRecipe;
    }
}
