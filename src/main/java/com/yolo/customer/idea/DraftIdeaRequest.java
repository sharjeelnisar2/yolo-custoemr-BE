package com.yolo.customer.idea;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class DraftIdeaRequest {
    private String title;
    private String description;
    private List<String> interests;
    private List<String> dietaryRestrictions;
}