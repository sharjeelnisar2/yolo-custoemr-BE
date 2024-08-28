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
    private String code;
    private List<String> url;
}
