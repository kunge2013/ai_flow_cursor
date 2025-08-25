package com.aiflow.server.liteflow;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@LiteflowComponent("httpRequestNode")
public class HttpRequestNodeComponent extends BaseNodeComponent {

    @Override
    public void process() throws Exception {
        String nodeId = this.getNodeId();
        log.info("Executing HTTP Request node: {}", nodeId);
        
        // TODO: 实现HTTP请求逻辑
        // 支持GET, POST等HTTP方法
        
        logNodeExecution(nodeId, "http_request", "http_request", "http_response");
    }
} 