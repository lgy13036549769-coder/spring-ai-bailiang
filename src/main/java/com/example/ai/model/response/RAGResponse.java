package com.example.ai.model.response;

import java.util.List;

import com.example.ai.model.vo.Document;

/**
 * RAG响应模型，用于表示知识库检索接口的响应数据
 */
public class RAGResponse {
    
    private String query;              // 用户查询问题
    private List<Document> documents;  // 检索到的相关文档列表
    private double totalScore;         // 总相关度分数
    
    // 构造方法
    public RAGResponse() {
    }
    
    public RAGResponse(String query, List<Document> documents, double totalScore) {
        this.query = query;
        this.documents = documents;
        this.totalScore = totalScore;
    }
    
    // Getters and Setters
    public String getQuery() {
        return query;
    }
    
    public void setQuery(String query) {
        this.query = query;
    }
    
    public List<Document> getDocuments() {
        return documents;
    }
    
    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }
    
    public double getTotalScore() {
        return totalScore;
    }
    
    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }
}