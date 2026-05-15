package com.example.ai.service;

import com.example.ai.model.request.AgentRequest;
import com.example.ai.model.response.AgentResponse;

/**
 * Agent服务接口，定义多Agent协作相关的业务方法
 */
public interface AgentService {
    
    /**
     * 多Agent协作方法
     * @param request Agent请求参数
     * @return Agent响应结果
     */
    AgentResponse collaborate(AgentRequest request);
}