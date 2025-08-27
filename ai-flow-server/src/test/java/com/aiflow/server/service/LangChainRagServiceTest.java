package com.aiflow.server.service;

import com.aiflow.server.dto.VectorSearchDtos.VectorSearchRequest;
import com.aiflow.server.dto.VectorSearchDtos.VectorSearchResponse;
import com.aiflow.server.entity.KnowledgeBase;
import com.aiflow.server.entity.VectorDocument;
import com.aiflow.server.mapper.KnowledgeBaseMapper;
import com.aiflow.server.mapper.VectorDocumentMapper;
import com.aiflow.server.service.LangChainRagService.DocumentProcessResult;
import com.aiflow.server.service.LangChainRagService.RagStatistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * LangChainRagService 测试类
 */
@ExtendWith(MockitoExtension.class)
class LangChainRagServiceTest {

    @Mock
    private KnowledgeBaseMapper knowledgeBaseMapper;

    @Mock
    private VectorDocumentMapper vectorDocumentMapper;

    @InjectMocks
    private com.aiflow.server.service.impl.LangChainRagServiceImpl langChainRagService;

    private KnowledgeBase testKb;
    private VectorDocument testDoc;

    @BeforeEach
    void setUp() {
        // 设置测试知识库
        testKb = new KnowledgeBase();
        testKb.setId("test-kb-001");
        testKb.setName("测试知识库");
        testKb.setDescription("用于测试的知识库");
        testKb.setVectorModel("openai");
        testKb.setStatus(true);
        testKb.setCreatedAt(LocalDateTime.now());

        // 设置测试文档
        testDoc = new VectorDocument();
        testDoc.setId("test-doc-001");
        testDoc.setKbId("test-kb-001");
        testDoc.setTitle("测试文档");
        testDoc.setContent("这是一个测试文档的内容，用于验证RAG功能。");
        testDoc.setContentType("text");
        testDoc.setFileSize(100L); // 修复类型错误，使用Long类型
        testDoc.setVectorModel("openai");
        testDoc.setStatus("completed");
        testDoc.setCreatedAt(LocalDateTime.now());
        testDoc.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testProcessDocument() {
        // 准备测试数据
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "这是一个测试文件的内容，用于验证文档处理功能。".getBytes()
        );

        when(knowledgeBaseMapper.selectById("test-kb-001")).thenReturn(testKb);
        when(vectorDocumentMapper.insert(any(VectorDocument.class))).thenReturn(1);
        when(vectorDocumentMapper.selectById(anyString())).thenReturn(testDoc);

        // 执行测试
        DocumentProcessResult result = langChainRagService.processDocument("test-kb-001", file);

        // 验证结果
        assertNotNull(result);
        assertEquals("success", result.getStatus());
        assertTrue(result.getProcessingTime() > 0);
        assertTrue(result.getTotalChunks() > 0);

        // 验证调用
        verify(knowledgeBaseMapper).selectById("test-kb-001");
        verify(vectorDocumentMapper).insert(any(VectorDocument.class));
    }

    @Test
    void testProcessDocumentContent() {
        // 准备测试数据
        String title = "测试文档";
        String content = "这是一个测试文档的内容，用于验证RAG功能。内容包含多个段落，用于测试文本分割功能。";
        String contentType = "text";

        when(knowledgeBaseMapper.selectById("test-kb-001")).thenReturn(testKb);
        when(vectorDocumentMapper.insert(any(VectorDocument.class))).thenReturn(1);
        when(vectorDocumentMapper.selectById(anyString())).thenReturn(testDoc);

        // 执行测试
        DocumentProcessResult result = langChainRagService.processDocumentContent(
                "test-kb-001", title, content, contentType);

        // 验证结果
        assertNotNull(result);
        assertEquals("success", result.getStatus());
        assertNotNull(result.getDocumentId());
        assertTrue(result.getTotalChunks() > 0);
        assertNotNull(result.getChunks());
        assertFalse(result.getChunks().isEmpty());

        // 验证调用
        verify(knowledgeBaseMapper).selectById("test-kb-001");
        verify(vectorDocumentMapper).insert(any(VectorDocument.class));
    }

