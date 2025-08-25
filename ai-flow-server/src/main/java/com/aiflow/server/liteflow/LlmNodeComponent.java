package com.aiflow.server.liteflow;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@LiteflowComponent("llmNode")
public class LlmNodeComponent extends BaseNodeComponent {

    @Override
    public void process() throws Exception {
        String nodeId = this.getNodeId();
        log.info("Executing LLM node: {}", nodeId);
        
        // TODO: 实现LLM调用逻辑
        // 这里可以集成各种LLM服务，如OpenAI, 百度文心等
        
        logNodeExecution(nodeId, "llm", "llm_request", "llm_response");
    }
} 