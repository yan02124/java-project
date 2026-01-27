package com.example.forum.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AI配置类
 * 用于读取application.yml中的AI相关配置
 */
@Component
@ConfigurationProperties(prefix = "ai")
public class AIConfig {
    
    /**
     * AI接口的API Key
     */
    private String apiKey;
    
    /**
     * AI接口的URL地址
     */
    private String apiUrl;
    
    /**
     * 是否启用AI功能
     */
    private boolean enabled = false;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
