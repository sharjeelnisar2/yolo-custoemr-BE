package com.yolo.customer.AI;

import com.yolo.customer.user.UserRepository;
import com.yolo.customer.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AIService {

    @Autowired
    private UserRepository userRepository;
    // In-memory store for tracking user prompts
    private final ConcurrentHashMap<Integer, List<String>> userPrompts = new ConcurrentHashMap<>();

    @Value("${api.security.max_limit}")
    public int maxLimit;

    public int processPrompt(String interests, String dietaryRestrictions) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User loggedInUser = userRepository.findByUsername(username);
//        Integer userId= loggedInUser.getId();
        String currentPrompt = generatePrompt(interests, dietaryRestrictions);

        // Retrieve existing prompts for the user
        List<String> prompts = userPrompts.getOrDefault(1, new ArrayList<>());

        // Check if the current prompt has been repeated
        long matchingPrompts = prompts.stream().filter(p -> p.equals(currentPrompt)).count();

        if (matchingPrompts >= maxLimit) {
            throw new IllegalStateException("Max limit reached for repeated prompts.");
        }

        // Add current prompt to the list of prompts for the user
        prompts.add(currentPrompt);
        userPrompts.put(1, prompts);

        // Return the remaining limit
        return maxLimit - (int) matchingPrompts;
    }

    private String generatePrompt(String interests, String dietaryRestrictions) {
        return interests + "|" + (dietaryRestrictions.isEmpty() ? "none" : dietaryRestrictions);
    }

    public int getMaxLimit() {
        return maxLimit;
    }
}

