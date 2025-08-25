package com.aiflow.server.liteflow;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@LiteflowComponent("startNode")
public class StartNodeComponent extends BaseNodeComponent {

    @Override
    public void process() throws Exception {
        String nodeId = this.getNodeId();
        log.info("Starting flow execution at node: {}", nodeId);
        
        logNodeExecution(nodeId, "start", "flow_begin", "initialized");
    }
} 