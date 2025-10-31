package com.example.aiTravelPlanner.model.request;

import lombok.Data;

@Data
public class UserLoginRequest {
    private String username;
    private String password;
}