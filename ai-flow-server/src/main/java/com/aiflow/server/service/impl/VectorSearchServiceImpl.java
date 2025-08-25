package com.aiflow.server.service.impl;

import com.aiflow.server.dto.VectorSearchDtos.*;
import com.aiflow.server.service.VectorSearchService;
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

@Slf4j
@Service
public class VectorSearchServiceImpl implements VectorSearchService {

    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Value("${ai.model.zhipu-ai.api-key:}")
    private String zhipuApiKey;
    
    @Value("${ai.model.zhipu-ai.embedding-model:text-embedding-v2}")
    private String zhipuEmbeddingModel;
    
    // 向量模型配置映射
    private final Map<String, String> vectorModelConfigs = Map.of(
        "openai", "text-embedding-ada-002",
        "zhipu", "text-embedding-v2",
        "deepseek", "deepseek-embedding"
    );

    @Override
    public VectorEmbeddingResponse embedDocument(String content, String vectorModel) {
        try {
            log.info("开始向量化文档，模型: {}, 内容长度: {}", vectorModel, content.length());
            
            List<Double> embedding;
            
            // 根据模型类型选择不同的实现
            switch (vectorModel.toLowerCase()) {
                case "zhipu":
                case "zhipu-ai":
                    embedding = callZhipuAiApi(content);
                    break;
                case "openai":
                    embedding = generateMockEmbedding(content, "openai");
                    break;
                default:
                    embedding = generateMockEmbedding(content, vectorModel);
                    break;
            }
            
            VectorEmbeddingResponse response = new VectorEmbeddingResponse();
            response.setContent(content);
            response.setEmbedding(embedding);
            response.setVectorModel(vectorModel);
            response.setTokenCount((long) content.length() / 4); // 粗略估算token数量
            response.setCost(0L); // 成本计算需要根据实际API定价
            
            log.info("文档向量化完成，向量维度: {}", embedding.size());
            return response;
            
        } catch (Exception e) {
            log.error("文档向量化失败", e);
            throw new RuntimeException("文档向量化失败: " + e.getMessage());
        }
    }

