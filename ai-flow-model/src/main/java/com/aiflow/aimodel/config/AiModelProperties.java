package com.aiflow.aimodel.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * AI模型配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "ai.model")
public class AiModelProperties {
    
    /**
     * 默认模型配置
     */
    private DefaultModel defaultModel = new DefaultModel();
    
    /**
     * OpenAI配置
     */
    private OpenAi openai = new OpenAi();
    
    /**
     * Anthropic配置
     */
    private Anthropic anthropic = new Anthropic();
    
    /**
     * Google Gemini配置
     */
    private GoogleGemini googleGemini = new GoogleGemini();
    
    /**
     * Ollama配置
     */
    private Ollama ollama = new Ollama();
    
    /**
     * 智普AI配置
     */
    private ZhipuAi zhipuAi = new ZhipuAi();
    
    /**
     * 通义千问配置
     */
    private Qianfan qianfan = new Qianfan();
    
    /**
     * 向量存储配置
     */
    private VectorStore vectorStore = new VectorStore();
    
    @Data
    public static class DefaultModel {
        private String type = "openai";
        private Integer maxTokens = 2048;
        private Double temperature = 0.7;
        private Double topP = 1.0;
        private Integer maxRetries = 3;
        private Long timeout = 60000L;
    }
    
    @Data
    public static class OpenAi {
        private String apiKey;
        private String baseUrl = "https://api.openai.com";
        private String model = "gpt-3.5-turbo";
        private String embeddingModel = "text-embedding-ada-002";
        private Integer maxTokens = 2048;
        private Double temperature = 0.7;
        private Double topP = 1.0;
        private Integer maxRetries = 3;
        private Long timeout = 60000L;
    }
    
    @Data
    public static class Anthropic {
        private String apiKey;
        private String baseUrl = "https://api.anthropic.com";
        private String model = "claude-3-sonnet-20240229";
        private Integer maxTokens = 4096;
        private Double temperature = 0.7;
        private Double topP = 1.0;
        private Integer maxRetries = 3;
        private Long timeout = 60000L;
    }
    
    @Data
    public static class GoogleGemini {
        private String apiKey;
        private String baseUrl = "https://generativelanguage.googleapis.com";
        private String model = "gemini-pro";
        private Integer maxTokens = 2048;
        private Double temperature = 0.7;
        private Double topP = 1.0;
        private Integer maxRetries = 3;
        private Long timeout = 60000L;
    }
    
    @Data
    public static class Ollama {
        private String baseUrl = "http://localhost:11434";
        private String model = "llama2";
        private Integer maxTokens = 2048;
        private Double temperature = 0.7;
        private Double topP = 1.0;
        private Integer maxRetries = 3;
        private Long timeout = 60000L;
    }
    
    @Data
    public static class ZhipuAi {
        private String apiKey;
        private String baseUrl = "https://open.bigmodel.cn";
        private String model = "glm-4";
        private String embeddingModel = "text-embedding-v2";
        private Integer maxTokens = 2048;
        private Double temperature = 0.7;
        private Double topP = 1.0;
        private Integer maxRetries = 3;
        private Long timeout = 60000L;
    }
    
    @Data
    public static class Qianfan {
        private String apiKey;
        private String secretKey;
        private String baseUrl = "https://aip.baidubce.com";
        private String model = "qwen-turbo";
        private String embeddingModel = "text-embedding-v1";
        private Integer maxTokens = 2048;
        private Double temperature = 0.7;
        private Double topP = 1.0;
        private Integer maxRetries = 3;
        private Long timeout = 60000L;
    }
    
    @Data
    public static class VectorStore {
        /**
         * 默认向量存储类型
         */
        private String defaultType = "milvus";
        
        /**
         * Milvus配置
         */
        private Milvus milvus = new Milvus();
        
        /**
         * Chroma配置
         */
        private Chroma chroma = new Chroma();
        
        /**
         * Pinecone配置
         */
        private Pinecone pinecone = new Pinecone();
        
        /**
         * Weaviate配置
         */
        private Weaviate weaviate = new Weaviate();
    }
    
    @Data
    public static class Milvus {
        private String host = "localhost";
        private Integer port = 19530;
        private String collectionName = "ai_flow_documents";
        private Integer dimension = 1536;
        private String username;
        private String password;
        private String database = "default";
    }
    
    @Data
    public static class Chroma {
        private String host = "localhost";
        private Integer port = 8000;
        private String collectionName = "ai_flow_documents";
        private String username;
        private String password;
    }
    
    @Data
    public static class Pinecone {
        private String apiKey;
        private String environment;
        private String projectName;
        private String indexName = "ai-flow-documents";
        private String namespace = "default";
    }
    
    @Data
    public static class Weaviate {
        private String host = "localhost";
        private Integer port = 8080;
        private String scheme = "http";
        private String apiKey;
        private String className = "Document";
    }
} 