package com.example.aiTravelPlanner.model.request;

import lombok.Data;

@Data
public class UserRegistrationRequest {
    private String username;
    private String password;
    private String email;  // 可选项
    private String phone;  // 可选项
}