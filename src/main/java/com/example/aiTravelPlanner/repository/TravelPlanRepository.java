package com.example.aiTravelPlanner.repository;

import com.example.aiTravelPlanner.model.TravelPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TravelPlanRepository extends JpaRepository<TravelPlan, Long> {
    
    // 根据用户ID查找所有行程表
    List<TravelPlan> findByUserId(Long userId);
    
    // 根据行程表ID和用户ID查找行程表（确保用户只能访问自己的行程表）
    Optional<TravelPlan> findByIdAndUserId(Long id, Long userId);
    
    // 检查用户是否拥有指定的行程表
    boolean existsByIdAndUserId(Long id, Long userId);
}