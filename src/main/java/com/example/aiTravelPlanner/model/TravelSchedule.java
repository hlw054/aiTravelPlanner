package com.example.aiTravelPlanner.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.lang.Double;

@Data
@Entity
@Table(name = "travel_schedule")
public class TravelSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "travel_plan_id", nullable = false)
    private Long travelPlanId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private ScheduleType type;

    @Column(name = "day", nullable = false)
    private Integer day;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "cost", precision = 10, scale = 2)
    private Double cost;

    @Column(name = "location", length = 200)
    private String location;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // 行程类型枚举
    public enum ScheduleType {
        ACCOMMODATION, // 住宿
        DINING,        // 餐饮
        TRANSPORTATION, // 交通
        TICKET,        // 门票
        SHOPPING       // 购物
    }
}