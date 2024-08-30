package com.yolo.customer.recipe;

import com.yolo.customer.idea.Idea;
import com.yolo.customer.idea.IdeaRepository;
import com.yolo.customer.recipe.recipeImage.RecipeImage;
import com.yolo.customer.recipe.recipeImage.RecipeImageRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeImageRepository recipeImageRepository;
    private final IdeaRepository ideaRepository;

    public RecipeService(RecipeRepository recipeRepository, RecipeImageRepository recipeImageRepository, IdeaRepository ideaRepository) {
        this.recipeRepository = recipeRepository;
        this.recipeImageRepository = recipeImageRepository;
        this.ideaRepository = ideaRepository;
    }

    @Transactional
    public List<RecipeResponse> findAll(Integer page, Integer size, Integer ideaId, String search) {
        if (page < 0) {
            throw new IllegalArgumentException("Page index must not be less than zero.");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Page size must be greater than zero.");
        }
        if (size > 1000) {
            size = 1000;
        }

        Pageable paging = PageRequest.of(page, size);
        Page<Recipe> pageRecipes;

        if ((ideaId == null || ideaId == 0) && (search == null || search.isEmpty())) {
            pageRecipes = recipeRepository.findAll(paging);
        } else if (ideaId != null && ideaId > 0) {
            pageRecipes = recipeRepository.findByIdeaId(ideaId, paging);
        } else {
            pageRecipes = recipeRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search, search, paging);
        }

        if (pageRecipes.isEmpty()) {
            throw new EntityNotFoundException("No recipes found with the given criteria.");
        }

        return pageRecipes.getContent().stream()
                .map(this::mapToRecipeResponse)
                .collect(Collectors.toList());
    }

    private RecipeResponse mapToRecipeResponse(Recipe recipe) {
        List<String> imageUrls = recipeImageRepository.findAllByRecipeId(recipe.getId())
                .stream()
                .map(RecipeImage::getUrl)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Idea idea = ideaRepository.findById(recipe.getIdeaId())
                .orElseThrow(() -> new EntityNotFoundException("Idea not found"));

        return new RecipeResponse(
                recipe.getId(),
                recipe.getName(),
                recipe.getDescription(),
                recipe.getServingSize(),
                recipe.getPrice(),
                idea.getCode(),
                recipe.getCode(),
                "USD",
                recipe.getChefCode(),
                recipe.getChefName(),
                recipe.getCreatedAt(),
                imageUrls
        );
    }


    @Transactional
    public Recipe createRecipe(RecipeRequest  newRecipe) throws EntityNotFoundException {

        Idea idea = ideaRepository.findByCode(newRecipe.getIdea_code());
        if (idea == null) {
            throw new EntityNotFoundException("Idea not found for code: " + newRecipe.getIdea_code());
        }
        Integer ideaId = idea.getId();

        Recipe recipe = new Recipe();
        recipe.setName(newRecipe.getRecipe_name());
        recipe.setDescription(newRecipe.getDescription());
        recipe.setServingSize(newRecipe.getServing_size());
        recipe.setPrice(newRecipe.getPrice());
        recipe.setCode(newRecipe.getRecipe_code());
        recipe.setChefCode(newRecipe.getChef_code());
        recipe.setChefName(newRecipe.getChef_name());
        recipe.setIdeaId(ideaId);
        recipe.setCurrencyId(1);

        Recipe createdRecipe = recipeRepository.save(recipe);

        List<String> imageUrls = newRecipe.getImages();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            for (String imageUrl : imageUrls) {
                RecipeImage recipeImage = new RecipeImage();
                recipeImage.setUrl(imageUrl);
                recipeImage.setRecipeId(createdRecipe.getId());
                recipeImageRepository.save(recipeImage);
            }
        }
        return recipeRepository.save(recipe);
    }

}
