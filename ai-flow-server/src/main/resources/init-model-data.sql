-- 模型数据初始化脚本
USE ai_flow;

-- 清空现有模型数据（如果需要重新初始化）
-- DELETE FROM t_model WHERE deleted = 0;

-- 插入示例模型数据
INSERT INTO t_model (
    model_name, 
    base_model, 
    model_type, 
    status, 
    api_endpoint, 
    description, 
    max_tokens, 
    temperature, 
    top_p, 
    frequency_penalty, 
    presence_penalty
) VALUES 
(
    'GPT-4文本生成模型', 
    'gpt-4', 
    'text', 
    'active', 
    'https://api.openai.com/v1/chat/completions', 
    'OpenAI GPT-4文本生成模型，支持多种自然语言处理任务，包括文本生成、对话、代码生成等', 
    4096, 
    0.70, 
    1.00, 
    0.00, 
    0.00
),
(
    'Claude图像分析模型', 
    'claude', 
    'multimodal', 
    'active', 
    'https://api.anthropic.com/v1/messages', 
    'Anthropic Claude多模态模型，支持图像和文本分析，具备强大的视觉理解能力', 
    4096, 
    0.70, 
    1.00, 
    0.00, 
    0.00
),
(
    'Gemini语音识别模型', 
    'gemini', 
    'speech', 
    'inactive', 
    'https://generativelanguage.googleapis.com/v1beta/models', 
    'Google Gemini语音识别和生成模型，支持多语言语音转文本和文本转语音', 
    4096, 
    0.70, 
    1.00, 
    0.00, 
    0.00
),
(
    '文心一言对话模型', 
    'wenxin', 
    'text', 
    'active', 
    'https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions', 
    '百度文心一言大语言模型，支持中文对话、文本生成、知识问答等任务', 
    2048, 
    0.80, 
    0.95, 
    0.00, 
    0.00
),
(
    '通义千问模型', 
    'qwen', 
    'text', 
    'active', 
    'https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation', 
    '阿里云通义千问大语言模型，具备强大的中文理解和生成能力', 
    2048, 
    0.75, 
    0.90, 
    0.00, 
    0.00
)
ON DUPLICATE KEY UPDATE 
    model_name = VALUES(model_name),
    description = VALUES(description),
    max_tokens = VALUES(max_tokens),
    temperature = VALUES(temperature),
    top_p = VALUES(top_p),
    updated_at = CURRENT_TIMESTAMP;

-- 查询验证
SELECT 
    id,
    model_name,
    base_model,
    model_type,
    status,
    api_endpoint,
    created_at
FROM t_model 
WHERE deleted = 0 
ORDER BY created_at DESC; 