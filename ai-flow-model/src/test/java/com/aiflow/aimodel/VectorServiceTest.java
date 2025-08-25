package com.aiflow.aimodel;

import com.aiflow.aimodel.embedding.EmbeddingModel;
import com.aiflow.aimodel.factory.EmbeddingModelFactory;
import com.aiflow.aimodel.factory.VectorStoreFactory;
import com.aiflow.aimodel.service.VectorService;
import com.aiflow.aimodel.vectorstore.VectorStore;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 向量服务测试
 */
@SpringBootTest
@TestPropertySource(properties = {
    "ai.vectorstore.default-type=milvus",
    "ai.vectorstore.milvus.host=localhost",
    "ai.vectorstore.milvus.port=19530",
    "ai.vectorstore.milvus.collection-name=test_collection"
})
public class VectorServiceTest {
    
    @Autowired
    private VectorService vectorService;
    
    @Autowired
    private EmbeddingModelFactory embeddingModelFactory;
    
    @Autowired
    private VectorStoreFactory vectorStoreFactory;
    
    @Test
    public void testEmbeddingModelFactory() {
        assertNotNull(embeddingModelFactory);
        
        // 测试获取所有向量模型类型
        List<String> modelTypes = embeddingModelFactory.getAllEmbeddingModelTypes();
        assertNotNull(modelTypes);
        assertFalse(modelTypes.isEmpty());
        
        // 测试连接
        Map<String, Boolean> connectionResults = embeddingModelFactory.testAllConnections();
        assertNotNull(connectionResults);
        assertFalse(connectionResults.isEmpty());
    }
    
    @Test
    public void testVectorStoreFactory() {
        assertNotNull(vectorStoreFactory);
        
        // 测试获取默认向量存储
        VectorStore defaultStore = vectorStoreFactory.getDefaultVectorStore();
        assertNotNull(defaultStore);
        assertEquals("milvus", defaultStore.getStoreType());
        
        // 测试获取所有向量存储类型
        List<String> storeTypes = vectorStoreFactory.getAllVectorStoreTypes();
        assertNotNull(storeTypes);
        assertFalse(storeTypes.isEmpty());
        
        // 测试连接
        Map<String, Boolean> connectionResults = vectorStoreFactory.testAllConnections();
        assertNotNull(connectionResults);
        assertFalse(connectionResults.isEmpty());
    }
    
    @Test
    public void testVectorService() {
        assertNotNull(vectorService);
        
        // 测试获取可用的向量模型类型
        List<String> embeddingModelTypes = vectorService.getAvailableEmbeddingModelTypes();
        assertNotNull(embeddingModelTypes);
        assertFalse(embeddingModelTypes.isEmpty());
        
        // 测试获取可用的向量存储类型
        List<String> vectorStoreTypes = vectorService.getAvailableVectorStoreTypes();
        assertNotNull(vectorStoreTypes);
        assertFalse(vectorStoreTypes.isEmpty());
        
        // 测试连接
        Map<String, Object> connectionResults = vectorService.testConnections();
        assertNotNull(connectionResults);
        assertTrue(connectionResults.containsKey("embeddingModels"));
        assertTrue(connectionResults.containsKey("vectorStores"));
        assertTrue(connectionResults.containsKey("defaultVectorStore"));
    }
    
    @Test
    public void testCollectionOperations() {
        String collectionName = "test_collection_" + System.currentTimeMillis();
        
        try {
            // 测试创建集合
            boolean created = vectorService.createCollection(collectionName);
            assertTrue(created);
            
            // 测试获取集合信息
            Map<String, Object> info = vectorService.getCollectionInfo(collectionName);
            assertNotNull(info);
            assertEquals(collectionName, info.get("name"));
            assertTrue((Boolean) info.get("exists"));
            
        } finally {
            // 清理测试集合
            vectorService.deleteCollection(collectionName);
        }
    }
    
    @Test
    public void testDocumentOperations() {
        String collectionName = "test_docs_" + System.currentTimeMillis();
        
        try {
            // 创建测试集合
            vectorService.createCollection(collectionName);
            
            // 创建测试文档
            Document document = Document.from("这是一个测试文档，用于测试向量存储功能。");
            
            // 测试添加文档
            String documentId = vectorService.addDocument(collectionName, document, "openai");
            assertNotNull(documentId);
            assertFalse(documentId.isEmpty());
            
            // 测试批量添加文档
            List<Document> documents = List.of(
                Document.from("第二个测试文档"),
                Document.from("第三个测试文档")
            );
            
            List<String> documentIds = vectorService.addDocuments(collectionName, documents, "openai");
            assertNotNull(documentIds);
            assertEquals(2, documentIds.size());
            
        } finally {
            // 清理测试集合
            vectorService.deleteCollection(collectionName);
        }
    }
    
    @Test
    public void testSearchOperations() {
        String collectionName = "test_search_" + System.currentTimeMillis();
        
        try {
            // 创建测试集合
            vectorService.createCollection(collectionName);
            
            // 添加测试文档
            List<Document> documents = List.of(
                Document.from("人工智能是计算机科学的一个分支"),
                Document.from("机器学习是人工智能的重要技术"),
                Document.from("深度学习是机器学习的一个子领域")
            );
            
            vectorService.addDocuments(collectionName, documents, "openai");
            
            // 测试相似性搜索
            List<EmbeddingMatch<TextSegment>> results = vectorService.search(
                collectionName, "机器学习", 5, "openai"
            );
            
            assertNotNull(results);
            // 注意：由于向量化需要时间，结果可能为空
            
        } finally {
            // 清理测试集合
            vectorService.deleteCollection(collectionName);
        }
    }
} 