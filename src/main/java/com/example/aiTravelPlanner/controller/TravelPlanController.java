package com.example.aiTravelPlanner.controller;

import com.example.aiTravelPlanner.model.TravelPlan;
import com.example.aiTravelPlanner.model.TravelSchedule;
import com.example.aiTravelPlanner.service.TravelPlanService;
import com.example.aiTravelPlanner.util.Result;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 行程计划控制器
 */
@RestController
@RequestMapping("/api/travel-plans")
public class TravelPlanController {

    private final TravelPlanService travelPlanService;

    @Autowired
    public TravelPlanController(TravelPlanService travelPlanService) {
        this.travelPlanService = travelPlanService;
    }

    /**
     * 分页获取用户的行程计划
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 分页结果
     */
    @GetMapping("/page")
    public Result<Map<String, Object>> getTravelPlansByPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            // 获取当前登录用户ID
            Long userId = StpUtil.getLoginIdAsLong();
            
            // 执行分页查询
            Page<TravelPlan> travelPlanPage = travelPlanService.getTravelPlansByUserIdPaged(userId, page, size);
            
            // 封装返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("list", travelPlanPage.getContent());
            result.put("total", travelPlanPage.getTotalElements());
            result.put("page", page);
            result.put("size", size);
            result.put("pages", travelPlanPage.getTotalPages());
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(500, "获取行程计划失败：" + e.getMessage());
        }
    }
    
    /**
     * 通过TravelPlanId获取每日日程详细信息
     * @param travelPlanId 行程计划ID
     * @return 每日日程详细信息
     */
    @GetMapping("/{travelPlanId}/schedules")
    public Result<List<TravelSchedule>> getTravelSchedulesByPlanId(
            @PathVariable Long travelPlanId) {
        try {
            // 获取当前登录用户ID
            Long userId = StpUtil.getLoginIdAsLong();
            
            // 获取行程安排列表
            List<TravelSchedule> schedules = travelPlanService.getTravelSchedulesByPlanId(travelPlanId, userId);
            
            return Result.success(schedules);
        } catch (RuntimeException e) {
            return Result.error(404, e.getMessage());
        } catch (Exception e) {
            return Result.error(500, "获取行程安排失败：" + e.getMessage());
        }
    }
    
    /**
     * 通过TravelPlanId删除对应的TravelPlan和TravelSchedule
     * @param travelPlanId 行程计划ID
     * @return 删除结果
     */
    @DeleteMapping("/{travelPlanId}")
    public Result<Void> deleteTravelPlan(
            @PathVariable Long travelPlanId) {
        try {
            // 获取当前登录用户ID
            Long userId = StpUtil.getLoginIdAsLong();
            
            // 删除行程计划和相关行程安排
            travelPlanService.deleteTravelPlan(travelPlanId, userId);
            
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(404, e.getMessage());
        } catch (Exception e) {
            return Result.error(500, "删除行程计划失败：" + e.getMessage());
        }
    }
}