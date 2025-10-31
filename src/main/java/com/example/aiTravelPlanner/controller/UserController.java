package com.example.aiTravelPlanner.controller;

import com.example.aiTravelPlanner.model.vo.LoginResponse;
import com.example.aiTravelPlanner.model.vo.UserVO;
import com.example.aiTravelPlanner.util.Result;
import com.example.aiTravelPlanner.model.request.UserLoginRequest;
import com.example.aiTravelPlanner.model.request.UserRegistrationRequest;
import com.example.aiTravelPlanner.service.UserService;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 用户注册接口
    @PostMapping("/register")
    public Result<?> register(@RequestBody UserRegistrationRequest request) {
        userService.registerUser(request);
        return Result.success("注册成功");
    }

    // 用户登录接口z
    @PostMapping("/login")
    public Result<?> login(@RequestBody UserLoginRequest request) {
        UserVO user = userService.loginUser(request);
        // 获取当前生成的token
        String token = StpUtil.getTokenValue();
        // 将token添加到返回结果中
        return Result.success(new LoginResponse(user, token));
    }
    
    // 登出接口
    @PostMapping("/logout")
    public Result<?> logout() {
        // 调用StpUtil.logout()方法进行登出
        StpUtil.logout();
        return Result.success();
    }
    
    // 获取当前登录用户信息接口
    @GetMapping("/current")
    public Result<?> getCurrentUser() {
        // 获取当前登录用户的id
        Long userId = StpUtil.getLoginIdAsLong();
        // 通过UserService获取用户信息
        UserVO userVO = userService.getUserById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));
        return Result.success(userVO);
    }
    

}