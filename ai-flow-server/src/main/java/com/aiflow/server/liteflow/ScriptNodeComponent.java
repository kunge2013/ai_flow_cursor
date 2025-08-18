package com.aiflow.server.liteflow;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@LiteflowComponent("scriptNode")
public class ScriptNodeComponent extends BaseNodeComponent {

    @Override
    public void process() throws Exception {
        String nodeId = this.getNodeId();
        log.info("Executing Script node: {}", nodeId);
        
        // TODO: 实现脚本执行逻辑
        // 支持JavaScript, Python等脚本执行
        
        logNodeExecution(nodeId, "script", "script_input", "script_output");
    }
} 