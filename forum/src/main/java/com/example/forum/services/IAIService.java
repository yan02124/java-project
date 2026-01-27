package com.example.forum.services;

/**
 * AI服务接口
 * 提供AI相关功能
 */
public interface IAIService {
    
    /**
     * 根据帖子内容生成AI回复
     * @param title 帖子标题
     * @param content 帖子内容
     * @return AI生成的回复内容
     */
    String generateReply(String title, String content);
    
    /**
     * 对长文章进行摘要总结
     * @param content 文章内容
     * @return AI生成的摘要
     */
    String generateSummary(String content);
}
