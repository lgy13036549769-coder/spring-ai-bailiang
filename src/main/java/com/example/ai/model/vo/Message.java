package com.example.ai.model.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 消息模型，用于表示AI对话中的消息
 */
public class Message {
    
    private String role;
    private String content;
    
    // 构造方法
    public Message() {
    }
    
    public Message(String role, String content) {
        this.role = role;
        this.content = content;
    }
    
    // Getters and Setters
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
}