package com.aiflow.aimodel.config;

import com.aiflow.aimodel.service.VectorService;
import com.aiflow.aimodel.service.RagService;
import com.aiflow.aimodel.service.impl.VectorServiceImpl;
import com.aiflow.aimodel.service.impl.RagServiceImpl;
import com.aiflow.aimodel.factory.EmbeddingModelFactory;
import com.aiflow.aimodel.factory.VectorStoreFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 向量服务自动配置类
 */
@Configuration
public class VectorServiceAutoConfiguration {

    /**
     * 配置向量服务
     */
    @Bean
    @ConditionalOnMissingBean
    public VectorService vectorService(EmbeddingModelFactory embeddingModelFactory,
                                     VectorStoreFactory vectorStoreFactory) {
        return new VectorServiceImpl(embeddingModelFactory, vectorStoreFactory);
    }

    /**
     * 配置RAG服务
     */
    @Bean
    @ConditionalOnMissingBean
    public RagService ragService(EmbeddingModelFactory embeddingModelFactory,
                                VectorStoreFactory vectorStoreFactory,
                                VectorService vectorService) {
        return new RagServiceImpl(embeddingModelFactory, vectorStoreFactory, vectorService);
    }
}
