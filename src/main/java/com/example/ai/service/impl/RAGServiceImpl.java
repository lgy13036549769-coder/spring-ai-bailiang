package com.example.ai.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.ai.model.KnowledgeBase;
import com.example.ai.model.KnowledgeDocument;
import com.example.ai.model.request.RAGRequest;
import com.example.ai.model.response.RAGResponse;
import com.example.ai.model.vo.Document;
import com.example.ai.service.RAGService;

/**
 * RAG知识库服务实现类，实现知识库检索功能
 * 使用内存存储虚拟文档数据，无需数据库
 */
@Service
public class RAGServiceImpl implements RAGService {
    
    private static final Logger log = LoggerFactory.getLogger(RAGServiceImpl.class);
    
    // 内存中的文档库
    private final List<Document> documentStore = new ArrayList<>();
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    
    // 在构造函数中初始化虚拟文档
    public RAGServiceImpl() {
        initDocumentStore();
    }
    
    /**
     * 初始化文档库 - 使用真实的企业知识库数据
     */
    private void initDocumentStore() {
        log.info("开始初始化企业知识库...");
        long startTime = System.currentTimeMillis();
        
        // 从 KnowledgeBase 获取所有文档
        List<KnowledgeDocument> knowledgeDocs = KnowledgeBase.getAllDocuments();
        
        // 转换为 Document 对象
        for (KnowledgeDocument kbDoc : knowledgeDocs) {
            Document doc = convertToDocument(kbDoc);
            documentStore.add(doc);
        }
        
        long initTime = System.currentTimeMillis() - startTime;
        log.info("企业知识库初始化完成，共加载 {} 个文档，耗时: {}ms", documentStore.size(), initTime);
    }
    
    /**
     * 将 KnowledgeDocument 转换为 Document
     */
    private Document convertToDocument(KnowledgeDocument kbDoc) {
        Document doc = new Document();
        doc.setId(kbDoc.getId());
        doc.setTitle(kbDoc.getTitle());
        doc.setContent(kbDoc.getContent());
        doc.setCategory(kbDoc.getCategory());
        doc.setCreatedAt(kbDoc.getCreatedAt());
        doc.setKeywords(extractKeywords(kbDoc.getContent()));
        return doc;
    }
    
    /**
     * 简单的关键词提取逻辑 - 支持中文
     */
    private Map<String, Double> extractKeywords(String content) {
        Map<String, Double> keywords = new HashMap<>();
        
        if (content == null || content.isEmpty()) {
            return keywords;
        }
        
        // 提取中文词汇(2-4个字的组合)
        List<String> chineseWords = new ArrayList<>();
        
        // 移除标点符号和特殊字符
        String cleanContent = content.replaceAll("[^\\w\\s\\u4e00-\\u9fa5]", " ");
        
        // 提取英文单词
        String[] englishWords = cleanContent.toLowerCase().split("\\s+");
        for (String word : englishWords) {
            if (word.length() > 1 && !word.matches("[\\u4e00-\\u9fa5]+")) {
                chineseWords.add(word);
            }
        }
        
        // 提取中文词语(2-4字组合)
        String chineseOnly = content.replaceAll("[^\\u4e00-\\u9fa5]", "");
        for (int len = 2; len <= 4 && len <= chineseOnly.length(); len++) {
            for (int i = 0; i <= chineseOnly.length() - len; i++) {
                String word = chineseOnly.substring(i, i + len);
                chineseWords.add(word);
            }
        }
        
        // 计算词频
        Map<String, Integer> wordCount = new HashMap<>();
        for (String word : chineseWords) {
            wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
        }
        
        // 计算权重
        int totalWords = wordCount.values().stream().mapToInt(Integer::intValue).sum();
        if (totalWords > 0) {
            for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
                keywords.put(entry.getKey(), (double) entry.getValue() / totalWords);
            }
        }
        
        return keywords;
    }
    
    @Override
    public RAGResponse search(RAGRequest request) {
        log.info("========== RAG知识库检索开始 ==========");
        
        String query = request.getQuery();
        int topK = request.getTopK();
        
        log.info("查询内容: {}", query);
        log.info("返回数量(topK): {}", topK);
        log.info("文档库总数: {}", documentStore.size());
        
        long startTime = System.currentTimeMillis();
        
        // 提取查询的关键词
        log.debug("开始提取查询关键词...");
        Map<String, Double> queryKeywords = extractKeywords(query);
        log.debug("提取到 {} 个关键词", queryKeywords.size());
        
        // 计算每个文档与查询的相关性分数
        log.debug("开始计算文档相关性分数...");
        List<Map.Entry<Document, Double>> scoredDocs = new ArrayList<>();
        for (Document doc : documentStore) {
            double score = calculateRelevanceScore(doc.getKeywords(), queryKeywords);
            if (score > 0) {
                scoredDocs.add(new AbstractMap.SimpleEntry<>(doc, score));
            }
        }
        log.debug("找到 {} 个相关文档", scoredDocs.size());
        
        // 按相关性分数降序排序
        scoredDocs.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        
        // 取前K个最相关的文档
        List<Document> results = new ArrayList<>();
        double totalScore = 0;
        for (int i = 0; i < Math.min(topK, scoredDocs.size()); i++) {
            Document doc = scoredDocs.get(i).getKey();
            double score = scoredDocs.get(i).getValue();
            results.add(doc);
            totalScore += score;
            log.debug("文档 #{}: {} (分数: {:.4f})", i + 1, doc.getTitle(), score);
        }
        
        long searchTime = System.currentTimeMillis() - startTime;
        
        // 构建响应
        RAGResponse response = new RAGResponse();
        response.setQuery(query);
        response.setDocuments(results);
        response.setTotalScore(totalScore);
        
        log.info("RAG检索完成，返回 {} 个文档，总分数: {:.4f}，耗时: {}ms", 
                results.size(), totalScore, searchTime);
        log.info("========== RAG知识库检索结束 ==========\n");
        
        return response;
    }
    
    /**
     * 计算文档与查询的相关性分数
     */
    private double calculateRelevanceScore(Map<String, Double> docKeywords, Map<String, Double> queryKeywords) {
        double score = 0;
        
        // 简单的相关性计算：关键词权重的点积
        for (Map.Entry<String, Double> queryEntry : queryKeywords.entrySet()) {
            String keyword = queryEntry.getKey();
            if (docKeywords.containsKey(keyword)) {
                score += docKeywords.get(keyword) * queryEntry.getValue();
            }
        }
        
        // 归一化分数
        if (score > 0) {
            double docNorm = Math.sqrt(docKeywords.values().stream()
                    .mapToDouble(w -> w * w).sum());
            double queryNorm = Math.sqrt(queryKeywords.values().stream()
                    .mapToDouble(w -> w * w).sum());
            
            if (docNorm > 0 && queryNorm > 0) {
                score = score / (docNorm * queryNorm);
            }
        }
        
        return score;
    }
}