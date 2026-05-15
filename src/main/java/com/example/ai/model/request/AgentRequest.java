package com.example.ai.model.request;

/**
 * Agent请求模型，用于多Agent协作接口的请求参数
 */
public class AgentRequest {
    
    private String query;              // 用户查询问题
    private AgentConfig config;        // Agent配置
    
    // 构造方法
    public AgentRequest() {
        this.config = new AgentConfig();
    }
    
    public AgentRequest(String query) {
        this.query = query;
        this.config = new AgentConfig();
    }
    
    public AgentRequest(String query, AgentConfig config) {
        this.query = query;
        this.config = config;
    }
    
    // Getters and Setters
    public String getQuery() {
        return query;
    }
    
    public void setQuery(String query) {
        this.query = query;
    }
    
    public AgentConfig getConfig() {
        return config;
    }
    
    public void setConfig(AgentConfig config) {
        this.config = config;
    }
    
    /**
     * Agent配置类，用于配置多Agent协作的参数
     */
    public static class AgentConfig {
        private boolean useRAG = true;     // 是否使用RAG检索
        private int topK = 3;              // RAG检索返回条数
        private boolean needSummary = true; // 是否需要总结
        
        // 构造方法
        public AgentConfig() {
        }
        
        public AgentConfig(boolean useRAG, int topK, boolean needSummary) {
            this.useRAG = useRAG;
            this.topK = topK;
            this.needSummary = needSummary;
        }
        
        // Getters and Setters
        public boolean isUseRAG() {
            return useRAG;
        }
        
        public void setUseRAG(boolean useRAG) {
            this.useRAG = useRAG;
        }
        
        public int getTopK() {
            return topK;
        }
        
        public void setTopK(int topK) {
            this.topK = topK;
        }
        
        public boolean isNeedSummary() {
            return needSummary;
        }
        
        public void setNeedSummary(boolean needSummary) {
            this.needSummary = needSummary;
        }
    }
}