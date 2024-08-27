package com.yolo.customer.AI;

public class PromptBuilder {

    public static String buildPrompt(AIRequest requestBody) {
        String message = requestBody.getMessage();
        AIRequest.Interests interests = requestBody.getInterests();
        AIRequest.Restrictions restrictions = requestBody.getRestrictions();

        // Building the custom prompt with interests and restrictions
        return "Generate a unique idea for the chef that includes a title and description. The title should be no more than 64 characters, and the description should be no more than 128 characters. "
                + "Use the details provided below to inform your idea: "
                + message + ". Interests: "
                + interests.getInterest1() + ", "
                + interests.getInterest2() + ", "
                + interests.getInterest3() + ". Restrictions: "
                + restrictions.getRestriction1() + ", "
                + restrictions.getRestriction2() + ", "
                + restrictions.getRestriction3() + ".";
    }
}
