package com.example.aiTravelPlanner.controller;

import com.example.aiTravelPlanner.model.TravelPlan;
import com.example.aiTravelPlanner.service.BaiLianService;
import com.example.aiTravelPlanner.service.TravelPlanParser;
import com.example.aiTravelPlanner.service.TravelPlanService;
import com.example.aiTravelPlanner.util.Result;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.aiTravelPlanner.model.vo.ScheduleVO;
import com.example.aiTravelPlanner.model.vo.TravelPlanResponseVO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AIGeneratedTravelPlanController {

    private final BaiLianService baiLianService;
    private final TravelPlanParser travelPlanParser;
    private final TravelPlanService travelPlanService;

    @Autowired
    public AIGeneratedTravelPlanController(BaiLianService baiLianService, 
                                         TravelPlanParser travelPlanParser, 
                                         TravelPlanService travelPlanService) {
        this.baiLianService = baiLianService;
        this.travelPlanParser = travelPlanParser;
        this.travelPlanService = travelPlanService;
    }

    /**
     * 生成AI行程计划
     * @param userRequest 用户的旅游要求
     * @return 生成的行程计划信息
     */
    @PostMapping("/generate-travel-plan")
    public Result<?> generateTravelPlan(@RequestBody String userRequest) {
        // 获取当前登录用户ID（需要从Sa-Token中获取）
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 检查用户请求是否为空
        if (userRequest == null || userRequest.trim().isEmpty()) {
            throw new RuntimeException("旅游要求不能为空");
        }
        
        // 调用阿里云百炼API生成行程计划
        String aiResponse = baiLianService.generateTravelPlan(userRequest);
        
        // 解析AI返回的行程计划
        TravelPlanParser.ParsedTravelPlan parsedPlan = travelPlanParser.parseTravelPlan(aiResponse);
        
        // 保存行程计划到数据库
        TravelPlan savedPlan = travelPlanService.saveTravelPlan(userId, parsedPlan);
        
        // 按天数组织行程计划
        Map<Integer, List<ScheduleVO>> schedulesByDay = new HashMap<>();
        parsedPlan.getSchedules().forEach(schedule -> {
            int day = schedule.getDay();
            ScheduleVO scheduleVO = new ScheduleVO();
            scheduleVO.setType(schedule.getType());
            scheduleVO.setStartTime(schedule.getStartTime());
            scheduleVO.setEndTime(schedule.getEndTime());
            scheduleVO.setCost(schedule.getCost());
            scheduleVO.setLocation(schedule.getLocation());
            scheduleVO.setDescription(schedule.getDescription());
            
            schedulesByDay.computeIfAbsent(day, k -> new ArrayList<>()).add(scheduleVO);
        });
        
        // 对每一天的行程按开始时间进行排序
        schedulesByDay.forEach((day, schedules) -> {
            schedules.sort((s1, s2) -> s1.getStartTime().compareTo(s2.getStartTime()));
        });
        
        // 构建返回结果
        TravelPlanResponseVO responseVO = new TravelPlanResponseVO();
        responseVO.setTravelPlanId(savedPlan.getId());
        responseVO.setTotalDays(savedPlan.getTotalDays());
        responseVO.setTotalBudget(savedPlan.getTotalBudget());
        responseVO.setTotalPeople(savedPlan.getTotalPeople());
        responseVO.setSchedulesCount(parsedPlan.getSchedules().size());
        responseVO.setSchedulesByDay(schedulesByDay);
        
        return Result.success(responseVO);
    }

    /**
     * 测试API连接性
     */
    @GetMapping("/test")
    public Result<?> testConnection() {
        return Result.success("AI行程计划API连接正常");
    }
}