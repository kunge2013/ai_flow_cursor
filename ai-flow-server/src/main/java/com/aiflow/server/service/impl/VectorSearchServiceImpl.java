package com.aiflow.server.service.impl;

import com.aiflow.server.dto.VectorSearchDtos.*;
import com.aiflow.server.service.VectorSearchService;
import com.aiflow.aimodel.service.VectorService;
import com.aiflow.aimodel.service.RagService;
import com.aiflow.aimodel.service.VectorService.VectorEmbeddingResult;
import com.aiflow.aimodel.service.VectorService.VectorSearchResult;
import com.aiflow.aimodel.service.VectorService.VectorStatistics;
import com.aiflow.aimodel.dto.VectorDtos;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@Service
public class VectorSearchServiceImpl implements VectorSearchService {

    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final VectorService vectorService;
    private final RagService ragService;
    
    @Value("${ai.model.zhipu-ai.api-key:}")
    private String zhipuApiKey;
    
    @Value("${ai.model.zhipu-ai.embedding-model:text-embedding-v2}")
    private String zhipuEmbeddingModel;
    
    @Value("${ai.model.openai.api-key:}")
    private String openaiApiKey;
    
    @Value("${ai.model.openai.embedding-model:text-embedding-ada-002}")
    private String openaiEmbeddingModel;
    
    @Value("${ai.model.ollama.base-url:http://localhost:11434}")
    private String ollamaBaseUrl;

    public VectorSearchServiceImpl(VectorService vectorService, RagService ragService) {
        this.vectorService = vectorService;
        this.ragService = ragService;
    }

    @Override
    public VectorEmbeddingResponse embedDocument(String content, String vectorModel) {
        try {
            log.info("开始向量化文档，模型: {}, 内容长度: {}", vectorModel, content.length());
            
            // 使用新的向量服务
            VectorEmbeddingResult result = vectorService.embedDocument(content, vectorModel);
            
            // 转换为响应对象
            VectorEmbeddingResponse response = new VectorEmbeddingResponse();
            response.setContent(result.getContent());
            response.setEmbedding(result.getEmbedding());
            response.setVectorModel(result.getVectorModel());
            response.setTokenCount(result.getTokenCount());
            response.setCost(result.getCost());
            
            log.info("文档向量化完成，向量维度: {}", result.getEmbedding().size());
            return response;
            
        } catch (Exception e) {
            log.error("文档向量化失败", e);
            throw new RuntimeException("文档向量化失败: " + e.getMessage());
        }
    }

    @Override
    public VectorSearchResponse searchSimilar(String query, String kbId, int topK, double scoreThreshold) {
        try {
            log.info("开始使用新的向量服务进行相似度搜索，查询: {}, 知识库: {}, topK: {}, 阈值: {}", 
                    query, kbId, topK, scoreThreshold);
            
            // 使用新的向量服务进行搜索
            VectorSearchResult result = vectorService.searchSimilar(query, kbId, topK, scoreThreshold);
            
            // 构建响应
            VectorSearchResponse response = new VectorSearchResponse();
            response.setQuery(query);
            
            if (result != null) {
                response.setTotalCount(1L);
                List<com.aiflow.server.dto.VectorSearchDtos.VectorSearchResult> results = new ArrayList<>();
                results.add(convertToVectorSearchResult(result));
                response.setResults(results);
            } else {
                response.setTotalCount(0L);
                response.setResults(new ArrayList<>());
            }
            
            response.setSearchTime(System.currentTimeMillis());
            
            log.info("向量搜索完成，找到 {} 个结果", response.getTotalCount());
            return response;
            
        } catch (Exception e) {
            log.error("向量搜索失败", e);
            throw new RuntimeException("向量搜索失败: " + e.getMessage());
        }
    }

