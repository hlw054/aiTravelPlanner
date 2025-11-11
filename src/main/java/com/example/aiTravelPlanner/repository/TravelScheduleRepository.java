package com.example.aiTravelPlanner.repository;

import com.example.aiTravelPlanner.model.TravelSchedule;
import com.example.aiTravelPlanner.model.TravelSchedule.ScheduleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TravelScheduleRepository extends JpaRepository<TravelSchedule, Long> {
    
    // 根据行程表ID查找所有行程安排，并按天和开始时间排序
    List<TravelSchedule> findByTravelPlanIdOrderByDayAscStartTimeAsc(Long travelPlanId);
    
    // 根据行程表ID和天数查找行程安排
    List<TravelSchedule> findByTravelPlanIdAndDayOrderByStartTimeAsc(Long travelPlanId, Integer day);
    
    // 根据行程表ID和类型查找行程安排
    List<TravelSchedule> findByTravelPlanIdAndTypeOrderByDayAscStartTimeAsc(Long travelPlanId, ScheduleType type);
    
    // 根据行程安排ID和行程表ID查找行程安排（确保数据安全）
    Optional<TravelSchedule> findByIdAndTravelPlanId(Long id, Long travelPlanId);
    
    // 检查行程表是否拥有指定的行程安排
    boolean existsByIdAndTravelPlanId(Long id, Long travelPlanId);
    
    // 删除行程表的所有行程安排
    void deleteByTravelPlanId(Long travelPlanId);
}