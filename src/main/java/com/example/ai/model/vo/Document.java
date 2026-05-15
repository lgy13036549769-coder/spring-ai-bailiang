package com.example.ai.model.vo;

import java.util.HashMap;
import java.util.Map;

/**
 * 文档模型，用于表示知识库中的文档
 */
public class Document {
    
    private String id;         // 文档唯一标识
    private String title;      // 文档标题
    private String content;    // 文档内容
    private String category;   // 文档分类
    private String createdAt;  // 创建时间
    private Map<String, Double> keywords = new HashMap<>(); // 关键词及其权重（用于简单相关性计算）
    
    // 构造方法
    public Document() {
    }
    
    public Document(String id, String title, String content, String category) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.category = category;
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
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public Map<String, Double> getKeywords() {
        return keywords;
    }
    
    public void setKeywords(Map<String, Double> keywords) {
        this.keywords = keywords;
    }
}