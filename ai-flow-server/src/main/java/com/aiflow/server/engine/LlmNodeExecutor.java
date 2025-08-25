package com.aiflow.server.engine;

import com.aiflow.server.dto.FlowDtos.GraphNode;

import java.util.HashMap;
import java.util.Map;

public class LlmNodeExecutor implements NodeExecutor {

    @Override
    public boolean supports(String type) {
        return "llm".equalsIgnoreCase(type);
    }

    @Override
    public StepResult execute(GraphNode node, ExecutionContext context) {
        Map<String, Object> props = node.properties != null ? node.properties : Map.of();
        String model = String.valueOf(props.getOrDefault("model", "gpt-4o-mini"));
        String input = String.valueOf(props.getOrDefault("input", ""));
        String outputVar = String.valueOf(props.getOrDefault("output", "result"));

        String resolvedPrompt = renderTemplate(input, context.variables);
        String fakeAnswer = "[" + model + "] -> " + resolvedPrompt; // simulate

        context.setVar(outputVar, fakeAnswer);

        NodeExecutor.StepResult r = new NodeExecutor.StepResult();
        r.nodeId = node.id;
        r.type = node.type;
        r.outputs = Map.of(outputVar, fakeAnswer);
        Map<String, Object> trace = new HashMap<>();
        trace.put("model", model);
        trace.put("input", resolvedPrompt);
        trace.put("outputVar", outputVar);
        r.trace = trace;
        return r;
    }

    private String renderTemplate(String tpl, Map<String, Object> vars) {
        if (tpl == null) return "";
        String s = tpl;
        for (Map.Entry<String, Object> e : vars.entrySet()) {
            String k = e.getKey();
            Object v = e.getValue();
            s = s.replace("${" + k + "}", v == null ? "" : String.valueOf(v));
        }
        return s;
    }
} 