    @Test
    void testSearchSimilar() {
        // 准备测试数据
        VectorSearchRequest request = new VectorSearchRequest();
        request.setQuery("测试查询");
        request.setKbId("test-kb-001");
        request.setTopK(5);
        request.setScoreThreshold(0.7);

        when(knowledgeBaseMapper.selectById("test-kb-001")).thenReturn(testKb);

        // 执行测试
        VectorSearchResponse response = langChainRagService.searchSimilar(request);

        // 验证结果
        assertNotNull(response);
        assertEquals("测试查询", response.getQuery());
        assertNotNull(response.getResults());
        assertTrue(response.getSearchTime() > 0);

        // 验证调用
        verify(knowledgeBaseMapper).selectById("test-kb-001");
    }

    @Test
    void testGetRagStatistics() {
        // 准备测试数据
        when(knowledgeBaseMapper.selectById("test-kb-001")).thenReturn(testKb);
        // 使用模拟的计数方法，避免调用不存在的方法
        when(vectorDocumentMapper.selectList(any())).thenReturn(Arrays.asList(testDoc));

        // 执行测试
        RagStatistics stats = langChainRagService.getRagStatistics("test-kb-001");

        // 验证结果
        assertNotNull(stats);
        assertEquals("test-kb-001", stats.getKbId());
        assertEquals("openai", stats.getVectorModel());
        assertTrue(stats.getTotalDocuments() >= 0);
        assertTrue(stats.getTotalEmbeddings() >= 0);
        assertTrue(stats.getTotalChunks() >= 0);
        assertTrue(stats.getLastUpdated() > 0);

        // 验证调用
        verify(knowledgeBaseMapper).selectById("test-kb-001");
    }

    @Test
    void testProcessDocumentWithInvalidKbId() {
        // 准备测试数据
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "测试内容".getBytes()
        );

        when(knowledgeBaseMapper.selectById("invalid-kb-id")).thenReturn(null);

        // 执行测试并验证异常
        assertThrows(RuntimeException.class, () -> {
            langChainRagService.processDocument("invalid-kb-id", file);
        });

        // 验证调用
        verify(knowledgeBaseMapper).selectById("invalid-kb-id");
        verify(vectorDocumentMapper, never()).insert(any(VectorDocument.class));
    }

    @Test
    void testProcessDocumentContentWithEmptyContent() {
        // 准备测试数据
        String title = "空内容文档";
        String content = "";
        String contentType = "text";

        when(knowledgeBaseMapper.selectById("test-kb-001")).thenReturn(testKb);
        when(vectorDocumentMapper.insert(any(VectorDocument.class))).thenReturn(1);
        when(vectorDocumentMapper.selectById(anyString())).thenReturn(testDoc);

        // 执行测试
        DocumentProcessResult result = langChainRagService.processDocumentContent(
                "test-kb-001", title, content, contentType);

        // 验证结果
        assertNotNull(result);
        assertEquals("success", result.getStatus());
        assertEquals(0, result.getTotalChunks());
        assertTrue(result.getChunks().isEmpty());
    }

    @Test
    void testProcessDocumentContentWithLongContent() {
        // 准备测试数据 - 长文本用于测试分割
        StringBuilder longContent = new StringBuilder();
        for (int i = 0; i < 50; i++) {
            longContent.append("这是第").append(i + 1).append("段内容，用于测试文本分割功能。");
            longContent.append("每段内容都包含足够的信息，确保分割后的chunk有意义。");
            longContent.append("\n\n");
        }

        when(knowledgeBaseMapper.selectById("test-kb-001")).thenReturn(testKb);
        when(vectorDocumentMapper.insert(any(VectorDocument.class))).thenReturn(1);
        when(vectorDocumentMapper.selectById(anyString())).thenReturn(testDoc);

        // 执行测试
        DocumentProcessResult result = langChainRagService.processDocumentContent(
                "test-kb-001", "长文档", longContent.toString(), "text");

        // 验证结果
        assertNotNull(result);
        assertEquals("success", result.getStatus());
        assertTrue(result.getTotalChunks() > 1); // 长文本应该被分割成多个chunk
        assertNotNull(result.getChunks());
        assertFalse(result.getChunks().isEmpty());
    }
}
