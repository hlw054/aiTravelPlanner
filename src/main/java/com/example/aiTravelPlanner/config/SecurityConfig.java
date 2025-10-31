package com.example.aiTravelPlanner.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF保护，避免与Sa-Token冲突
            .csrf(csrf -> csrf.disable())
            // 禁用表单登录，因为使用Sa-Token
            .formLogin(formLogin -> formLogin.disable())
            // 禁用基本认证，因为使用Sa-Token
            .httpBasic(httpBasic -> httpBasic.disable())
            // 禁用会话管理，因为使用token进行无状态认证
            .sessionManagement(session -> session.disable())
            // 允许所有请求，因为权限控制由Sa-Token统一处理
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().permitAll()
            );
        return http.build();
    }
}