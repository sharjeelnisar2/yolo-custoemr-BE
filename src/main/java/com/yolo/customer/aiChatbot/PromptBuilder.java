package com.yolo.customer.aiChatbot;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Component
public class PromptBuilder {

    @Value("${api.security.TITLE_LENGTH}")
    private static int titleLength;

    @Value("${api.security.DESCRIPTION_LENGTH}")
    private static int descriptionLength;

    @Value("classpath:constant/prompt.txt")
    private Resource promptTemplate;

    private static String template;

    @PostConstruct
    private void init() throws IOException {
        template = new String(Files.readAllBytes(Paths.get(promptTemplate.getURI())));
    }

    public static String buildPrompt(AIChatbotRequest requestBody) {
        List<String> interests = requestBody.getInterests();
        List<String> restrictions = requestBody.getDietaryRestrictions();

        String interestsString = String.join(", ", interests);
        String restrictionsString = String.join(", ", restrictions);

        return template
                .replace("{TITLE_LENGTH}", String.valueOf(titleLength))
                .replace("{DESCRIPTION_LENGTH}", String.valueOf(descriptionLength))
                .replace("{INTERESTS}", interestsString)
                .replace("{RESTRICTIONS}", restrictionsString);
    }
}
