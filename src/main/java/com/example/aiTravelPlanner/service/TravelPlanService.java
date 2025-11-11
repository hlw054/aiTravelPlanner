package com.example.aiTravelPlanner.service;

import com.example.aiTravelPlanner.model.TravelPlan;
import com.example.aiTravelPlanner.model.TravelSchedule;
import com.example.aiTravelPlanner.repository.TravelPlanRepository;
import com.example.aiTravelPlanner.repository.TravelScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.example.aiTravelPlanner.service.TravelPlanParser;

@Service
public class TravelPlanService {

    private final TravelPlanRepository travelPlanRepository;
    private final TravelScheduleRepository travelScheduleRepository;

    @Autowired
    public TravelPlanService(TravelPlanRepository travelPlanRepository, TravelScheduleRepository travelScheduleRepository) {
        this.travelPlanRepository = travelPlanRepository;
        this.travelScheduleRepository = travelScheduleRepository;
    }

    /**
     * 将解析后的行程计划保存到数据库
     * @param userId 用户ID
     * @param parsedPlan 解析后的行程计划
     * @return 保存后的行程计划对象
     */
    @Transactional
    public TravelPlan saveTravelPlan(Long userId, TravelPlanParser.ParsedTravelPlan parsedPlan) {
        // 创建行程表记录
        TravelPlan travelPlan = new TravelPlan();
        travelPlan.setUserId(userId);
        travelPlan.setTotalDays(parsedPlan.getTotalDays());
        travelPlan.setTotalBudget(parsedPlan.getTotalBudget());
        travelPlan.setTotalPeople(parsedPlan.getTotalPeople());
        travelPlan.setAccommodationBudget(parsedPlan.getAccommodationBudget());
        travelPlan.setDiningBudget(parsedPlan.getDiningBudget());
        travelPlan.setTransportationBudget(parsedPlan.getTransportationBudget());
        travelPlan.setAttractionsBudget(parsedPlan.getAttractionsBudget());
        travelPlan.setShoppingBudget(parsedPlan.getShoppingBudget());
        
        // 保存行程表
        TravelPlan savedTravelPlan = travelPlanRepository.save(travelPlan);
        
        // 保存行程安排
        saveSchedules(savedTravelPlan.getId(), parsedPlan.getSchedules());
        
        return savedTravelPlan;
    }

    /**
     * 保存行程安排列表
     */
    private void saveSchedules(Long travelPlanId, List<TravelPlanParser.ParsedSchedule> parsedSchedules) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        
        for (TravelPlanParser.ParsedSchedule parsedSchedule : parsedSchedules) {
            TravelSchedule schedule = new TravelSchedule();
            schedule.setTravelPlanId(travelPlanId);
            schedule.setDay(parsedSchedule.getDay());
            
            // 设置行程类型，使用枚举值
            try {
                schedule.setType(TravelSchedule.ScheduleType.valueOf(parsedSchedule.getType()));
            } catch (IllegalArgumentException e) {
                // 如果类型无效，默认为SHOPPING
                schedule.setType(TravelSchedule.ScheduleType.SHOPPING);
            }
            
            // 解析时间
            if (!parsedSchedule.getStartTime().isEmpty()) {
                try {
                    schedule.setStartTime(LocalDateTime.parse(parsedSchedule.getStartTime(), formatter));
                } catch (Exception e) {
                    // 时间解析失败，使用当前时间
                    schedule.setStartTime(LocalDateTime.now());
                }
            } else {
                schedule.setStartTime(LocalDateTime.now());
            }
            
            if (!parsedSchedule.getEndTime().isEmpty()) {
                try {
                    schedule.setEndTime(LocalDateTime.parse(parsedSchedule.getEndTime(), formatter));
                } catch (Exception e) {
                    // 时间解析失败，使用当前时间+1小时
                    schedule.setEndTime(LocalDateTime.now().plusHours(1));
                }
            } else {
                schedule.setEndTime(LocalDateTime.now().plusHours(1));
            }
            
            schedule.setCost(parsedSchedule.getCost());
            schedule.setLocation(parsedSchedule.getLocation());
            schedule.setDescription(parsedSchedule.getDescription());
            
            travelScheduleRepository.save(schedule);
        }
    }

    /**
     * 根据用户ID获取行程计划列表
     */
    public List<TravelPlan> getTravelPlansByUserId(Long userId) {
        return travelPlanRepository.findByUserId(userId);
    }
    
    /**
     * 分页获取用户的行程计划
     * @param userId 用户ID
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 分页后的行程计划
     */
    public Page<TravelPlan> getTravelPlansByUserIdPaged(Long userId, Integer page, Integer size) {
        // 转换为从0开始的页码
        Pageable pageable = PageRequest.of(page - 1, size);
        return travelPlanRepository.findByUserId(userId, pageable);
    }

    /**
     * 根据ID和用户ID获取行程计划
     */
    public TravelPlan getTravelPlanByIdAndUserId(Long id, Long userId) {
        return travelPlanRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Travel plan not found"));
    }

    /**
     * 通过行程计划ID获取行程安排列表
     * @param travelPlanId 行程计划ID
     * @param userId 用户ID
     * @return 行程安排列表
     */
    public List<TravelSchedule> getTravelSchedulesByPlanId(Long travelPlanId, Long userId) {
        // 验证行程计划是否存在且属于该用户
        if (!travelPlanRepository.existsByIdAndUserId(travelPlanId, userId)) {
            throw new RuntimeException("Travel plan not found or access denied");
        }
        
        // 获取行程安排并按天和开始时间排序
        return travelScheduleRepository.findByTravelPlanIdOrderByDayAscStartTimeAsc(travelPlanId);
    }
    
    /**
     * 删除行程计划（包括相关的行程安排）
     */
    @Transactional
    public void deleteTravelPlan(Long id, Long userId) {
        if (!travelPlanRepository.existsByIdAndUserId(id, userId)) {
            throw new RuntimeException("Travel plan not found or access denied");
        }
        
        // 先删除相关的行程安排
        travelScheduleRepository.deleteByTravelPlanId(id);
        
        // 再删除行程计划
        travelPlanRepository.deleteById(id);
    }
}