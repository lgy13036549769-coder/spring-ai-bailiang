package com.example.ai.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ai.model.request.AgentRequest;
import com.example.ai.model.response.AgentResponse;
import com.example.ai.model.response.CommonResponse;
import com.example.ai.service.AgentService;
import com.example.ai.util.ResponseUtil;

/**
 * Agent控制器，处理多Agent协作相关的HTTP请求
 */
@RestController
@RequestMapping("/agent")
public class AgentController {
    
    private static final Logger log = LoggerFactory.getLogger(AgentController.class);
    
    private final AgentService agentService;
    
    @Autowired
    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }
    
    /**
     * 多Agent协作接口
     * @param request Agent请求参数
     * @return Agent响应结果
     */
    @PostMapping("/collaborate")
    public ResponseEntity<CommonResponse<AgentResponse>> collaborate(@RequestBody AgentRequest request) {
        log.info("收到多Agent协作请求，查询: {}", request != null ? request.getQuery() : "null");
        
        // 验证请求参数
        if (request == null || request.getQuery() == null || request.getQuery().trim().isEmpty()) {
            log.warn("多Agent协作请求参数验证失败: 查询内容为空");
            return ResponseUtil.error("查询内容不能为空");
        }
        
        // 如果没有配置，使用默认配置
        if (request.getConfig() == null) {
            log.info("未提供配置信息，使用默认配置");
            request.setConfig(new AgentRequest.AgentConfig());
        } else {
            log.info("配置信息 - 使用RAG: {}, topK: {}, 需要总结: {}",
                    request.getConfig().isUseRAG(),
                    request.getConfig().getTopK(),
                    request.getConfig().isNeedSummary());
        }
        
        // 调用Agent服务进行多Agent协作
        AgentResponse response = agentService.collaborate(request);
        
        log.info("多Agent协作响应完成，总耗时: {}ms", response.getTotalTime());
        return ResponseUtil.success(response);
    }
}