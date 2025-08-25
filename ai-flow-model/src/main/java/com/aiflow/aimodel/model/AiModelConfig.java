package com.aiflow.aimodel.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

/**
 * AI模型配置
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiModelConfig {
    
    /**
     * 模型ID
     */
    private String id;
    
    /**
     * 模型名称
     */
    @NotBlank(message = "模型名称不能为空")
    private String name;
    
    /**
     * 模型类型
     */
    @NotNull(message = "模型类型不能为空")
    private AiModelType type;
    
    /**
     * 基础模型名称
     */
    @NotBlank(message = "基础模型名称不能为空")
    private String baseModel;
    
    /**
     * API密钥
     */
    private String apiKey;
    
    /**
     * 密钥（用于某些需要双密钥的模型）
     */
    private String secretKey;
    
    /**
     * API端点
     */
    private String apiEndpoint;
    
    /**
     * 最大Token数
     */
    @Min(value = 1, message = "最大Token数必须大于0")
    @Max(value = 100000, message = "最大Token数不能超过100000")
    private Integer maxTokens = 2048;
    
    /**
     * 温度参数
     */
    @Min(value = 0, message = "温度参数不能小于0")
    @Max(value = 2, message = "温度参数不能大于2")
    private Double temperature = 0.7;
    
    /**
     * Top-P参数
     */
    @Min(value = 0, message = "Top-P参数不能小于0")
    @Max(value = 1, message = "Top-P参数不能大于1")
    private Double topP = 1.0;
    
    /**
     * 最大重试次数
     */
    @Min(value = 0, message = "最大重试次数不能小于0")
    @Max(value = 10, message = "最大重试次数不能超过10")
    private Integer maxRetries = 3;
    
    /**
     * 超时时间（毫秒）
     */
    @Min(value = 1000, message = "超时时间不能小于1000毫秒")
    @Max(value = 300000, message = "超时时间不能超过300000毫秒")
    private Long timeout = 60000L;
    
    /**
     * 是否启用
     */
    private Boolean enabled = true;
    
    /**
     * 描述信息
     */
    private String description;
    
    /**
     * 标签
     */
    private String[] tags;
    
    /**
     * 自定义参数
     */
    private java.util.Map<String, Object> customParams;
    
    /**
     * 创建时间
     */
    private java.time.LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private java.time.LocalDateTime updatedAt;
    
    /**
     * 检查配置是否有效
     */
    public boolean isValid() {
        if (type == null || baseModel == null || baseModel.trim().isEmpty()) {
            return false;
        }
        
        // 根据模型类型检查必要的配置
        switch (type) {
            case OPENAI:
            case ANTHROPIC:
            case GOOGLE_GEMINI:
            case ZHIPU_AI:
                return apiKey != null && !apiKey.trim().isEmpty();
            case QIANFAN:
                return apiKey != null && !apiKey.trim().isEmpty() 
                    && secretKey != null && !secretKey.trim().isEmpty();
            case OLLAMA:
                return true; // Ollama不需要API密钥
            case HUGGING_FACE:
                return apiKey != null && !apiKey.trim().isEmpty();
            case CUSTOM:
                return true; // 自定义模型由用户决定
            default:
                return false;
        }
    }
    
    /**
     * 获取显示名称
     */
    public String getDisplayName() {
        return String.format("%s (%s)", name, type.getName());
    }
} 