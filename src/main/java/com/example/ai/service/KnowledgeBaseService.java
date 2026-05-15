package com.example.ai.service;

import com.example.ai.model.KnowledgeDocument;
import java.util.List;

/**
 * 知识库管理服务接口
 */
public interface KnowledgeBaseService {
    
    /**
     * 获取所有文档
     */
    List<KnowledgeDocument> getAllDocuments();
    
    /**
     * 根据ID获取文档
     */
    KnowledgeDocument getDocumentById(String id);
    
    /**
     * 添加新文档
     */
    KnowledgeDocument addDocument(KnowledgeDocument document);
    
    /**
     * 更新文档
     */
    KnowledgeDocument updateDocument(String id, KnowledgeDocument document);
    
    /**
     * 删除文档
     */
    boolean deleteDocument(String id);
    
    /**
     * 根据分类获取文档
     */
    List<KnowledgeDocument> getDocumentsByCategory(String category);
    
    /**
     * 搜索文档
     */
    List<KnowledgeDocument> searchDocuments(String keyword);
}
