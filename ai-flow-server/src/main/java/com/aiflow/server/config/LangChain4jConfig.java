package com.aiflow.server.config;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * LangChain4j配置类
 * 配置嵌入模型等基本组件
 */
@Slf4j
@Configuration
public class LangChain4jConfig {

    @Value("${ai.model.openai.api-key:}")
    private String openaiApiKey;
    
    @Value("${ai.model.openai.embedding-model:text-embedding-ada-002}")
    private String openaiEmbeddingModel;
    
    @Value("${milvus.host:localhost}")
    private String milvusHost;
    
    @Value("${milvus.port:19530}")
    private int milvusPort;
    
    @Value("${milvus.collection:ai_flow_documents}")
    private String milvusCollection;
    
    @Value("${langchain4j.document.chunk-size:1000}")
    private int chunkSize;
    
    @Value("${langchain4j.document.chunk-overlap:200}")
    private int chunkOverlap;

    /**
     * 配置OpenAI嵌入模型
     */
    @Bean
    @Profile("!test")
    public EmbeddingModel openaiEmbeddingModel() {
        if (openaiApiKey == null || openaiApiKey.isEmpty()) {
            log.warn("OpenAI API Key未配置，将使用模拟嵌入模型");
            return null;
        }
        
        log.info("配置OpenAI嵌入模型: {}", openaiEmbeddingModel);
        return OpenAiEmbeddingModel.builder()
                .apiKey(openaiApiKey)
                .modelName(openaiEmbeddingModel)
                .build();
    }
}
