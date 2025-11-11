package com.example.aiTravelPlanner.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;

import java.util.Arrays;

@Service
public class BaiLianService {

    private static final Logger logger = LoggerFactory.getLogger(BaiLianService.class);

    @Value("${aliyun.bailian.api-key}")
    private String apiKey;

    @Value("${aliyun.bailian.api-url}")
    private String apiUrl;

    private OpenAIClient openAiClient;
    private final ObjectMapper objectMapper;

    public BaiLianService() {
        this.objectMapper = new ObjectMapper();
    }

    @PostConstruct
    public void init() {
        // 在Spring注入属性后初始化OpenAI客户端
        this.openAiClient = OpenAIOkHttpClient.builder()
                .apiKey(apiKey)
                .baseUrl(apiUrl)
                .build();
    }

    /**
     * 使用OpenAI Java SDK v2.6.0调用API生成行程计划
     * @param userRequest 用户的旅游要求
     * @return AI生成的行程计划JSON
     */
    public String generateTravelPlan(String userRequest) {
        try {
            logger.info("开始生成AI行程计划，用户请求: {}", userRequest);
            // 构建合理的prompt，确保AI返回格式化的信息
            String prompt = "请根据用户的旅游要求，生成一个详细的旅游行程计划。" +
                    "注意以下要求：\n" +
                    "1. 每天的旅游计划从目标城市的酒店开始\n" +
                    "2. 在每一个场景（景点、餐厅等）之间必须穿插交通安排\n" +
                    "3. 每一天中的行程安排必须严格按照时间顺序排列\n" +
                    "请按照以下JSON格式返回，确保格式正确且可被解析：\n" +
                    "{\n" +
                    "  \"totalDays\": 3,\n" +
                    "  \"totalPeople\": 2,\n" +
                    "  \"totalBudget\": 3000.00,\n" +
                    "  \"accommodationBudget\": 1000.00,\n" +
                    "  \"diningBudget\": 800.00,\n" +
                    "  \"transportationBudget\": 600.00,\n" +
                    "  \"attractionsBudget\": 400.00,\n" +
                    "  \"shoppingBudget\": 200.00,\n" +
                    "  \"schedules\": [\n" +
                    "    {\n" +
                    "      \"day\": 1,\n" +
                    "      \"type\": \"ACCOMMODATION\", // 可选值: ACCOMMODATION, DINING, TRANSPORTATION, TICKET, SHOPPING\n" +
                    "      \"startTime\": \"2023-07-15T14:00:00\",\n" +
                    "      \"endTime\": \"2023-07-16T12:00:00\",\n" +
                    "      \"cost\": 300.00,\n" +
                    "      \"location\": \"某酒店\",\n" +
                    "      \"description\": \"舒适的双人房\"\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}\n" +
                    "用户的旅游要求是：" + userRequest;

            // 使用OpenAI Java SDK v2.6.0构建请求 - 严格按照官方示例
            logger.info("准备调用AI模型，正在构建请求参数...");
            ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                    .addSystemMessage("你是一个专业的旅游规划助手，擅长根据用户需求制定详细的旅游行程计划。")
                    .addUserMessage(prompt)
                    .model("qwen-plus-2025-07-28")
                    .build();

            // 发送请求并获取响应
            logger.info("发送AI模型调用请求...");
            ChatCompletion chatCompletion = openAiClient.chat().completions().create(params);
            logger.info("AI模型调用成功，收到响应");

            // 提取生成的内容 - 正确处理Optional返回值
            if (chatCompletion.choices() != null && !chatCompletion.choices().isEmpty()) {
                String content = chatCompletion.choices().get(0).message().content().orElse("无法生成行程计划");
                logger.info("成功提取AI生成内容，内容长度: {}字符", content.length());
                return content;
            }

            logger.error("AI响应格式错误: Empty choices");
            throw new RuntimeException("AI response format error: Empty choices");

        } catch (Exception e) {
            logger.error("生成行程计划失败: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate travel plan: " + e.getMessage(), e);
        }
    }
}