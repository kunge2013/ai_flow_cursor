package com.aiflow.server.engine;

import com.aiflow.server.dto.FlowDtos.FlowGraph;
import com.aiflow.server.dto.FlowDtos.FlowRunResult;
import com.aiflow.server.dto.FlowDtos.GraphNode;

import java.util.*;
import java.util.stream.Collectors;

public class Engine {
    private final List<NodeExecutor> executors;

    public Engine(List<NodeExecutor> executors) {
        this.executors = executors;
    }

    public FlowRunResult run(String flowId, FlowGraph graph, Map<String, Object> inputs) {
        Map<String, Object> variables = new HashMap<>();
        if (inputs != null) variables.putAll(inputs);
        NodeExecutor.ExecutionContext ctx = new NodeExecutor.ExecutionContext(variables);

        List<Map<String, Object>> trace = new ArrayList<>();

        List<GraphNode> nodes = graph != null && graph.nodes != null ? graph.nodes : List.of();
        Map<String, GraphNode> id2node = nodes.stream().collect(Collectors.toMap(n -> n.id, n -> n, (a,b)->a));

        // naive order: try to start at node with properties.role == start, then others by appearance
        List<GraphNode> ordered = new ArrayList<>();
        for (GraphNode n : nodes) {
            if (n.properties != null && Objects.equals(String.valueOf(n.properties.get("role")), "start")) {
                ordered.add(n);
            }
        }
        for (GraphNode n : nodes) {
            if (!ordered.contains(n)) ordered.add(n);
        }

        for (GraphNode n : ordered) {
            NodeExecutor exec = findExecutor(n.type);
            if (exec == null) continue; // skip unsupported types
            NodeExecutor.StepResult step = exec.execute(n, ctx);
            if (step != null && step.trace != null) {
                Map<String, Object> stepTrace = new LinkedHashMap<>();
                stepTrace.put("nodeId", step.nodeId);
                stepTrace.put("type", step.type);
                stepTrace.putAll(step.trace);
                trace.add(stepTrace);
            }
        }

        FlowRunResult r = new FlowRunResult();
        r.flowId = flowId;
        r.runId = "r_" + UUID.randomUUID();
        r.outputs = new LinkedHashMap<>(variables);
        r.trace = trace;
        return r;
    }

    private NodeExecutor findExecutor(String type) {
        for (NodeExecutor e : executors) {
            if (e.supports(type)) return e;
        }
        return null;
    }
} 