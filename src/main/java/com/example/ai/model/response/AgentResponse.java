package com.example.ai.model.response;

import java.util.List;

import com.example.ai.model.vo.Document;

/**
 * Agent响应模型，用于表示多Agent协作接口的响应数据
 */
public class AgentResponse {
    
    private String query;              // 用户查询问题
    private AgentResult qaResult;      // 问答Agent结果
    private AgentResult summaryResult; // 总结Agent结果
    private long totalTime;            // 总处理时间
    
    // 构造方法
    public AgentResponse() {
    }
    
    public AgentResponse(String query, AgentResult qaResult, AgentResult summaryResult, long totalTime) {
        this.query = query;
        this.qaResult = qaResult;
        this.summaryResult = summaryResult;
        this.totalTime = totalTime;
    }
    
    // Getters and Setters
    public String getQuery() {
        return query;
    }
    
    public void setQuery(String query) {
        this.query = query;
    }
    
    public AgentResult getQaResult() {
        return qaResult;
    }
    
    public void setQaResult(AgentResult qaResult) {
        this.qaResult = qaResult;
    }
    
    public AgentResult getSummaryResult() {
        return summaryResult;
    }
    
    public void setSummaryResult(AgentResult summaryResult) {
        this.summaryResult = summaryResult;
    }
    
    public long getTotalTime() {
        return totalTime;
    }
    
    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }
    
    /**
     * Agent结果类，用于表示单个Agent的处理结果
     */
    public static class AgentResult {
        private String agentName;          // Agent名称
        private String content;            // 处理结果内容
        private long processingTime;       // 处理时间（毫秒）
        private List<Document> references; // 参考文档列表
        
        // 构造方法
        public AgentResult() {
        }
        
        public AgentResult(String agentName, String content, long processingTime, List<Document> references) {
            this.agentName = agentName;
            this.content = content;
            this.processingTime = processingTime;
            this.references = references;
        }
        
        // Getters and Setters
        public String getAgentName() {
            return agentName;
        }
        
        public void setAgentName(String agentName) {
            this.agentName = agentName;
        }
        
        public String getContent() {
            return content;
        }
        
        public void setContent(String content) {
            this.content = content;
        }
        
        public long getProcessingTime() {
            return processingTime;
        }
        
        public void setProcessingTime(long processingTime) {
            this.processingTime = processingTime;
        }
        
        public List<Document> getReferences() {
            return references;
        }
        
        public void setReferences(List<Document> references) {
            this.references = references;
        }
    }
}