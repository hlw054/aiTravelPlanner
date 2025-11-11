package com.example.aiTravelPlanner.model.vo;

import com.example.aiTravelPlanner.model.vo.ScheduleVO;
import lombok.Data;

import java.lang.Double;
import java.util.List;
import java.util.Map;

/**
 * 行程计划响应VO类
 * 用于封装行程计划的响应数据
 */
@Data
public class TravelPlanResponseVO {
    /**
     * 行程计划ID
     */
    private Long travelPlanId;
    
    /**
     * 总天数
     */
    private Integer totalDays;
    
    /**
     * 总预算
     */
    private Double totalBudget;
    
    /**
     * 总人数
     */
    private Integer totalPeople;
    
    /**
     * 行程数量
     */
    private Integer schedulesCount;
    
    /**
     * 按天数组织的行程安排
     */
    private Map<Integer, List<ScheduleVO>> schedulesByDay;
}