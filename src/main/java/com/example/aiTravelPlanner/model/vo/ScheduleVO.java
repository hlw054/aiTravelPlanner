package com.example.aiTravelPlanner.model.vo;

import lombok.Data;


/**
 * 行程安排VO类
 * 用于封装行程安排的详细信息
 */
@Data
public class ScheduleVO {
    /**
     * 行程类型
     * 可选值: ACCOMMODATION, DINING, TRANSPORTATION, TICKET, SHOPPING
     */
    private String type;
    
    /**
     * 开始时间
     */
    private String startTime;
    
    /**
     * 结束时间
     */
    private String endTime;
    
    /**
     * 费用
     */
    private Double cost;
    
    /**
     * 地点
     */
    private String location;
    
    /**
     * 描述
     */
    private String description;
}