package com.aiflow.server.service;

import com.aiflow.server.dto.KbDtos.*;
import com.aiflow.server.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class KbService {

    private final Map<String, KnowledgeBaseSummary> kbStore = new ConcurrentHashMap<>();
    private final Map<String, List<DocumentInfo>> documentStore = new ConcurrentHashMap<>();

    public KnowledgeBasePageResponse list(KnowledgeBaseQueryRequest req) {
        List<KnowledgeBaseSummary> filteredList = kbStore.values().stream()
                .filter(k -> req.name == null || req.name.isBlank() || k.name.toLowerCase().contains(req.name.toLowerCase()))
                .filter(k -> req.vectorModel == null || req.vectorModel.isBlank() || req.vectorModel.equals(k.vectorModel))
                .filter(k -> req.status == null || req.status.equals(k.status))
                .sorted(Comparator.comparing((KnowledgeBaseSummary k) -> k.createdAt).reversed())
                .collect(Collectors.toList());

        long total = filteredList.size();
        int start = (req.page - 1) * req.size;
        int end = Math.min(start + req.size, filteredList.size());
        
        List<KnowledgeBaseSummary> records = filteredList.subList(start, end);

        KnowledgeBasePageResponse response = new KnowledgeBasePageResponse();
        response.records = records;
        response.total = total;
        response.page = req.page;
        response.size = req.size;
        
        return response;
    }

    public KnowledgeBaseSummary create(KnowledgeBaseUpsertRequest req) {
        KnowledgeBaseSummary k = new KnowledgeBaseSummary();
        k.id = IdService.newId();
        k.name = req.name;
        k.description = req.description;
        k.tags = req.tags != null ? req.tags : new ArrayList<>();
        k.vectorModel = req.vectorModel != null ? req.vectorModel : "openai";
        k.status = req.status != null ? req.status : true;
        k.createdAt = Instant.now();
        k.updatedAt = k.createdAt;
        kbStore.put(k.id, k);
        
        // 初始化文档存储
        documentStore.put(k.id, new ArrayList<>());
        
        return k;
    }

    public KnowledgeBaseSummary get(String id) {
        KnowledgeBaseSummary k = kbStore.get(id);
        if (k == null) throw new NotFoundException("KB not found: " + id);
        return k;
    }

    public KnowledgeBaseSummary update(String id, KnowledgeBaseUpsertRequest req) {
        KnowledgeBaseSummary k = get(id);
        k.name = req.name;
        k.description = req.description;
        k.tags = req.tags != null ? req.tags : new ArrayList<>();
        k.vectorModel = req.vectorModel != null ? req.vectorModel : "openai";
        k.status = req.status != null ? req.status : true;
        k.updatedAt = Instant.now();
        return k;
    }

    public void delete(String id) {
        if (kbStore.remove(id) == null) {
            throw new NotFoundException("KB not found: " + id);
        }
        // 删除相关文档
        documentStore.remove(id);
    }

    // 文档管理相关方法
    public List<DocumentInfo> getDocuments(String kbId) {
        if (!kbStore.containsKey(kbId)) {
            throw new NotFoundException("KB not found: " + kbId);
        }
        return documentStore.getOrDefault(kbId, new ArrayList<>());
    }

    public DocumentInfo addDocument(String kbId, DocumentUploadRequest req) {
        if (!kbStore.containsKey(kbId)) {
            throw new NotFoundException("KB not found: " + kbId);
        }

        DocumentInfo doc = new DocumentInfo();
        doc.id = IdService.newId();
        doc.title = req.title;
        doc.content = req.content;
        doc.type = req.type != null ? req.type : "text";
        doc.size = req.size != null ? req.size : req.content.length();
        doc.createdAt = Instant.now();
        doc.updatedAt = doc.createdAt;

        List<DocumentInfo> docs = documentStore.computeIfAbsent(kbId, k -> new ArrayList<>());
        docs.add(doc);
        
        return doc;
    }

    public void deleteDocument(String kbId, String documentId) {
        if (!kbStore.containsKey(kbId)) {
            throw new NotFoundException("KB not found: " + kbId);
        }

        List<DocumentInfo> docs = documentStore.get(kbId);
        if (docs != null) {
            docs.removeIf(doc -> doc.id.equals(documentId));
        }
    }

    // 命中测试方法
    public TestQueryResponse testQuery(String kbId, TestQueryRequest req) {
        if (!kbStore.containsKey(kbId)) {
            throw new NotFoundException("KB not found: " + kbId);
        }

        List<DocumentInfo> docs = documentStore.getOrDefault(kbId, new ArrayList<>());
        if (docs.isEmpty()) {
            TestQueryResponse response = new TestQueryResponse();
            response.query = req.query;
            response.documents = new ArrayList<>();
            response.scores = new ArrayList<>();
            response.answer = "知识库中暂无文档，无法进行查询。";
            return response;
        }

        // 简单的相似度计算（这里使用关键词匹配作为示例）
        List<DocumentInfo> matchedDocs = new ArrayList<>();
        List<Double> scores = new ArrayList<>();

        for (DocumentInfo doc : docs) {
            double score = calculateSimilarity(req.query, doc.content);
            if (score >= req.scoreThreshold) {
                matchedDocs.add(doc);
                scores.add(score);
            }
        }

        // 按分数排序并限制数量
        List<Integer> sortedIndices = new ArrayList<>();
        for (int i = 0; i < scores.size(); i++) {
            sortedIndices.add(i);
        }
        sortedIndices.sort((a, b) -> Double.compare(scores.get(b), scores.get(a)));

        List<DocumentInfo> topDocs = new ArrayList<>();
        List<Double> topScores = new ArrayList<>();
        int count = Math.min(req.topK, sortedIndices.size());
        
        for (int i = 0; i < count; i++) {
            int idx = sortedIndices.get(i);
            topDocs.add(matchedDocs.get(idx));
            topScores.add(scores.get(idx));
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