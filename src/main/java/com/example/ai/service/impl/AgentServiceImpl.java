package com.example.ai.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ai.model.request.AIRequest;
import com.example.ai.model.request.AgentRequest;
import com.example.ai.model.request.RAGRequest;
import com.example.ai.model.response.AIResponse;
import com.example.ai.model.response.AgentResponse;
import com.example.ai.model.response.AgentResponse.AgentResult;
import com.example.ai.model.response.RAGResponse;
import com.example.ai.model.vo.Document;
import com.example.ai.model.vo.Message;
import com.example.ai.service.AIService;
import com.example.ai.service.AgentService;
import com.example.ai.service.RAGService;

/**
 * Agent服务实现类，实现多Agent协作功能
 * 模拟问答Agent和总结Agent的协作过程
 */
@Service
public class AgentServiceImpl implements AgentService {
    
    private static final Logger log = LoggerFactory.getLogger(AgentServiceImpl.class);
    
    private final RAGService ragService;
    private final AIService aiService;
    
    @Autowired
    public AgentServiceImpl(RAGService ragService, AIService aiService) {
        this.ragService = ragService;
        this.aiService = aiService;
    }
    
    @Override
    public AgentResponse collaborate(AgentRequest request) {
        log.info("========== 多Agent协作开始 ==========");
        long startTime = System.currentTimeMillis();
        
        log.info("查询内容: {}", request.getQuery());
        log.info("配置信息 - 使用RAG: {}, topK: {}, 需要总结: {}", 
                request.getConfig().isUseRAG(), 
                request.getConfig().getTopK(),
                request.getConfig().isNeedSummary());
        
        AgentResponse response = new AgentResponse();
        response.setQuery(request.getQuery());
        
        // 1. 问答Agent处理
        log.info("\n--- 步骤1: 启动问答Agent ---");
        AgentResult qaResult = processQAAgent(request);
        response.setQaResult(qaResult);
        log.info("问答Agent完成，耗时: {}ms", qaResult.getProcessingTime());
        
        // 2. 总结Agent处理
        if (request.getConfig().isNeedSummary()) {
            log.info("\n--- 步骤2: 启动总结Agent ---");
            AgentResult summaryResult = processSummaryAgent(request, qaResult);
            response.setSummaryResult(summaryResult);
            log.info("总结Agent完成，耗时: {}ms", summaryResult.getProcessingTime());
        } else {
            log.info("\n--- 跳过总结Agent（配置不需要总结）---");
        }
        
        long totalTime = System.currentTimeMillis() - startTime;
        response.setTotalTime(totalTime);
        
        log.info("\n========== 多Agent协作完成 ==========");
        log.info("总耗时: {}ms", totalTime);
        log.info("问答Agent耗时: {}ms", qaResult.getProcessingTime());
        if (response.getSummaryResult() != null) {
            log.info("总结Agent耗时: {}ms", response.getSummaryResult().getProcessingTime());
        }
        log.info("====================================\n");
        
        return response;
    }
    
    /**
     * 处理问答Agent的逻辑
     */
    private AgentResult processQAAgent(AgentRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("[问答Agent] 开始处理...");
        
        AgentResult result = new AgentResult();
        result.setAgentName("问答助手");
        
        // 如果启用RAG，先进行文档检索
        List<Document> references = new ArrayList<>();
        if (request.getConfig().isUseRAG()) {
            log.info("[问答Agent] 启用RAG，开始检索相关文档...");
            RAGRequest ragRequest = new RAGRequest();
            ragRequest.setQuery(request.getQuery());
            ragRequest.setTopK(request.getConfig().getTopK());
            
            long ragStartTime = System.currentTimeMillis();
            RAGResponse ragResponse = ragService.search(ragRequest);
            references = ragResponse.getDocuments();
            long ragTime = System.currentTimeMillis() - ragStartTime;
            
            log.info("[问答Agent] RAG检索完成，找到 {} 个相关文档，耗时: {}ms", references.size(), ragTime);
            
            // 构建带RAG上下文的AI请求
            AIRequest aiRequest = new AIRequest();
            aiRequest.setMessages(Collections.singletonList(
                new Message("user", request.getQuery())
            ));
            
            AIRequest.RAGContext ragContext = new AIRequest.RAGContext();
            ragContext.setDocuments(references);
            ragContext.setUseContext(true);
            aiRequest.setRagContext(ragContext);
            
            // 调用AI服务获取回答
            log.info("[问答Agent] 调用AI服务生成回答...");
            long aiStartTime = System.currentTimeMillis();
            AIResponse aiResponse = aiService.chat(aiRequest);
            result.setContent(extractContentFromAIResponse(aiResponse));
            long aiTime = System.currentTimeMillis() - aiStartTime;
            
            log.info("[问答Agent] AI回答生成完成，耗时: {}ms", aiTime);
        } else {
            log.info("[问答Agent] 未启用RAG，直接调用AI服务...");
            // 不使用RAG，直接调用AI服务
            AIRequest aiRequest = new AIRequest();
            aiRequest.setMessages(Collections.singletonList(
                new Message("user", request.getQuery())
            ));
            
            long aiStartTime = System.currentTimeMillis();
            AIResponse aiResponse = aiService.chat(aiRequest);
            result.setContent(extractContentFromAIResponse(aiResponse));
            long aiTime = System.currentTimeMillis() - aiStartTime;
            
            log.info("[问答Agent] AI回答生成完成，耗时: {}ms", aiTime);
        }
        
        result.setProcessingTime(System.currentTimeMillis() - startTime);
        result.setReferences(references);
        
        log.info("[问答Agent] 处理完成，总耗时: {}ms", result.getProcessingTime());
        
        return result;
    }
    
    /**
     * 处理总结Agent的逻辑
     */
    private AgentResult processSummaryAgent(AgentRequest request, AgentResult qaResult) {
        long startTime = System.currentTimeMillis();
        log.info("[总结Agent] 开始处理...");
        log.info("[总结Agent] 待总结内容长度: {} 字符", qaResult.getContent().length());
        
        AgentResult result = new AgentResult();
        result.setAgentName("总结助手");
        
        // 构建总结请求
        AIRequest aiRequest = new AIRequest();
        aiRequest.setMessages(Arrays.asList(
            new Message("system", "请对以下内容进行简洁明了的总结，突出核心要点："),
            new Message("user", qaResult.getContent())
        ));
        
        // 调用AI服务获取总结
        log.info("[总结Agent] 调用AI服务生成总结...");
        long aiStartTime = System.currentTimeMillis();
        AIResponse aiResponse = aiService.chat(aiRequest);
        result.setContent(extractContentFromAIResponse(aiResponse));
        long aiTime = System.currentTimeMillis() - aiStartTime;
        
        result.setProcessingTime(System.currentTimeMillis() - startTime);
        result.setReferences(qaResult.getReferences());
        
        log.info("[总结Agent] 总结生成完成，AI耗时: {}ms，总耗时: {}ms", 
                aiTime, result.getProcessingTime());
        
        return result;
    }
    
    /**
     * 从AI响应中提取内容
     */
    private String extractContentFromAIResponse(AIResponse response) {
        if (response.getChoices() != null && !response.getChoices().isEmpty()) {
            return response.getChoices().get(0).getMessage().getContent();
        }
        return "无法获取AI响应内容";
    }
}