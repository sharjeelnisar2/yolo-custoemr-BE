package com.yolo.customer.AI;

import com.yolo.customer.utils.ErrorResponse;
import com.yolo.customer.utils.ResponseObject;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@CrossOrigin
@RestController
public class AIController {

    private final AnthropicChatModel chatModel;
    private final AIService aiService;

    @Autowired
    public AIController(AnthropicChatModel chatModel, AIService aiService) {
        this.chatModel = chatModel;
        this.aiService = aiService;
    }

    @PostMapping("/ai/generate")
    public ResponseEntity<?> generate(@RequestBody AIRequest requestBody) {
        try {
            String interests = String.join(", ", requestBody.getInterests());
            String dietaryRestrictions = String.join(", ", requestBody.getDietaryRestrictions());

            // Check the prompt processing limit for the user
            aiService.processPrompt(interests, dietaryRestrictions);

            // Build the AI prompt
            String prompt = PromptBuilder.buildPrompt(requestBody);

            // Call the AI model with the prompt
            String result = chatModel.call(prompt);

            String title = "";
            String description = "";

            // Regular expression to extract the title and description from the response
            Pattern pattern = Pattern.compile("Title:\\s*\"(.*?)\"\\s*Description:\\s*(.*)", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(result);

            if (matcher.find()) {
                title = matcher.group(1).trim(); // Extract the title
                description = matcher.group(2).trim(); // Extract the description
            }

            return ResponseEntity.ok(new ResponseObject<>(true, "idea", Map.of(
                    "title", title,
                    "description", description
            )));
        } catch (IllegalArgumentException e) {
            log.warn("Illegal argument: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ErrorResponse.create(HttpStatus.BAD_REQUEST, "Bad Request", e.getMessage()));
        } catch (Exception ex) {
            log.error("Internal server error: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse.create(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage()));
        }
    }

    @GetMapping("/ai/max-limit")
    public ResponseEntity<?> getMaxLimit() {
        try {
            int maxLimit = aiService.getMaxLimit();
            return ResponseEntity.ok(new ResponseObject<>(true, "maxLimit", maxLimit));
        } catch (Exception ex) {
            log.error("Internal server error: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse.create(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage()));
        }
    }
}
