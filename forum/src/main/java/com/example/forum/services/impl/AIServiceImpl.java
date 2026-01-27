package com.example.forum.services.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.forum.config.AIConfig;
import com.example.forum.services.IAIService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * AI服务实现类
 * 调用外部AI接口实现智能回复和摘要功能
 */
@Slf4j
@Service
public class AIServiceImpl implements IAIService {

    @Resource
    private AIConfig aiConfig;

    /**
     * HTTP客户端，用于发送请求到AI接口
     */
    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)  // 连接超时30秒
            .readTimeout(60, TimeUnit.SECONDS)     // 读取超时60秒
            .writeTimeout(30, TimeUnit.SECONDS)    // 写入超时30秒
            .build();

    /**
     * 根据帖子内容生成AI回复
     * @param title 帖子标题
     * @param content 帖子内容
     * @return AI生成的回复内容
     */
    @Override
    public String generateReply(String title, String content) {
        // 检查AI功能是否启用
        if (!aiConfig.isEnabled()) {
            return "AI功能暂未开启";
        }
        
        // 构造提示词，告诉AI要做什么
        String prompt = "你是一个技术论坛的AI助手。请根据以下帖子内容，给出一个专业、友好的回复。\n\n"
                + "帖子标题：" + title + "\n\n"
                + "帖子内容：" + content + "\n\n"
                + "请给出你的回复（不超过500字）：";
        
        // 调用AI接口
        return callAI(prompt);
    }

    /**
     * 对长文章进行摘要总结
     * @param content 文章内容
     * @return AI生成的摘要
     */
    @Override
    public String generateSummary(String content) {
        // 检查AI功能是否启用
        if (!aiConfig.isEnabled()) {
            return "AI功能暂未开启";
        }
        
        // 构造提示词
        String prompt = "请对以下文章内容进行摘要总结，提取关键信息，不超过200字：\n\n" + content;
        
        // 调用AI接口
        return callAI(prompt);
    }

    /**
     * 调用AI接口（火山引擎豆包）
     * @param prompt 提示词
     * @return AI返回的内容
     */
    private String callAI(String prompt) {
        try {
            // 构造请求体（火山引擎豆包格式，兼容OpenAI）
            JSONObject requestBody = new JSONObject();
            // 模型ID，使用豆包模型的endpoint
            requestBody.put("model", "ep-20260112145659-h547q");
            
            // 构造消息数组
            JSONArray messages = new JSONArray();
            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", prompt);
            messages.add(message);
            requestBody.put("messages", messages);
            
            // 创建HTTP请求
            Request request = new Request.Builder()
                    .url(aiConfig.getApiUrl())
                    .addHeader("Authorization", "Bearer " + aiConfig.getApiKey())
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(
                            requestBody.toJSONString(),
                            MediaType.parse("application/json")
                    ))
                    .build();
            
            log.info("AI请求URL: " + aiConfig.getApiUrl());
            log.info("AI请求体: " + requestBody.toJSONString());
            
            // 发送请求并获取响应
            try (Response response = httpClient.newCall(request).execute()) {
                String responseBody = response.body() != null ? response.body().string() : "无响应体";
                log.info("AI接口响应码: " + response.code());
                log.info("AI接口响应: " + responseBody);
                
                if (response.isSuccessful()) {
                    JSONObject jsonResponse = JSON.parseObject(responseBody);
                    
                    // 解析AI返回的内容
                    JSONArray choices = jsonResponse.getJSONArray("choices");
                    if (choices != null && !choices.isEmpty()) {
                        JSONObject choice = choices.getJSONObject(0);
                        JSONObject messageObj = choice.getJSONObject("message");
                        if (messageObj != null) {
                            return messageObj.getString("content");
                        }
                    }
                }
                return "AI服务暂时不可用: " + responseBody;
            }
        } catch (IOException e) {
            log.error("调用AI接口异常: ", e);
            return "AI服务暂时不可用，请稍后再试";
        }
    }

}
