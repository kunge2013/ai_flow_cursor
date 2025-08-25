package com.aiflow.server.engine;

import com.aiflow.server.dto.FlowDtos.GraphNode;

import java.util.Map;

public interface NodeExecutor {
    boolean supports(String type);
    StepResult execute(GraphNode node, ExecutionContext context);

    class ExecutionContext {
        public final Map<String, Object> variables;
        public ExecutionContext(Map<String, Object> variables) { this.variables = variables; }
        public Object getVar(String key) { return variables.get(key); }
        public void setVar(String key, Object value) { variables.put(key, value); }
    }

    class StepResult {
        public String nodeId;
        public String type;
        public Map<String, Object> outputs; // newly produced variables
        public Map<String, Object> trace;   // trace info for this step
    }
} 