package com.yolo.customer.utils;

import lombok.Getter;
import lombok.Setter;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class ResponseObject<T> {
    private Boolean success;
    private Map<String, T> data;
    public ResponseObject(Boolean success, String name, T data) {
        this.success= success;
        this.data = new HashMap<>();
        this.data.put(name, data);
    }
}