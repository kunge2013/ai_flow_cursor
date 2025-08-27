package com.aiflow.aimodel;

import com.aiflow.aimodel.adapter.impl.ZhipuAiAdapter;
import com.aiflow.aimodel.model.AiModelConfig;
import com.aiflow.aimodel.model.AiModelType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 智普AI适配器测试类
 */
@DisplayName("智普AI适配器测试")
class ZhipuAiAdapterTest {
    
    private ZhipuAiAdapter adapter;
    private AiModelConfig validConfig;
    private AiModelConfig invalidConfig;
    
    @BeforeEach
    void setUp() {
        adapter = new ZhipuAiAdapter();
        
        // 创建有效配置
        validConfig = new AiModelConfig();
        validConfig.setType(AiModelType.ZHIPU_AI);
        validConfig.setApiKey("test-api-key");
        validConfig.setBaseModel("glm-4");
        validConfig.setMaxTokens(2048);
        validConfig.setTemperature(0.7);
        validConfig.setTopP(0.9);
        validConfig.setTimeout(30000);
        validConfig.setMaxRetries(3);
        
        // 创建无效配置
        invalidConfig = new AiModelConfig();
        invalidConfig.setType(AiModelType.OPENAI); // 错误的类型
        invalidConfig.setApiKey(""); // 空的API密钥
        invalidConfig.setBaseModel(null); // 空的模型名称
    }
    
    @Test
    @DisplayName("测试支持的模型类型")
    void testGetSupportedType() {
        assertEquals(AiModelType.ZHIPU_AI, adapter.getSupportedType());
    }
    
    @Test
    @DisplayName("测试有效配置验证")
    void testValidConfig() {
        assertTrue(adapter.isValidConfig(validConfig));
    }
    
    @Test
    @DisplayName("测试无效配置验证")
    void testInvalidConfig() {
        assertFalse(adapter.isValidConfig(invalidConfig));
        assertFalse(adapter.isValidConfig(null));
    }
    
    @Test
    @DisplayName("测试空配置验证")
    void testNullConfig() {
        assertFalse(adapter.isValidConfig(null));
    }
    
    @Test
    @DisplayName("测试空API密钥验证")
    void testEmptyApiKey() {
        AiModelConfig config = new AiModelConfig();
        config.setType(AiModelType.ZHIPU_AI);
        config.setApiKey("");
        config.setBaseModel("glm-4");
        
        assertFalse(adapter.isValidConfig(config));
    }
    
    @Test
    @DisplayName("测试空模型名称验证")
    void testEmptyBaseModel() {
        AiModelConfig config = new AiModelConfig();
        config.setType(AiModelType.ZHIPU_AI);
        config.setApiKey("test-key");
        config.setBaseModel("");
        
        assertFalse(adapter.isValidConfig(config));
    }
    
    @Test
    @DisplayName("测试模型信息获取")
    void testGetModelInfo() {
        var modelInfo = adapter.getModelInfo(validConfig);
        
        assertNotNull(modelInfo);
        assertEquals("glm-4", modelInfo.modelId());
        assertEquals("智普AI", modelInfo.provider());
        assertArrayEquals(new String[]{"text-generation", "chat", "streaming"}, modelInfo.supportedFeatures());
    }
    
    @Test
    @DisplayName("测试模型状态获取")
    void testGetModelStatus() {
        // 测试无效配置的状态
        assertEquals(AiModelAdapter.ModelStatus.ERROR, adapter.getModelStatus(invalidConfig));
        
        // 注意：实际API调用测试需要有效的API密钥，这里只测试状态逻辑
        // 在实际环境中，应该使用真实的API密钥进行测试
    }
    
    @Test
    @DisplayName("测试配置验证边界情况")
    void testConfigValidationEdgeCases() {
        // 测试空格API密钥
        AiModelConfig config1 = new AiModelConfig();
        config1.setType(AiModelType.ZHIPU_AI);
        config1.setApiKey("   ");
        config1.setBaseModel("glm-4");
        assertFalse(adapter.isValidConfig(config1));
        
        // 测试空格模型名称
        AiModelConfig config2 = new AiModelConfig();
        config2.setType(AiModelType.ZHIPU_AI);
        config2.setApiKey("test-key");
        config2.setBaseModel("   ");
        assertFalse(adapter.isValidConfig(config2));
    }
    
    @Test
    @DisplayName("测试异步方法返回")
    void testAsyncMethods() {
        // 测试异步方法是否正确返回CompletableFuture
        assertNotNull(adapter.generateTextAsync(validConfig, "test"));
        assertNotNull(adapter.generateTextAsync(validConfig, "test", 100, 0.5, 0.8));
    }
    
    @Test
    @DisplayName("测试流式方法返回")
    void testStreamMethods() {
        // 测试流式方法是否正确返回Stream
        assertNotNull(adapter.generateTextStream(validConfig, "test"));
        assertNotNull(adapter.generateTextStream(validConfig, "test", 100, 0.5, 0.8));
    }
}
