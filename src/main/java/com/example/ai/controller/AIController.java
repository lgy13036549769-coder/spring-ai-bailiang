package com.example.ai.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.ai.model.request.AIRequest;
import com.example.ai.model.response.AIResponse;
import com.example.ai.model.response.CommonResponse;
import com.example.ai.service.AIService;
import com.example.ai.util.ResponseUtil;

/**
 * AI控制器，处理AI相关的HTTP请求
 */
@RestController
@RequestMapping("/ai")
public class AIController {
    
    private static final Logger log = LoggerFactory.getLogger(AIController.class);
    
    private final AIService aiService;
    
    @Autowired
    public AIController(AIService aiService) {
        this.aiService = aiService;
    }
    
    /**
     * AI对话接口
     * @param request AI请求参数
     * @return AI响应结果
     */
    @PostMapping("/chat")
    public ResponseEntity<CommonResponse<AIResponse>> chat(@RequestBody AIRequest request) {
        log.info("收到AI对话请求，消息数量: {}", request != null && request.getMessages() != null ? request.getMessages().size() : 0);
        
        // 调用AI服务进行对话
        AIResponse response = aiService.chat(request);
        
        log.info("AI对话响应完成，响应ID: {}", response.getId());
        return ResponseUtil.success(response);
    }
    
    /**
     * 流式AI对话接口
     * @param request AI请求参数
     * @return SSE响应
     */
    @PostMapping("/chat/stream")
    public SseEmitter streamChat(@RequestBody AIRequest request) {
        log.info("收到流式AI对话请求");
        
        // 设置流式响应标志
        request.setStream(true);
        
        // 调用AI服务进行流式对话
        SseEmitter emitter = aiService.streamChat(request);
        
        log.info("流式AI对话SSE连接已建立");
        return emitter;
    }
}