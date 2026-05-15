package com.example.ai.service;

import com.example.ai.model.request.RAGRequest;
import com.example.ai.model.response.RAGResponse;

/**
 * RAG知识库服务接口，定义知识库检索相关的业务方法
 */
public interface RAGService {
    
    /**
     * 知识库检索方法
     * @param request RAG请求参数
     * @return RAG响应结果
     */
    RAGResponse search(RAGRequest request);
}