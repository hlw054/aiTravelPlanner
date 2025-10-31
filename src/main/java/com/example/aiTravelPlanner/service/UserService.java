package com.example.aiTravelPlanner.service;

import com.example.aiTravelPlanner.model.User;
import com.example.aiTravelPlanner.model.vo.UserVO;
import com.example.aiTravelPlanner.repository.UserRepository;
import com.example.aiTravelPlanner.model.request.UserRegistrationRequest;
import com.example.aiTravelPlanner.model.request.UserLoginRequest;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Objects;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // 用户注册（接收请求对象）
    @Transactional
    public void registerUser(UserRegistrationRequest request) {
        registerUser(request.getUsername(), request.getPassword(), request.getEmail(), request.getPhone());
    }
    
    // 用户注册（接收分散参数）
    @Transactional
    public User registerUser(String username, String password, String email, String phone) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 仅当邮箱不为空时才检查邮箱是否已存在
        if (email != null && !email.isEmpty() && userRepository.existsByEmail(email)) {
            throw new RuntimeException("邮箱已存在");
        }
        
        // 创建新用户
        User user = new User();
        user.setUsername(username);
        // 对密码进行加密
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setPhone(phone);
        
        return userRepository.save(user);
    }

    // 将User实体转换为UserVO
    private UserVO convertToUserVO(User user) {
        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setUsername(user.getUsername());
        userVO.setEmail(user.getEmail());
        userVO.setPhone(user.getPhone());
        userVO.setCreatedAt(user.getCreatedAt());
        userVO.setUpdatedAt(user.getUpdatedAt());
        return userVO;
    }

    // 用户登录（接收请求对象）
    public UserVO loginUser(UserLoginRequest request) {
        // 调用分散参数的登录方法，该方法内部已实现StpUtil登录
        return loginUser(request.getUsername(), request.getPassword());
    }
    
    // 用户登录（接收分散参数）
    public UserVO loginUser(String username, String password) {
        // 根据用户名查找用户
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // 验证密码
            if (passwordEncoder.matches(password, user.getPasswordHash())) {
                // 使用Sa-Token进行登录，将用户id作为登录标识
                StpUtil.login(user.getId());
                
                // 获取token
                String token = StpUtil.getTokenValue();
                System.out.println("生成的token: " + token);
                
                return convertToUserVO(user);
            }
        }
        
        throw new RuntimeException("用户名或密码错误");
    }

    // 根据ID获取用户信息
    public Optional<UserVO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToUserVO);
    }
}