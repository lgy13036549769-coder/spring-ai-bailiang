package com.example.ai.model.request;

import java.util.List;

import com.example.ai.model.vo.Document;
import com.example.ai.model.vo.Message;

/**
 * AI请求模型，用于AI对话接口的请求参数
 */
public class AIRequest {
    
    private List<Message> messages;      // 对话历史消息
    private boolean stream = false;      // 是否启用流式响应
    private RAGContext ragContext;       // RAG检索结果上下文（可选）
    
    // Getters and Setters
    public List<Message> getMessages() {
        return messages;
    }
    
    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
    
    public boolean isStream() {
        return stream;
    }
    
    public void setStream(boolean stream) {
        this.stream = stream;
    }
    
    public RAGContext getRagContext() {
        return ragContext;
    }
    
    public void setRagContext(RAGContext ragContext) {
        this.ragContext = ragContext;
    }
    
    /**
     * RAG上下文类，用于传递RAG检索结果
     */
    public static class RAGContext {
        private List<Document> documents;    // 相关文档列表
        private boolean useContext = true;   // 是否使用检索结果作为上下文
        
        // Getters and Setters
        public List<Document> getDocuments() {
            return documents;
        }
        
        public void setDocuments(List<Document> documents) {
            this.documents = documents;
        }
        
        public boolean isUseContext() {
            return useContext;
        }
        
        public void setUseContext(boolean useContext) {
            this.useContext = useContext;
        }
    }
}