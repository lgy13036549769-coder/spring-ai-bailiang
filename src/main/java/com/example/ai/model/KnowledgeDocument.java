package com.example.ai.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 知识库文档模型
 * 用于存储企业知识库中的文档信息
 */
public class KnowledgeDocument {
    
    private String id;              // 文档唯一标识
    private String title;           // 文档标题
    private String content;         // 文档内容(支持Markdown格式)
    private String category;        // 文档分类
    private Map<String, Double> keywords; // 关键词及权重
    private String createdAt;       // 创建时间
    
    public KnowledgeDocument() {
        this.keywords = new HashMap<>();
    }
    
    public KnowledgeDocument(String id, String title, String category, String content) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.content = content;
        this.keywords = new HashMap<>();
        this.createdAt = java.time.LocalDateTime.now().toString();
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public Map<String, Double> getKeywords() {
        return keywords;
    }
    
    public void setKeywords(Map<String, Double> keywords) {
        this.keywords = keywords;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "KnowledgeDocument{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", contentLength=" + (content != null ? content.length() : 0) +
                '}';
    }
}
