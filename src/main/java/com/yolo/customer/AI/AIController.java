package com.yolo.customer.AI;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CrossOrigin
@RestController
public class AIController {

    private final AnthropicChatModel chatModel;

    @Autowired
    public AIController(AnthropicChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @PostMapping("/ai/generate")
    public Map<String, Object> generate(@RequestBody AIRequest requestBody) {
        String prompt = PromptBuilder.buildPrompt(requestBody);
//        if (prompt.equals("Please enter a valid prompt related to recipe idea generation")) {
//            return Map.of("error", "Please enter a valid prompt related to recipe idea generation");
//        }

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

        return Map.of("idea", Map.of(
                "title", title,
                "description", description
        ));
    }


    @GetMapping("/ai/generateStream")
    public Flux<ChatResponse> generateStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return chatModel.stream(prompt);
    }
}