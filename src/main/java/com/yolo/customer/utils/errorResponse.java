package com.yolo.customer.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class errorResponse {
    public static ResponseEntity<Object> create(HttpStatus status, String message, String errorDetails) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", status.getReasonPhrase());
        response.put("message", message);
        response.put("details", errorDetails);
        return ResponseEntity.status(status).body(response);
    }
}
