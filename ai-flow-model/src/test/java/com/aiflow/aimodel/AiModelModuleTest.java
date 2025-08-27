// package com.aiflow.aimodel;

// import com.aiflow.aimodel.adapter.AiModelAdapter;
// import com.aiflow.aimodel.factory.AiModelFactory;
// import com.aiflow.aimodel.model.AiModelConfig;
// import com.aiflow.aimodel.model.AiModelType;
// import com.aiflow.aimodel.service.AiModelService;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.test.context.ActiveProfiles;

// import static org.junit.jupiter.api.Assertions.*;

// /**
//  * AI模型模块测试类
//  */
// @SpringBootTest
// @ActiveProfiles("test")
// class AiModelModuleTest {
    
//     @Autowired
//     private AiModelFactory modelFactory;
    
//     @Autowired
//     private AiModelService modelService;
    
//     @Test
//     void testModelFactory() {
//         assertNotNull(modelFactory);
//         assertTrue(modelFactory.getAdapterCount() > 0);
        
//         // 测试支持的模型类型
//         var supportedTypes = modelFactory.getAvailableModelTypes();
//         assertNotNull(supportedTypes);
//         assertFalse(supportedTypes.isEmpty());
        
//         // 测试获取适配器
//         assertTrue(modelFactory.isModelTypeSupported(AiModelType.OPENAI));
//         assertNotNull(modelFactory.getAdapter(AiModelType.OPENAI));
//     }
    
//     @Test
//     void testModelService() {
//         assertNotNull(modelService);
        
//         // 测试获取支持的模型类型
//         var supportedTypes = modelService.getAvailableModelTypes();
//         assertNotNull(supportedTypes);
//         assertFalse(supportedTypes.isEmpty());
        
//         // 测试模型类型支持检查
//         assertTrue(modelService.isModelTypeSupported(AiModelType.OPENAI));
//         assertTrue(modelService.isModelTypeSupported("openai"));
//     }
    
//     @Test
//     void testModelConfigValidation() {
//         // 测试有效的OpenAI配置
//         AiModelConfig validConfig = AiModelConfig.builder()
//                 .name("Test OpenAI")
//                 .type(AiModelType.OPENAI)
//                 .baseModel("gpt-3.5-turbo")
//                 .apiKey("test-api-key")
//                 .maxTokens(1024)
//                 .temperature(0.8)
//                 .build();
        
//         assertTrue(validConfig.isValid());
//         assertEquals("Test OpenAI (OpenAI)", validConfig.getDisplayName());
        
//         // 测试无效配置
//         AiModelConfig invalidConfig = AiModelConfig.builder()
//                 .name("Invalid Config")
//                 .type(AiModelType.OPENAI)
//                 .baseModel("")
//                 .build();
        
//         assertFalse(invalidConfig.isValid());
//     }
    
//     @Test
//     void testModelTypeEnum() {
//         // 测试从代码获取类型
//         assertEquals(AiModelType.OPENAI, AiModelType.fromCode("openai"));
//         assertEquals(AiModelType.ANTHROPIC, AiModelType.fromCode("anthropic"));
//         assertEquals(AiModelType.OLLAMA, AiModelType.fromCode("ollama"));
        
//         // 测试从名称获取类型
//         assertEquals(AiModelType.OPENAI, AiModelType.fromName("OpenAI"));
//         assertEquals(AiModelType.ANTHROPIC, AiModelType.fromName("Anthropic Claude"));
        
//         // 测试不存在的类型
//         assertEquals(AiModelType.CUSTOM, AiModelType.fromCode("non-existent"));
//         assertEquals(AiModelType.CUSTOM, AiModelType.fromName("Non Existent"));
//     }
    
//     @Test
//     void testModelConfigConstraints() {
//         // 测试约束验证
//         AiModelConfig config = AiModelConfig.builder()
//                 .name("Test Config")
//                 .type(AiModelType.OPENAI)
//                 .baseModel("test-model")
//                 .apiKey("test-key")
//                 .maxTokens(100000) // 超过最大值
//                 .temperature(3.0)   // 超过最大值
//                 .topP(2.0)         // 超过最大值
//                 .build();
        
//         // 注意：这里只是测试配置对象，实际的验证应该在服务层进行
//         assertNotNull(config);
//     }
// } 