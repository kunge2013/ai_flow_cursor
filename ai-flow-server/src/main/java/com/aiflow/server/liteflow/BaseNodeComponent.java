package com.aiflow.server.liteflow;

import com.yomahub.liteflow.core.NodeComponent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseNodeComponent extends NodeComponent {
    
    protected void logNodeExecution(String nodeId, String nodeType, Object input, Object output) {
        log.info("Executing node: {} [type: {}], input: {}, output: {}", 
                nodeId, nodeType, input, output);
    }
    
    protected void logNodeError(String nodeId, String nodeType, Exception e) {
        log.error("Error executing node: {} [type: {}]", nodeId, nodeType, e);
    }
} 