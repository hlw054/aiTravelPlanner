package com.example.aiTravelPlanner.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.lang.Double;

@Data
@Entity
@Table(name = "travel_plan")
public class TravelPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "total_days", nullable = false)
    private Integer totalDays;

    @Column(name = "total_budget", nullable = false, precision = 10, scale = 2)
    private Double totalBudget;

    @Column(name = "total_people", nullable = false)
    private Integer totalPeople;

    @Column(name = "accommodation_budget", precision = 10, scale = 2)
    private Double accommodationBudget;

    @Column(name = "dining_budget", precision = 10, scale = 2)
    private Double diningBudget;

    @Column(name = "transportation_budget", precision = 10, scale = 2)
    private Double transportationBudget;

    @Column(name = "attractions_budget", precision = 10, scale = 2)
    private Double attractionsBudget;

    @Column(name = "shopping_budget", precision = 10, scale = 2)
    private Double shoppingBudget;

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
}