    @Override
    public List<VectorEmbeddingResponse> batchEmbedDocuments(List<String> contents, String vectorModel) {
        try {
            log.info("开始批量向量化文档，数量: {}, 模型: {}", contents.size(), vectorModel);
            
            // 使用新的向量服务进行批量处理
            List<VectorEmbeddingResult> results = vectorService.batchEmbedDocuments(contents, vectorModel);
            
            // 转换为响应对象
            List<VectorEmbeddingResponse> responses = results.stream()
                    .map(this::convertToVectorEmbeddingResponse)
                    .collect(Collectors.toList());
            
            log.info("批量向量化完成，成功处理 {} 个文档", responses.size());
            return responses;
            
        } catch (Exception e) {
            log.error("批量向量化失败", e);
            throw new RuntimeException("批量向量化失败: " + e.getMessage());
        }
    }

    @Override
    public void saveDocumentVector(String documentId, String kbId, String content, String vectorModel) {
        try {
            log.info("开始保存文档向量，文档ID: {}, 知识库: {}, 模型: {}", documentId, kbId, vectorModel);
            
            // 使用新的向量服务保存文档向量
            vectorService.saveDocumentVector(documentId, kbId, content, vectorModel);
            
            log.info("文档向量保存成功，文档ID: {}", documentId);
            
        } catch (Exception e) {
            log.error("保存文档向量失败，文档ID: {}", documentId, e);
            throw new RuntimeException("保存文档向量失败: " + e.getMessage());
        }
    }

    @Override
    public void deleteDocumentVector(String documentId) {
        try {
            log.info("开始删除文档向量，文档ID: {}", documentId);
            
            // 使用新的向量服务删除文档向量
            vectorService.deleteDocumentVector(documentId);
            
            log.info("文档向量删除成功，文档ID: {}", documentId);
            
        } catch (Exception e) {
            log.error("删除文档向量失败，文档ID: {}", documentId, e);
            throw new RuntimeException("删除文档向量失败: " + e.getMessage());
        }
    }

    @Override
    public void updateDocumentVector(String documentId, String content, String vectorModel) {
        try {
            log.info("开始更新文档向量，文档ID: {}, 模型: {}", documentId, vectorModel);
            
            // 使用新的向量服务更新文档向量
            vectorService.updateDocumentVector(documentId, content, vectorModel);
            
            log.info("文档向量更新成功，文档ID: {}", documentId);
            
        } catch (Exception e) {
            log.error("更新文档向量失败，文档ID: {}", documentId, e);
            throw new RuntimeException("更新文档向量失败: " + e.getMessage());
        }
    }

