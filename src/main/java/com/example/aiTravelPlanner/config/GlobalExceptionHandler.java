package com.example.aiTravelPlanner.config;

import com.example.aiTravelPlanner.util.Result;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.SaTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器
 * 捕获所有控制器层的异常，并返回统一格式的Result对象
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理Sa-Token认证相关异常
     * 包括未登录、token过期、token无效等认证相关错误
     */
    @ExceptionHandler(SaTokenException.class)
    public Result<?> handleSaTokenException(SaTokenException e, HttpServletRequest request) {
        log.error("认证异常: {}, 请求路径: {}", e.getMessage(), request.getRequestURI(), e);
        // 使用401错误码表示认证失败
        return Result.error(401, "认证失败，请重新登录");
    }

    /**
     * 处理未登录异常
     * 专门处理Sa-Token的NotLoginException异常
     */
    @ExceptionHandler(NotLoginException.class)
    public Result<?> handleNotLoginException(NotLoginException e, HttpServletRequest request) {
        log.error("未登录异常: {}, 请求路径: {}", e.getMessage(), request.getRequestURI(), e);
        // 使用401错误码表示未登录
        return Result.error(401, "请先登录");
    }

    /**
     * 处理业务异常（运行时异常）
     * 通常用于处理业务逻辑中的错误，如用户名已存在、数据验证失败等
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.error("业务异常: {}, 请求路径: {}", e.getMessage(), request.getRequestURI(), e);
        // 使用400错误码表示业务异常
        return Result.error(400, e.getMessage());
    }

    /**
     * 处理空指针异常
     * 专门处理空指针异常，提供更友好的错误信息
     */
    @ExceptionHandler(NullPointerException.class)
    public Result<?> handleNullPointerException(NullPointerException e, HttpServletRequest request) {
        log.error("空指针异常: 请求路径: {}", request.getRequestURI(), e);
        // 使用500错误码表示服务器内部错误
        return Result.error(500, "系统繁忙，请稍后重试");
    }

    /**
     * 处理请求路径不存在的异常
     * 当访问不存在的API路径时触发
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public Result<?> handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        log.error("请求路径不存在: {}, 请求方法: {}", request.getRequestURI(), request.getMethod(), e);
        // 使用404错误码表示资源不存在
        return Result.error(404, "请求的接口不存在");
    }

    /**
     * 处理其他所有未捕获的异常
     * 兜底异常处理，确保所有异常都能以统一格式返回
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e, HttpServletRequest request) {
        log.error("未预期的异常: 请求路径: {}", request.getRequestURI(), e);
        // 使用500错误码表示服务器内部错误
        return Result.error(500, "系统内部错误，请联系管理员");
    }
}