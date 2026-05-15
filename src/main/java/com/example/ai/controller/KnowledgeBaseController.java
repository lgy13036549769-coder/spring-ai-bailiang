package com.example.ai.controller;

import com.example.ai.model.KnowledgeDocument;
import com.example.ai.model.response.CommonResponse;
import com.example.ai.service.KnowledgeBaseService;
import com.example.ai.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 知识库管理控制器
 */
@RestController
@RequestMapping("/knowledge")
public class KnowledgeBaseController {
    
    private static final Logger log = LoggerFactory.getLogger(KnowledgeBaseController.class);
    
    private final KnowledgeBaseService knowledgeBaseService;
    
    @Autowired
    public KnowledgeBaseController(KnowledgeBaseService knowledgeBaseService) {
        this.knowledgeBaseService = knowledgeBaseService;
    }
    
    /**
     * 获取所有文档
     */
    @GetMapping("/documents")
    public ResponseEntity<CommonResponse<List<KnowledgeDocument>>> getAllDocuments() {
        log.info("获取所有知识库文档");
        List<KnowledgeDocument> documents = knowledgeBaseService.getAllDocuments();
        return ResponseUtil.success(documents);
    }
    
    /**
     * 根据ID获取文档
     */
    @GetMapping("/documents/{id}")
    public ResponseEntity<CommonResponse<KnowledgeDocument>> getDocumentById(@PathVariable String id) {
        log.info("获取文档详情: {}", id);
        KnowledgeDocument document = knowledgeBaseService.getDocumentById(id);
        
        if (document == null) {
            return ResponseUtil.error("文档不存在: " + id);
        }
        
        return ResponseUtil.success(document);
    }
    
    /**
     * 添加新文档
     */
    @PostMapping("/documents")
    public ResponseEntity<CommonResponse<KnowledgeDocument>> addDocument(@RequestBody KnowledgeDocument document) {
        log.info("添加新文档: {}", document != null ? document.getTitle() : "null");
        
        try {
            KnowledgeDocument newDoc = knowledgeBaseService.addDocument(document);
            return ResponseUtil.success(newDoc);
        } catch (IllegalArgumentException e) {
            return ResponseUtil.error(e.getMessage());
        }
    }
    
    /**
     * 更新文档
     */
    @PutMapping("/documents/{id}")
    public ResponseEntity<CommonResponse<KnowledgeDocument>> updateDocument(
            @PathVariable String id, 
            @RequestBody KnowledgeDocument document) {
        log.info("更新文档: {}", id);
        
        try {
            KnowledgeDocument updatedDoc = knowledgeBaseService.updateDocument(id, document);
            return ResponseUtil.success(updatedDoc);
        } catch (IllegalArgumentException e) {
            return ResponseUtil.error(e.getMessage());
        }
    }
    
    /**
     * 删除文档
     */
    @DeleteMapping("/documents/{id}")
    public ResponseEntity<CommonResponse<Void>> deleteDocument(@PathVariable String id) {
        log.info("删除文档: {}", id);
        
        boolean deleted = knowledgeBaseService.deleteDocument(id);
        
        if (deleted) {
            return ResponseUtil.success(null);
        } else {
            return ResponseUtil.error("文档不存在: " + id);
        }
    }
    
    /**
     * 根据分类获取文档
     */
    @GetMapping("/documents/category/{category}")
    public ResponseEntity<CommonResponse<List<KnowledgeDocument>>> getDocumentsByCategory(@PathVariable String category) {
        log.info("按分类获取文档: {}", category);
        List<KnowledgeDocument> documents = knowledgeBaseService.getDocumentsByCategory(category);
        return ResponseUtil.success(documents);
    }
    
    /**
     * 搜索文档
     */
    @GetMapping("/documents/search")
    public ResponseEntity<CommonResponse<List<KnowledgeDocument>>> searchDocuments(@RequestParam String keyword) {
        log.info("搜索文档: {}", keyword);
        List<KnowledgeDocument> documents = knowledgeBaseService.searchDocuments(keyword);
        return ResponseUtil.success(documents);
    }
}
