package com.yolo.customer.recipe;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RecipeRequest {
    private String name;
    private String description;
    private Integer servingSize;
    private Long price;
    private String ideaCode;
    private String recipeCode;
    private String chefCode;
    private String chefName;
    private List<String> url;
}
