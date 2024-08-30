package com.yolo.customer.idea.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class IdeaDTO {
    private IdeaDetails idea;

    public IdeaDetails getIdea() {
        return (idea == null) ? null : new IdeaDetails(idea);
    }

    public void setIdea(IdeaDetails idea) {
        this.idea = (idea == null) ? null : new IdeaDetails(idea);
    }

    @Getter
    @Setter
    public static class IdeaDetails {
        private String customerName;
        private String title;
        private String description;
        private String ideaCode;
        private List<String> interests = new ArrayList<>();
        private List<String> dietaryRestrictions = new ArrayList<>();

        public IdeaDetails(IdeaDetails other) {
            if (other != null) {
                this.customerName = other.customerName;
                this.title = other.title;
                this.description = other.description;
                this.ideaCode = other.ideaCode;
                this.interests = new ArrayList<>(other.interests);
                this.dietaryRestrictions = new ArrayList<>(other.dietaryRestrictions);
            }
        }

        public IdeaDetails() {}

        public List<String> getInterests() {
            return new ArrayList<>(interests);
        }

        public void setInterests(List<String> interests) {
            this.interests = (interests == null) ? new ArrayList<>() : new ArrayList<>(interests);
        }

        public List<String> getDietaryRestrictions() {
            return new ArrayList<>(dietaryRestrictions);
        }

        public void setDietaryRestrictions(List<String> dietaryRestrictions) {
            this.dietaryRestrictions = (dietaryRestrictions == null) ? new ArrayList<>() : new ArrayList<>(dietaryRestrictions);
        }
    }
}