package com.yolo.customer.recipe.recipeImage;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecipeImageRepository extends JpaRepository<RecipeImage, Integer> {
   List<RecipeImage> findAllByRecipeId(Integer recipeId);
    Optional<RecipeImage> findById(Long id);
}
