package com.example.aiTravelPlanner.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TravelPlanParser {

    private final ObjectMapper objectMapper;

    public TravelPlanParser() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 解析AI返回的行程计划JSON
     * @param aiResponse AI返回的JSON字符串
     * @return 解析后的行程计划对象
     */
    public ParsedTravelPlan parseTravelPlan(String aiResponse) {
        try {
            // 提取JSON部分（有时候AI会在JSON前后加文字）
            String jsonPart = extractJsonFromResponse(aiResponse);
            
            JsonNode rootNode = objectMapper.readTree(jsonPart);
            
            // 提取行程计划基本信息
            int totalDays = rootNode.has("totalDays") ? rootNode.get("totalDays").asInt() : 1;
            int totalPeople = rootNode.has("totalPeople") ? rootNode.get("totalPeople").asInt() : 1;
            double totalBudget = rootNode.has("totalBudget") ? rootNode.get("totalBudget").asDouble() : 0.0;
            double accommodationBudget = rootNode.has("accommodationBudget") ? rootNode.get("accommodationBudget").asDouble() : 0.0;
            double diningBudget = rootNode.has("diningBudget") ? rootNode.get("diningBudget").asDouble() : 0.0;
            double transportationBudget = rootNode.has("transportationBudget") ? rootNode.get("transportationBudget").asDouble() : 0.0;
            double attractionsBudget = rootNode.has("attractionsBudget") ? rootNode.get("attractionsBudget").asDouble() : 0.0;
            double shoppingBudget = rootNode.has("shoppingBudget") ? rootNode.get("shoppingBudget").asDouble() : 0.0;
            
            // 提取行程安排
            List<ParsedSchedule> schedules = new ArrayList<>();
            if (rootNode.has("schedules") && rootNode.get("schedules").isArray()) {
                for (JsonNode scheduleNode : rootNode.get("schedules")) {
                    ParsedSchedule schedule = new ParsedSchedule();
                    schedule.setDay(scheduleNode.has("day") ? scheduleNode.get("day").asInt() : 1);
                    schedule.setType(scheduleNode.has("type") ? scheduleNode.get("type").asText() : "OTHER");
                    schedule.setStartTime(scheduleNode.has("startTime") ? scheduleNode.get("startTime").asText() : "");
                    schedule.setEndTime(scheduleNode.has("endTime") ? scheduleNode.get("endTime").asText() : "");
                    schedule.setCost(scheduleNode.has("cost") ? scheduleNode.get("cost").asDouble() : 0.0);
                    schedule.setLocation(scheduleNode.has("location") ? scheduleNode.get("location").asText() : "");
                    schedule.setDescription(scheduleNode.has("description") ? scheduleNode.get("description").asText() : "");
                    schedules.add(schedule);
                }
            }
            
            ParsedTravelPlan plan = new ParsedTravelPlan();
            plan.setTotalDays(totalDays);
            plan.setTotalPeople(totalPeople);
            plan.setTotalBudget(totalBudget);
            plan.setAccommodationBudget(accommodationBudget);
            plan.setDiningBudget(diningBudget);
            plan.setTransportationBudget(transportationBudget);
            plan.setAttractionsBudget(attractionsBudget);
            plan.setShoppingBudget(shoppingBudget);
            plan.setSchedules(schedules);
            
            return plan;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse travel plan: " + e.getMessage(), e);
        }
    }
    
    /**
     * 从AI响应中提取纯JSON部分
     */
    private String extractJsonFromResponse(String response) {
        // 尝试找到JSON的开始和结束标记
        int startIdx = response.indexOf('{');
        int endIdx = response.lastIndexOf('}');
        
        if (startIdx != -1 && endIdx != -1 && endIdx > startIdx) {
            return response.substring(startIdx, endIdx + 1);
        }
        
        // 如果没找到明确的JSON边界，返回整个响应
        return response;
    }
    
    /**
     * 解析后的行程计划对象
     */
    public static class ParsedTravelPlan {
        private int totalDays;
        private int totalPeople;
        private double totalBudget;
        private double accommodationBudget;
        private double diningBudget;
        private double transportationBudget;
        private double attractionsBudget;
        private double shoppingBudget;
        private List<ParsedSchedule> schedules;
        
        // Getters and Setters
        public int getTotalDays() { return totalDays; }
        public void setTotalDays(int totalDays) { this.totalDays = totalDays; }
        public int getTotalPeople() { return totalPeople; }
        public void setTotalPeople(int totalPeople) { this.totalPeople = totalPeople; }
        public double getTotalBudget() { return totalBudget; }
        public void setTotalBudget(double totalBudget) { this.totalBudget = totalBudget; }
        public double getAccommodationBudget() { return accommodationBudget; }
        public void setAccommodationBudget(double accommodationBudget) { this.accommodationBudget = accommodationBudget; }
        public double getDiningBudget() { return diningBudget; }
        public void setDiningBudget(double diningBudget) { this.diningBudget = diningBudget; }
        public double getTransportationBudget() { return transportationBudget; }
        public void setTransportationBudget(double transportationBudget) { this.transportationBudget = transportationBudget; }
        public double getAttractionsBudget() { return attractionsBudget; }
        public void setAttractionsBudget(double attractionsBudget) { this.attractionsBudget = attractionsBudget; }
        public double getShoppingBudget() { return shoppingBudget; }
        public void setShoppingBudget(double shoppingBudget) { this.shoppingBudget = shoppingBudget; }
        public List<ParsedSchedule> getSchedules() { return schedules; }
        public void setSchedules(List<ParsedSchedule> schedules) { this.schedules = schedules; }
    }
    
    /**
     * 解析后的行程安排对象
     */
    public static class ParsedSchedule {
        private int day;
        private String type;
        private String startTime;
        private String endTime;
        private double cost;
        private String location;
        private String description;
        
        // Getters and Setters
        public int getDay() { return day; }
        public void setDay(int day) { this.day = day; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }
        public String getEndTime() { return endTime; }
        public void setEndTime(String endTime) { this.endTime = endTime; }
        public double getCost() { return cost; }
        public void setCost(double cost) { this.cost = cost; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}