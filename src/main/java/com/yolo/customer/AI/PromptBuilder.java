package com.yolo.customer.AI;

import java.util.List;

public class PromptBuilder {

    public static String buildPrompt(AIRequest requestBody) {
        String message = requestBody.getMessage();
        List<String> interests = requestBody.getInterests();
        List<String> restrictions = requestBody.getDietaryRestrictions();

        // Join interests and restrictions into a comma-separated string
        String interestsString = String.join(", ", interests);
        String restrictionsString = String.join(", ", restrictions);

        // Building the custom prompt with interests and restrictions
        return "Generate a unique idea for the chef that includes a title and description. The title should be no more than 64 characters, and the description should be no more than 128 characters. "
                + "Use the details provided below to inform your idea: "
                + message + ". Interests: "
                + interestsString + ". Restrictions: "
                + restrictionsString + ".";
    }
}
