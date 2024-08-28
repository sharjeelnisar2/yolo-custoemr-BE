package com.yolo.customer.AI;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AIRequest {
    private List<String> interests;
    private List<String> dietaryRestrictions;

}
