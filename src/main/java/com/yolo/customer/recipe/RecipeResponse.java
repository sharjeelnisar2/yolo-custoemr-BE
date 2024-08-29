package com.yolo.customer.recipe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeResponse {
    private Integer id;
    private String title;
    private String description;
    private Integer servingSize;
    private BigInteger price;
    private String currencyCode;
    private String recipeCode;
    private String ideaCode;
    private LocalDateTime createdAt;
    private List<String> url;
}
