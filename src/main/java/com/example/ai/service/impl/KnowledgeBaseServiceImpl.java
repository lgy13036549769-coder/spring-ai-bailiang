package com.example.ai.service.impl;

import com.example.ai.model.KnowledgeBase;
import com.example.ai.model.KnowledgeDocument;
import com.example.ai.service.KnowledgeBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 知识库管理服务实现类
 */
@Service
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {
    
    private static final Logger log = LoggerFactory.getLogger(KnowledgeBaseServiceImpl.class);
    
    // 使用 ConcurrentHashMap 存储文档,支持并发访问
    private final Map<String, KnowledgeDocument> documentStore = new ConcurrentHashMap<>();
    
    public KnowledgeBaseServiceImpl() {
        // 初始化时加载默认文档
        initDefaultDocuments();
    }
    
    /**
     * 初始化默认文档
     */
    private void initDefaultDocuments() {
        log.info("开始初始化企业知识库...");
        long startTime = System.currentTimeMillis();
        
        List<KnowledgeDocument> knowledgeDocs = KnowledgeBase.getAllDocuments();
        
        for (KnowledgeDocument kbDoc : knowledgeDocs) {
            documentStore.put(kbDoc.getId(), kbDoc);
        }
        
        log.info("企业知识库初始化完成，共加载 {} 个文档，耗时: {}ms", 
                documentStore.size(), System.currentTimeMillis() - startTime);
    }
    
    @Override
    public List<KnowledgeDocument> getAllDocuments() {
        return new ArrayList<>(documentStore.values());
    }
    
    @Override
    public KnowledgeDocument getDocumentById(String id) {
        return documentStore.get(id);
    }
    
    @Override
    public KnowledgeDocument addDocument(KnowledgeDocument document) {
        if (document == null || document.getTitle() == null || document.getContent() == null) {
            throw new IllegalArgumentException("文档标题和内容不能为空");
        }
        
        // 生成唯一ID
        String id = "kb-" + String.format("%03d", documentStore.size() + 1);
        document.setId(id);
        document.setCreatedAt(java.time.LocalDateTime.now().toString());
        
        documentStore.put(id, document);
        log.info("添加新文档: {} (ID: {})", document.getTitle(), id);
        
        return document;
    }
    
    @Override
    public KnowledgeDocument updateDocument(String id, KnowledgeDocument document) {
        if (!documentStore.containsKey(id)) {
            throw new IllegalArgumentException("文档不存在: " + id);
        }
        
        // 保留原有ID和创建时间
        document.setId(id);
        KnowledgeDocument existingDoc = documentStore.get(id);
        if (document.getCreatedAt() == null) {
            document.setCreatedAt(existingDoc.getCreatedAt());
        }
        
        documentStore.put(id, document);
        log.info("更新文档: {} (ID: {})", document.getTitle(), id);
        
        return document;
    }
    
    @Override
    public boolean deleteDocument(String id) {
        KnowledgeDocument removed = documentStore.remove(id);
        if (removed != null) {
            log.info("删除文档: {} (ID: {})", removed.getTitle(), id);
            return true;
        }
        log.warn("尝试删除不存在的文档: {}", id);
        return false;
    }
    
    @Override
    public List<KnowledgeDocument> getDocumentsByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return getAllDocuments();
        }
        
        return documentStore.values().stream()
                .filter(doc -> category.equals(doc.getCategory()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<KnowledgeDocument> searchDocuments(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllDocuments();
        }
        
        String lowerKeyword = keyword.toLowerCase();
        
        return documentStore.values().stream()
                .filter(doc -> 
                    doc.getTitle().toLowerCase().contains(lowerKeyword) ||
                    doc.getContent().toLowerCase().contains(lowerKeyword) ||
                    doc.getCategory().toLowerCase().contains(lowerKeyword)
                )
                .collect(Collectors.toList());
    }
}
