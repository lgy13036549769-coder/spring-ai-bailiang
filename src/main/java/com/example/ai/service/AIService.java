package com.example.ai.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.ai.model.request.AIRequest;
import com.example.ai.model.response.AIResponse;

/**
 * AI服务接口，定义AI相关的业务方法
 */
public interface AIService {
    
    /**
     * AI对话方法
     * @param request AI请求参数
     * @return AI响应结果
     */
    AIResponse chat(AIRequest request);
    
    /**
     * 流式AI对话方法
     * @param request AI请求参数
     * @return SSE发射器
     */
    SseEmitter streamChat(AIRequest request);
}