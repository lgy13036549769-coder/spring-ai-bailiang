package com.example.ai.model.request;

/**
 * RAG请求模型，用于知识库检索接口的请求参数
 */
public class RAGRequest {
    
    private String query;      // 用户查询问题
    private int topK = 3;      // 返回最相关的前K条文档
    
    // 构造方法
    public RAGRequest() {
    }
    
    public RAGRequest(String query) {
        this.query = query;
    }
    
    public RAGRequest(String query, int topK) {
        this.query = query;
        this.topK = topK;
    }
    
    // Getters and Setters
    public String getQuery() {
        return query;
    }
    
    public void setQuery(String query) {
        this.query = query;
    }
    
    public int getTopK() {
        return topK;
    }
    
    public void setTopK(int topK) {
        this.topK = topK;
    }
}