    /**
     * 批量保存文档向量
     */
    public void batchSaveDocumentVectors(List<DocumentVectorRequest> requests) {
        try {
            log.info("开始批量保存文档向量，数量: {}", requests.size());
            
            // 使用线程池并行处理
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            
            for (DocumentVectorRequest request : requests) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        vectorService.saveDocumentVector(
                            request.getDocumentId(), 
                            request.getKbId(), 
                            request.getContent(), 
                            request.getVectorModel()
                        );
                    } catch (Exception e) {
                        log.error("批量保存文档向量失败，文档ID: {}", request.getDocumentId(), e);
                    }
                }, executorService);
                
                futures.add(future);
            }
            
            // 等待所有任务完成
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
            );
            
            allFutures.join();
            
            log.info("批量保存文档向量完成，成功处理 {} 个文档", requests.size());
            
        } catch (Exception e) {
            log.error("批量保存文档向量失败", e);
            throw new RuntimeException("批量保存文档向量失败: " + e.getMessage());
        }
    }

    /**
     * 获取文档向量信息
     */
    public DocumentVectorInfo getDocumentVector(String documentId) {
        try {
            log.info("获取文档向量信息，文档ID: {}", documentId);
            
            // 这里需要根据实际情况实现
            // 暂时返回模拟数据
            DocumentVectorInfo info = new DocumentVectorInfo();
            info.setDocumentId(documentId);
            info.setLastUpdated(System.currentTimeMillis());
            
            log.info("获取文档向量信息成功，文档ID: {}", documentId);
            return info;
            
        } catch (Exception e) {
            log.error("获取文档向量信息失败，文档ID: {}", documentId, e);
            throw new RuntimeException("获取文档向量信息失败: " + e.getMessage());
        }
    }

    /**
     * 搜索相似文档（增强版）
     */
    public EnhancedVectorSearchResponse enhancedSearchSimilar(String query, String kbId, int topK, double scoreThreshold) {
        try {
            log.info("开始增强相似度搜索，查询: {}, 知识库: {}, topK: {}, 阈值: {}", 
                    query, kbId, topK, scoreThreshold);
            
            // 使用新的向量服务进行搜索
            VectorSearchResult result = vectorService.searchSimilar(query, kbId, topK, scoreThreshold);
            
            // 构建增强响应
            EnhancedVectorSearchResponse enhancedResponse = new EnhancedVectorSearchResponse();
            enhancedResponse.setQuery(query);
            enhancedResponse.setKbId(kbId);
            
            if (result != null) {
                List<com.aiflow.server.dto.VectorSearchDtos.VectorSearchResult> results = new ArrayList<>();
                results.add(convertToVectorSearchResult(result));
                enhancedResponse.setResults(results);
                enhancedResponse.setTotalResults(1);
            } else {
                enhancedResponse.setResults(new ArrayList<>());
                enhancedResponse.setTotalResults(0);
            }
            
            enhancedResponse.setSearchTime(System.currentTimeMillis());
            enhancedResponse.setVectorModel(getVectorModelByKbId(kbId));
            
            // 添加搜索统计信息
            SearchStatistics stats = new SearchStatistics();
            stats.setQueryLength(query.length());
            stats.setResultCount(enhancedResponse.getTotalResults());
            if (enhancedResponse.getTotalResults() > 0) {
                stats.setAverageScore(enhancedResponse.getResults().get(0).getScore());
            } else {
                stats.setAverageScore(0.0);
            }
            enhancedResponse.setStatistics(stats);
            
            log.info("增强相似度搜索完成，找到 {} 个结果", enhancedResponse.getTotalResults());
            return enhancedResponse;
            
        } catch (Exception e) {
            log.error("增强相似度搜索失败", e);
            throw new RuntimeException("增强相似度搜索失败: " + e.getMessage());
        }
    }

    /**
     * 转换VectorSearchResult为VectorSearchResult
     */
    private com.aiflow.server.dto.VectorSearchDtos.VectorSearchResult convertToVectorSearchResult(com.aiflow.aimodel.service.VectorService.VectorSearchResult result) {
        com.aiflow.server.dto.VectorSearchDtos.VectorSearchResult response = new com.aiflow.server.dto.VectorSearchDtos.VectorSearchResult();
        response.setTitle(result.getTitle());
        response.setContent(result.getContent());
        response.setScore(result.getScore());
        response.setDocumentId(result.getDocumentId());
        response.setMetadata(result.getKbId()); // 使用metadata字段存储kbId
        return response;
    }

    /**
     * 转换VectorEmbeddingResult为VectorEmbeddingResponse
     */
    private VectorEmbeddingResponse convertToVectorEmbeddingResponse(VectorEmbeddingResult result) {
        VectorEmbeddingResponse response = new VectorEmbeddingResponse();
        response.setContent(result.getContent());
        response.setEmbedding(result.getEmbedding());
        response.setVectorModel(result.getVectorModel());
        response.setTokenCount(result.getTokenCount());
        response.setCost(result.getCost());
        // VectorEmbeddingResponse没有documentId字段，所以不设置
        return response;
    }

    /**
     * 获取知识库的向量模型
     */
    private String getVectorModelByKbId(String kbId) {
        try {
            // 这里应该查询数据库获取向量模型
            // 目前返回默认值
            return "openai";
            
        } catch (Exception e) {
            log.error("获取向量模型失败，知识库ID: {}", kbId, e);
            return "openai";
        }
    }
} 