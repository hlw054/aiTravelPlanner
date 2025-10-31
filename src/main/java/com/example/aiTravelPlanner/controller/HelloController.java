package com.example.aiTravelPlanner.controller;

import com.example.aiTravelPlanner.util.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试权限访问的控制器
 * 提供简单的接口用于验证权限控制是否正常工作
 */
@RestController
@RequestMapping("/api")
public class HelloController {

    /**
     * 测试权限访问的接口
     * 此接口受到Sa-Token权限控制，需要有效的token才能访问
     * 
     * @return 包含问候信息的Result对象
     */
    @GetMapping("/hello")
    public Result<?> hello() {
        return Result.success("Hello! 您已成功访问受保护的接口");
    }
}