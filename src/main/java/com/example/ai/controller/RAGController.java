package com.example.ai.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ai.model.request.RAGRequest;
import com.example.ai.model.response.CommonResponse;
import com.example.ai.model.response.RAGResponse;
import com.example.ai.service.RAGService;
import com.example.ai.util.ResponseUtil;

/**
 * RAG控制器，处理知识库检索相关的HTTP请求
 */
@RestController
@RequestMapping("/rag")
public class RAGController {
    
    private static final Logger log = LoggerFactory.getLogger(RAGController.class);
    
    private final RAGService ragService;
    
    @Autowired
    public RAGController(RAGService ragService) {
        this.ragService = ragService;
    }
    
    /**
     * 知识库检索接口
     * @param request RAG请求参数
     * @return RAG响应结果
     */
    @PostMapping("/search")
    public ResponseEntity<CommonResponse<RAGResponse>> search(@RequestBody RAGRequest request) {
        log.info("收到RAG知识库检索请求，查询: {}, topK: {}", 
                request != null ? request.getQuery() : "null",
                request != null ? request.getTopK() : 0);
        
        // 验证请求参数
        if (request == null || request.getQuery() == null || request.getQuery().trim().isEmpty()) {
            log.warn("RAG检索请求参数验证失败: 查询内容为空");
            return ResponseUtil.error("查询内容不能为空");
        }
        
        // 调用RAG服务进行检索
        RAGResponse response = ragService.search(request);
        
        log.info("RAG检索响应完成，返回文档数: {}", response.getDocuments().size());
        return ResponseUtil.success(response);
    }
}