package com.example.aiTravelPlanner.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import com.example.aiTravelPlanner.util.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SaToken路由拦截器注册配置类
 */
@Slf4j
@Configuration
public class SaTokenConfig {
    /**
     * 注册 [Sa-Token全局过滤器]
     */
    @Bean
    public SaServletFilter getSaServletFilter() {
        return new SaServletFilter()
                // 指定 拦截路由 与 放行路由
                .addInclude("/api/**")
                // 认证函数: 每次请求执行
                .setAuth(obj -> {
                    // 登录认证 -- 拦截所有/api/路径的请求，并排除登录、注册和登出接口
                    SaRouter.match("/api/**")
                            .notMatch("/api/users/login")
                            .notMatch("/api/users/register")
                            .notMatch("/api/users/logout")
                            .check(StpUtil::checkLogin);
                })
                // 返回异常结果
                .setError(e -> {
                    log.error("Token验证失败: {}", e.getMessage());
                    // 设置响应头
                    SaHolder.getResponse().setHeader("Content-Type", "application/json;charset=UTF-8");
                    try {
                        // 使用Jackson将Result对象转换为JSON字符串
                        ObjectMapper mapper = new ObjectMapper();
                        return mapper.writeValueAsString(Result.error(401, "token验证失败，请重新登录"));
                    } catch (Exception ex) {
                        return "{\"code\":401,\"msg\":\"token验证失败\",\"data\":null}";
                    }
                })
                // 前置函数：在每次认证函数之前执行
                .setBeforeAuth(obj -> {
                    SaHolder.getResponse()
                            // ---------- 设置跨域响应头 ----------
                            // 允许指定域访问跨域资源
                            .setHeader("Access-Control-Allow-Origin", SaHolder.getRequest().getHeader("Origin"))
                            // 允许所有请求方式
                            .setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS")
                            // 允许的header参数
                            .setHeader("Access-Control-Allow-Headers", "Content-Type,x-requested-with,satoken")
                            // 允许跨域携带cookies
                            .setHeader("Access-Control-Allow-Credentials", "true")
                            // 有效时间
                            .setHeader("Access-Control-Max-Age", "3600");

                    // 如果是预检请求，则立即返回到前端
                    SaRouter.match(SaHttpMethod.OPTIONS)
                            .free(r -> log.info("--------OPTIONS预检请求，不做处理"))
                            .back();
                });
    }
}