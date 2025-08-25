package com.aiflow.server.service;

import com.aiflow.server.dto.KbDtos.*;
import com.aiflow.server.dto.VectorSearchDtos.*;
import com.aiflow.server.entity.KnowledgeBase;
import com.aiflow.server.entity.VectorDocument;
import com.aiflow.server.exception.NotFoundException;
import com.aiflow.server.mapper.KnowledgeBaseMapper;
import com.aiflow.server.mapper.VectorDocumentMapper;
import com.aiflow.server.service.VectorSearchService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KbService {

    private final KnowledgeBaseMapper knowledgeBaseMapper;
    private final VectorDocumentMapper vectorDocumentMapper;
    private final VectorSearchService vectorSearchService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public KnowledgeBasePageResponse list(KnowledgeBaseQueryRequest req) {
        // 创建分页对象
        Page<KnowledgeBase> page = new Page<>(req.page, req.size);
        
        // 构建查询条件
        LambdaQueryWrapper<KnowledgeBase> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(req.name)) {
            wrapper.like(KnowledgeBase::getName, req.name);
        }
        if (StringUtils.hasText(req.vectorModel)) {
            wrapper.eq(KnowledgeBase::getVectorModel, req.vectorModel);
        }
        if (req.status != null) {
            wrapper.eq(KnowledgeBase::getStatus, req.status);
        }
        wrapper.orderByDesc(KnowledgeBase::getCreatedAt);
        
        // 执行分页查询
        IPage<KnowledgeBase> result = knowledgeBaseMapper.selectPage(page, wrapper);
        
        // 转换为 DTO
        List<KnowledgeBaseSummary> records = result.getRecords().stream()
                .map(this::convertToSummary)
                .collect(Collectors.toList());
        
        KnowledgeBasePageResponse response = new KnowledgeBasePageResponse();
        response.records = records;
        response.total = result.getTotal();
        response.page = req.page;
        response.size = req.size;
        
        return response;
    }

    @Transactional
    public KnowledgeBaseSummary create(KnowledgeBaseUpsertRequest req) {
        KnowledgeBase kb = new KnowledgeBase();
        kb.setId(IdService.newId());
        kb.setName(req.name);
        kb.setDescription(req.description);
        kb.setVectorModel(req.vectorModel != null ? req.vectorModel : "openai");
        kb.setStatus(req.status != null ? req.status : true);
        kb.setCreatedAt(LocalDateTime.now());
        kb.setUpdatedAt(kb.getCreatedAt());
        
        // 处理标签
        if (req.tags != null && !req.tags.isEmpty()) {
            try {
                kb.setTags(objectMapper.writeValueAsString(req.tags));
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize tags", e);
                kb.setTags("[]");
            }
        } else {
            kb.setTags("[]");
        }
        
        // 保存到数据库
        knowledgeBaseMapper.insert(kb);
        
        return convertToSummary(kb);
    }

    public KnowledgeBaseSummary get(String id) {
        KnowledgeBase kb = knowledgeBaseMapper.selectById(id);
        if (kb == null) {
            throw new NotFoundException("KB not found: " + id);
        }
        return convertToSummary(kb);
    }

    @Transactional
    public KnowledgeBaseSummary update(String id, KnowledgeBaseUpsertRequest req) {
        KnowledgeBase kb = knowledgeBaseMapper.selectById(id);
        if (kb == null) {
            throw new NotFoundException("KB not found: " + id);
        }
        
        kb.setName(req.name);
        kb.setDescription(req.description);
        kb.setVectorModel(req.vectorModel != null ? req.vectorModel : "openai");
        kb.setStatus(req.status != null ? req.status : true);
        kb.setUpdatedAt(LocalDateTime.now());
        
        // 处理标签
        if (req.tags != null && !req.tags.isEmpty()) {
            try {
                kb.setTags(objectMapper.writeValueAsString(req.tags));
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize tags", e);
                kb.setTags("[]");
            }
        } else {
            kb.setTags("[]");
        }
        
        // 更新数据库
        knowledgeBaseMapper.updateById(kb);
        
        return convertToSummary(kb);
    }

    @Transactional
    public void delete(String id) {
        KnowledgeBase kb = knowledgeBaseMapper.selectById(id);
        if (kb == null) {
            throw new NotFoundException("KB not found: " + id);
        }
        
        // 删除知识库（逻辑删除）
        knowledgeBaseMapper.deleteById(id);
        
        // 删除相关文档（逻辑删除）
        vectorDocumentMapper.deleteByKbId(id);
    }

    // 文档管理相关方法
    public List<DocumentInfo> getDocuments(String kbId) {
        if (knowledgeBaseMapper.selectById(kbId) == null) {
            throw new NotFoundException("KB not found: " + kbId);
        }
        
        List<VectorDocument> documents = vectorDocumentMapper.selectByKbId(kbId);
        return documents.stream()
                .map(this::convertToDocumentInfo)
                .collect(Collectors.toList());
    }

    @Transactional
    public DocumentInfo addDocument(String kbId, DocumentUploadRequest req) {
        if (knowledgeBaseMapper.selectById(kbId) == null) {
            throw new NotFoundException("KB not found: " + kbId);
        }

        // 获取知识库信息以确定向量模型
        KnowledgeBase kb = knowledgeBaseMapper.selectById(kbId);
        String vectorModel = kb.getVectorModel() != null ? kb.getVectorModel() : "zhipu";

        VectorDocument doc = new VectorDocument();
        doc.setId(IdService.newId());
        doc.setKbId(kbId);
        doc.setTitle(req.title);
        doc.setContent(req.content);
        doc.setContentType(req.type != null ? req.type : "text");
        doc.setFileSize(req.size != null ? req.size : req.content.length());
        doc.setVectorModel(vectorModel);
        doc.setStatus("processing"); // 设置为处理中状态
        doc.setCreatedAt(LocalDateTime.now());
        doc.setUpdatedAt(doc.getCreatedAt());
        
        // 保存到数据库
        vectorDocumentMapper.insert(doc);
        
        // 异步进行向量化处理
        try {
            // 调用向量化服务
            vectorSearchService.saveDocumentVector(doc.getId(), kbId, req.content, vectorModel);
            
            // 更新状态为完成
            doc.setStatus("completed");
            doc.setUpdatedAt(LocalDateTime.now());
            vectorDocumentMapper.updateById(doc);
            
            log.info("文档向量化完成: {}", doc.getId());
        } catch (Exception e) {
            log.error("文档向量化失败: {}", doc.getId(), e);
            doc.setStatus("failed");
            doc.setUpdatedAt(LocalDateTime.now());
            vectorDocumentMapper.updateById(doc);
        }
        
        return convertToDocumentInfo(doc);
    }

    @Transactional
    public void deleteDocument(String kbId, String documentId) {
        if (knowledgeBaseMapper.selectById(kbId) == null) {
            throw new NotFoundException("KB not found: " + kbId);
        }

        VectorDocument doc = vectorDocumentMapper.selectById(documentId);
        if (doc == null || !doc.getKbId().equals(kbId)) {
            throw new NotFoundException("Document not found: " + documentId);
        }
        
        // 逻辑删除文档
        vectorDocumentMapper.deleteById(documentId);
    }

    // 命中测试方法
    public TestQueryResponse testQuery(String kbId, TestQueryRequest req) {
        if (knowledgeBaseMapper.selectById(kbId) == null) {
            throw new NotFoundException("KB not found: " + kbId);
        }

        try {
            // 使用向量搜索服务进行相似度搜索
            VectorSearchResponse searchResponse = vectorSearchService.searchSimilar(
                req.query, kbId, req.topK, req.scoreThreshold
            );
            
            // 转换为DocumentInfo列表
            List<DocumentInfo> documents = new ArrayList<>();
            List<Double> scores = new ArrayList<>();
            
            for (VectorSearchResult result : searchResponse.getResults()) {
                DocumentInfo docInfo = new DocumentInfo();
                docInfo.id = result.getDocumentId();
                docInfo.title = result.getTitle();
                docInfo.content = result.getContent();
                docInfo.type = "text";
                docInfo.size = (long) result.getContent().length();
                docInfo.createdAt = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant();
                docInfo.updatedAt = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant();
                
                documents.add(docInfo);
                scores.add(result.getScore());
            }
            
            // 生成答案
            String answer = generateAnswer(req.query, documents);
            
            TestQueryResponse response = new TestQueryResponse();
            response.query = req.query;
            response.documents = documents;
            response.scores = scores;
            response.answer = answer;
            
            return response;
            
        } catch (Exception e) {
            log.error("向量搜索失败，回退到关键词搜索", e);
            
            // 回退到关键词搜索
            List<VectorDocument> docs = vectorDocumentMapper.selectByKbId(kbId);
            if (docs.isEmpty()) {
                TestQueryResponse response = new TestQueryResponse();
                response.query = req.query;
                response.documents = new ArrayList<>();
                response.scores = new ArrayList<>();
                response.answer = "知识库中暂无文档，无法进行查询。";
                return response;
            }

            // 简单的相似度计算（这里使用关键词匹配作为示例）
            List<VectorDocument> matchedDocs = new ArrayList<>();
            List<Double> keywordScores = new ArrayList<>();

            for (VectorDocument doc : docs) {
                double score = calculateSimilarity(req.query, doc.getContent());
                if (score >= req.scoreThreshold) {
                    matchedDocs.add(doc);
                    keywordScores.add(score);
                }
            }

            // 按分数排序并限制数量
            List<Integer> sortedIndices = new ArrayList<>();
            for (int i = 0; i < keywordScores.size(); i++) {
                sortedIndices.add(i);
            }
            sortedIndices.sort((a, b) -> Double.compare(keywordScores.get(b), keywordScores.get(a)));

            List<DocumentInfo> topDocs = new ArrayList<>();
            List<Double> topScores = new ArrayList<>();
            int count = Math.min(req.topK, sortedIndices.size());
            
            for (int i = 0; i < count; i++) {
                int idx = sortedIndices.get(i);
                topDocs.add(convertToDocumentInfo(matchedDocs.get(idx)));
                topScores.add(keywordScores.get(idx));
            }

            // 生成答案（这里使用简单的模板）
            String answer = generateAnswer(req.query, topDocs);

            TestQueryResponse response = new TestQueryResponse();
            response.query = req.query;
            response.documents = topDocs;
            response.scores = topScores;
            response.answer = answer;

            return response;
        }
    }

    // 转换方法
    private KnowledgeBaseSummary convertToSummary(KnowledgeBase kb) {
        KnowledgeBaseSummary summary = new KnowledgeBaseSummary();
        summary.id = kb.getId();
        summary.name = kb.getName();
        summary.description = kb.getDescription();
        summary.vectorModel = kb.getVectorModel();
        summary.status = kb.getStatus();
        summary.createdAt = kb.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant();
        summary.updatedAt = kb.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant();
        
        // 解析标签
        if (StringUtils.hasText(kb.getTags())) {
            try {
                summary.tags = objectMapper.readValue(kb.getTags(), new TypeReference<List<String>>() {});
            } catch (JsonProcessingException e) {
                log.error("Failed to deserialize tags", e);
                summary.tags = new ArrayList<>();
            }
        } else {
            summary.tags = new ArrayList<>();
        }
        
        return summary;
    }

    private DocumentInfo convertToDocumentInfo(VectorDocument doc) {
        DocumentInfo info = new DocumentInfo();
        info.id = doc.getId();
        info.title = doc.getTitle();
        info.content = doc.getContent();
        info.type = doc.getContentType();
        info.size = doc.getFileSize();
        info.createdAt = doc.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant();
        info.updatedAt = doc.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant();
        return info;
    }

    private double calculateSimilarity(String query, String content) {
        if (query == null || content == null) return 0.0;
        
        String[] queryWords = query.toLowerCase().split("\\s+");
        String[] contentWords = content.toLowerCase().split("\\s+");
        
        int matches = 0;
        for (String queryWord : queryWords) {
            for (String contentWord : contentWords) {
                if (contentWord.contains(queryWord) || queryWord.contains(contentWord)) {
                    matches++;
                    break;
                }
            }
        }
        
        return queryWords.length > 0 ? (double) matches / queryWords.length : 0.0;
    }

    private String generateAnswer(String query, List<DocumentInfo> docs) {
        if (docs.isEmpty()) {
            return "抱歉，没有找到相关的文档信息。";
        }

        StringBuilder answer = new StringBuilder();
        answer.append("根据查询「").append(query).append("」，找到以下相关信息：\n\n");
        
        for (int i = 0; i < docs.size(); i++) {
            DocumentInfo doc = docs.get(i);
            answer.append(i + 1).append(". ").append(doc.title).append("\n");
            answer.append("   内容：").append(doc.content.length() > 100 ? 
                doc.content.substring(0, 100) + "..." : doc.content).append("\n\n");
        }
        
        return answer.toString();
    }
} 