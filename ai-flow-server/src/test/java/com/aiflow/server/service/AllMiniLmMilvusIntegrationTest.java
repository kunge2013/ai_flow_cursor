package com.aiflow.server.service;


import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import org.junit.jupiter.api.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Milvus 向量存储基本功能测试
 * 直接测试 Milvus 连接和基本功能
 */
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "ai.vectorstore.default-type=milvus",
    "ai.vectorstore.milvus.host=localhost",
    "ai.vectorstore.milvus.port=19530",
    "ai.vectorstore.milvus.collection-name=test_minilm", // 缩短集合名称
    "ai.vectorstore.milvus.dimension=384"
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AllMiniLmMilvusIntegrationTest {
    
    private MilvusEmbeddingStore embeddingStore;
    private String testCollectionName;
    
    @BeforeEach
    void setUp() {
        // 生成唯一的测试集合名称，使用更短的格式避免ID长度问题
        testCollectionName = "test_minilm_" + UUID.randomUUID().toString().substring(0, 8);
        
        // 直接创建 MilvusEmbeddingStore 实例
        this.embeddingStore = MilvusEmbeddingStore.builder()
                .host("localhost")
                .port(19530)
                .collectionName(testCollectionName)
                .dimension(384)
                .build();
    }
    
    @Test
    @Order(1)
    @DisplayName("测试 Milvus 向量存储基本功能")
    void testMilvusVectorStore() {
        // 创建测试向量（384维）
        float[] testVector = new float[384];
        for (int i = 0; i < 384; i++) {
            testVector[i] = (float) (Math.random() - 0.5);
        }
        Embedding testEmbedding = Embedding.from(testVector);
        
        // 测试添加单个向量
        String id = embeddingStore.add(testEmbedding);
        assertNotNull(id, "添加向量后应该返回有效的ID");
        assertFalse(id.isEmpty(), "向量ID不应该为空");
        assertTrue(id.length() <= 36, "向量ID长度应该不超过36个字符");
        System.out.println("✅ 单个向量添加成功，ID: " + id + " (长度: " + id.length() + ")");
        
        // 测试添加带ID的向量
        String customId = "custom_" + UUID.randomUUID().toString().substring(0, 20); // 限制ID长度为30字符以内
        embeddingStore.add(customId, testEmbedding);
        System.out.println("✅ 自定义ID向量添加成功，ID: " + customId + " (长度: " + customId.length() + ")");
        
        // 测试批量添加向量
        List<Embedding> batchEmbeddings = List.of(
            Embedding.from(createRandomVector(384)),
            Embedding.from(createRandomVector(384)),
            Embedding.from(createRandomVector(384))
        );
        List<String> batchIds = embeddingStore.addAll(batchEmbeddings);
        assertNotNull(batchIds, "批量添加应该返回ID列表");
        assertEquals(3, batchIds.size(), "应该返回 3 个ID");
        
        // 验证所有ID长度都不超过36个字符
        for (String batchId : batchIds) {
            assertTrue(batchId.length() <= 36, "批量添加的ID长度应该不超过36个字符");
            System.out.println("✅ 批量向量ID: " + batchId + " (长度: " + batchId.length() + ")");
        }
        
        // 测试批量添加带文本段落的向量
        List<TextSegment> textSegments = List.of(
            TextSegment.from("人工智能是计算机科学的一个分支"),
            TextSegment.from("机器学习是人工智能的重要技术"),
            TextSegment.from("深度学习是机器学习的一个子领域")
        );
        List<String> segmentIds = embeddingStore.addAll(batchEmbeddings, textSegments);
        assertNotNull(segmentIds, "批量添加文本段落应该返回ID列表");
        assertEquals(3, segmentIds.size(), "应该返回 3 个ID");
        
        // 验证所有ID长度都不超过36个字符
        for (String segmentId : segmentIds) {
            assertTrue(segmentId.length() <= 36, "文本段落ID长度应该不超过36个字符");
            System.out.println("✅ 文本段落ID: " + segmentId + " (长度: " + segmentId.length() + ")");
        }
        
        System.out.println("✅ Milvus 向量存储测试通过！所有ID长度都在36字符限制内。");
        System.out.println("✅ 测试集合名称: " + testCollectionName);
    }
    
    @Test
    @Order(2)
    @DisplayName("测试向量相似度搜索功能 (findRelevant)")
    void testFindRelevant() {
        // 创建一些测试向量
        float[] vector1 = createRandomVector(384);
        float[] vector2 = createRandomVector(384);
        float[] vector3 = createRandomVector(384);
        
        // 创建一个与vector1相似的向量（稍微修改一些值）
        float[] similarVector = vector1.clone();
        for (int i = 0; i < 10; i++) { // 只修改10个维度，保持相似性
            similarVector[i] += 0.1f;
        }
        
        // 添加向量到存储中
        String id1 = embeddingStore.add(Embedding.from(vector1));
        String id2 = embeddingStore.add(Embedding.from(vector2));
        String id3 = embeddingStore.add(Embedding.from(vector3));
        String similarId = embeddingStore.add(Embedding.from(similarVector));
        
        System.out.println("✅ 测试向量已添加，ID: " + id1 + ", " + id2 + ", " + id3 + ", " + similarId);
        
        // 测试 findRelevant 方法
        Embedding queryEmbedding = Embedding.from(vector1);
        
        // 搜索最相关的向量，返回前3个结果
        List<dev.langchain4j.store.embedding.EmbeddingMatch<dev.langchain4j.data.segment.TextSegment>> results = 
            embeddingStore.findRelevant(queryEmbedding, 3, 0.0);
        
        // 验证搜索结果
        assertNotNull(results, "搜索结果不应该为null");
        assertFalse(results.isEmpty(), "搜索结果不应该为空");
        assertTrue(results.size() <= 3, "结果数量不应该超过3个");
        
        System.out.println("✅ 找到 " + results.size() + " 个相关向量");
        
        // 验证结果按相关性排序（得分从高到低）
        for (int i = 0; i < results.size() - 1; i++) {
            double currentScore = results.get(i).score();
            double nextScore = results.get(i + 1).score();
            assertTrue(currentScore >= nextScore, 
                "结果应该按相关性得分降序排列，第" + i + "个得分: " + currentScore + 
                " 应该 >= 第" + (i + 1) + "个得分: " + nextScore);
        }
        
        // 验证第一个结果应该是与查询向量最相似的
        dev.langchain4j.store.embedding.EmbeddingMatch<dev.langchain4j.data.segment.TextSegment> topResult = results.get(0);
        assertNotNull(topResult, "第一个结果不应该为null");
        assertTrue(topResult.score() > 0.5, "最相关结果的得分应该大于0.5，实际得分: " + topResult.score());
        
        System.out.println("✅ 最相关向量得分: " + topResult.score());
        System.out.println("✅ 向量相似度搜索测试通过！");
        
        // 测试带最小得分阈值的搜索
        List<dev.langchain4j.store.embedding.EmbeddingMatch<dev.langchain4j.data.segment.TextSegment>> highThresholdResults = 
            embeddingStore.findRelevant(queryEmbedding, 3, 0.8);
        
        // 验证高阈值搜索的结果
        assertNotNull(highThresholdResults, "高阈值搜索结果不应该为null");
        for (dev.langchain4j.store.embedding.EmbeddingMatch<dev.langchain4j.data.segment.TextSegment> result : highThresholdResults) {
            assertTrue(result.score() >= 0.8, 
                "高阈值搜索结果得分应该 >= 0.8，实际得分: " + result.score());
        }
        
        System.out.println("✅ 高阈值搜索找到 " + highThresholdResults.size() + " 个高相关性向量");
        System.out.println("✅ 所有高阈值搜索结果得分都 >= 0.8");
    }
    
    /**
     * 创建随机向量
     */
    private float[] createRandomVector(int dimension) {
        float[] vector = new float[dimension];
        for (int i = 0; i < dimension; i++) {
            vector[i] = (float) (Math.random() - 0.5);
        }
        return vector;
    }
}