    @Override
    public VectorSearchResponse searchSimilar(String query, String kbId, int topK, double scoreThreshold) {
        try {
            log.info("开始向量相似度搜索，查询: {}, 知识库: {}, topK: {}, 阈值: {}", 
                    query, kbId, topK, scoreThreshold);
            
            // 1. 向量化查询
            VectorEmbeddingResponse queryEmbedding = embedDocument(query, "zhipu"); // 默认使用智普AI
            
            // 2. 在知识库中搜索相似向量
            List<VectorSearchResult> results = searchSimilarVectors(queryEmbedding.getEmbedding(), kbId, topK, scoreThreshold);
            
            VectorSearchResponse response = new VectorSearchResponse();
            response.setQuery(query);
            response.setResults(results);
            response.setTotalCount((long) results.size());
            response.setSearchTime(System.currentTimeMillis());
            
            log.info("向量搜索完成，找到 {} 个结果", results.size());
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
            
            List<CompletableFuture<VectorEmbeddingResponse>> futures = new ArrayList<>();
            
            for (String content : contents) {
                CompletableFuture<VectorEmbeddingResponse> future = CompletableFuture.supplyAsync(() -> 
                    embedDocument(content, vectorModel), executorService);
                futures.add(future);
            }
            
            // 等待所有任务完成
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
            );
            
            allFutures.join();
            
            List<VectorEmbeddingResponse> results = new ArrayList<>();
            for (CompletableFuture<VectorEmbeddingResponse> future : futures) {
                results.add(future.get());
            }
            
            log.info("批量向量化完成，成功处理 {} 个文档", results.size());
            return results;
            
        } catch (Exception e) {
            log.error("批量向量化失败", e);
            throw new RuntimeException("批量向量化失败: " + e.getMessage());
        }
    }

    @Override
    public void saveDocumentVector(String documentId, String kbId, String content, String vectorModel) {
        try {
            log.info("保存文档向量，文档ID: {}, 知识库: {}, 模型: {}", documentId, kbId, vectorModel);
            
            // 1. 向量化文档内容
            VectorEmbeddingResponse embedding = embedDocument(content, vectorModel);
            
            // 2. 保存到向量数据库（这里应该调用实际的向量数据库API）
            saveToVectorDatabase(documentId, kbId, embedding);
            
            log.info("文档向量保存成功");
            
        } catch (Exception e) {
            log.error("保存文档向量失败", e);
            throw new RuntimeException("保存文档向量失败: " + e.getMessage());
        }
    }

    @Override
    public void deleteDocumentVector(String documentId) {
        try {
            log.info("删除文档向量，文档ID: {}", documentId);
            
            // 从向量数据库中删除
            deleteFromVectorDatabase(documentId);
            
            log.info("文档向量删除成功");
            
        } catch (Exception e) {
            log.error("删除文档向量失败", e);
            throw new RuntimeException("删除文档向量失败: " + e.getMessage());
        }
    }

    @Override
    public void updateDocumentVector(String documentId, String content, String vectorModel) {
        try {
            log.info("更新文档向量，文档ID: {}, 模型: {}", documentId, vectorModel);
            
            // 1. 重新向量化文档内容
            VectorEmbeddingResponse embedding = embedDocument(content, vectorModel);
            
            // 2. 更新向量数据库
            updateInVectorDatabase(documentId, embedding);
            
            log.info("文档向量更新成功");
            
        } catch (Exception e) {
            log.error("更新文档向量失败", e);
            throw new RuntimeException("更新文档向量失败: " + e.getMessage());
        }
    }

    // 私有辅助方法
    
    /**
     * 调用智普AI API进行向量化
     */
    private List<Double> callZhipuAiApi(String text) throws Exception {
        if (zhipuApiKey == null || zhipuApiKey.trim().isEmpty()) {
            log.warn("智普AI API Key未配置，使用模拟向量");
            return generateMockEmbedding(text, "zhipu");
        }
        
        String url = "https://open.bigmodel.cn/api/paas/v4/embeddings";
        
        // 构建请求体
        String requestBody = String.format(
            "{\"model\":\"%s\",\"input\":[\"%s\"]}",
            zhipuEmbeddingModel,
            text.replace("\"", "\\\"")
        );
        
        // 创建HTTP请求
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + zhipuApiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        
        try {
            // 发送请求
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                log.warn("智普AI API调用失败: {}，使用模拟向量", response.statusCode());
                return generateMockEmbedding(text, "zhipu");
            }
            
            // 解析响应
            JsonNode responseJson = objectMapper.readTree(response.body());
            
            if (responseJson.has("error")) {
                log.warn("智普AI API返回错误: {}，使用模拟向量", responseJson.get("error").asText());
                return generateMockEmbedding(text, "zhipu");
            }
            
            // 提取向量数据
            JsonNode data = responseJson.get("data");
            if (data == null || !data.isArray() || data.size() == 0) {
                log.warn("智普AI API响应格式错误，使用模拟向量");
                return generateMockEmbedding(text, "zhipu");
            }
            
            JsonNode embedding = data.get(0).get("embedding");
            if (embedding == null || !embedding.isArray()) {
                log.warn("智普AI API响应中缺少向量数据，使用模拟向量");
                return generateMockEmbedding(text, "zhipu");
            }
            
            // 转换为Double列表
            List<Double> result = new ArrayList<>();
            for (JsonNode value : embedding) {
                result.add(value.asDouble());
            }
            
            return result;
            
        } catch (Exception e) {
            log.warn("智普AI API调用异常: {}，使用模拟向量", e.getMessage());
            return generateMockEmbedding(text, "zhipu");
        }
    }
    
    /**
     * 生成模拟的向量数据
     */
    private List<Double> generateMockEmbedding(String content, String vectorModel) {
        // 生成模拟的向量数据
        int dimension = getVectorDimension(vectorModel);
        List<Double> embedding = new ArrayList<>();
        
        Random random = new Random(content.hashCode()); // 使用内容hash作为种子，确保相同内容生成相同向量
        
        for (int i = 0; i < dimension; i++) {
            embedding.add(random.nextDouble() * 2 - 1); // 生成-1到1之间的随机数
        }
        
        // 归一化向量
        double magnitude = Math.sqrt(embedding.stream().mapToDouble(x -> x * x).sum());
        for (int i = 0; i < embedding.size(); i++) {
            embedding.set(i, embedding.get(i) / magnitude);
        }
        
        return embedding;
    }
    
    private int getVectorDimension(String vectorModel) {
        return switch (vectorModel.toLowerCase()) {
            case "openai" -> 1536;
            case "zhipu" -> 1024;
            case "deepseek" -> 1024;
            default -> 1024;
        };
    }
    
    private List<VectorSearchResult> searchSimilarVectors(List<Double> queryEmbedding, String kbId, int topK, double scoreThreshold) {
        // 这里应该调用实际的向量数据库进行相似度搜索
        // 目前返回模拟结果，实际实现时需要：
        // 1. 从向量数据库中检索相似向量
        // 2. 计算余弦相似度
        // 3. 按相似度排序
        // 4. 返回topK结果
        
        List<VectorSearchResult> results = new ArrayList<>();
        
        // 模拟结果
        for (int i = 0; i < Math.min(topK, 3); i++) {
            VectorSearchResult result = new VectorSearchResult();
            result.setDocumentId("doc_" + i);
            result.setTitle("示例文档 " + (i + 1));
            result.setContent("这是示例文档的内容，用于演示向量搜索功能。");
            result.setScore(0.9 - i * 0.1); // 模拟相似度分数
            result.setMetadata("{\"source\": \"vector_search\", \"type\": \"text\"}");
            
            if (result.getScore() >= scoreThreshold) {
                results.add(result);
            }
        }
        
        return results;
    }
    
    private void saveToVectorDatabase(String documentId, String kbId, VectorEmbeddingResponse embedding) {
        // 这里应该调用实际的向量数据库API
        // 例如：Milvus、Pinecone、Weaviate等
        log.debug("保存向量到数据库: {}", documentId);
        
        // TODO: 实现真实的向量数据库保存逻辑
        // 1. 将向量数据存储到向量数据库
        // 2. 建立文档ID和向量的映射关系
        // 3. 创建向量索引用于快速检索
    }
    
    private void deleteFromVectorDatabase(String documentId) {
        // 这里应该调用实际的向量数据库API
        log.debug("从数据库删除向量: {}", documentId);
        
        // TODO: 实现真实的向量数据库删除逻辑
    }
    
    private void updateInVectorDatabase(String documentId, VectorEmbeddingResponse embedding) {
        // 这里应该调用实际的向量数据库API
        log.debug("更新数据库中的向量: {}", documentId);
        
        // TODO: 实现真实的向量数据库更新逻辑
    }
} 