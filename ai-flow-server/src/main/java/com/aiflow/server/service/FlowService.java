package com.aiflow.server.service;

import com.aiflow.server.dto.FlowDtos.FlowGraph;
import com.aiflow.server.dto.FlowDtos.FlowRunRequest;
import com.aiflow.server.dto.FlowDtos.FlowRunResult;
import com.aiflow.server.dto.FlowDtos.FlowSummary;
import com.aiflow.server.dto.FlowDtos.FlowUpsertRequest;
import com.aiflow.server.dto.FlowDtos.FlowWithGraphResponse;
import com.aiflow.server.exception.NotFoundException;
import com.aiflow.server.engine.Engine;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class FlowService {

    private final Map<String, FlowWithGraphResponse> store = new ConcurrentHashMap<>();
    private Engine engine; // lazy set via setter for simplicity

    public void setEngine(Engine engine) { this.engine = engine; }

    public List<FlowSummary> list(String nameLike) {
        return store.values().stream()
                .filter(f -> nameLike == null || nameLike.isBlank() || f.name.toLowerCase().contains(nameLike.toLowerCase()))
                .sorted(Comparator.comparing((FlowWithGraphResponse f) -> f.createdAt).reversed())
                .map(this::toSummary)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public FlowSummary create(FlowUpsertRequest req) {
        FlowWithGraphResponse f = new FlowWithGraphResponse();
        f.id = IdService.newId();
        f.name = req.name;
        f.description = req.description;
        f.createdAt = Instant.now();
        f.updatedAt = f.createdAt;
        f.graph = emptyGraph();
        store.put(f.id, f);
        return toSummary(f);
    }

    public FlowWithGraphResponse get(String id) {
        FlowWithGraphResponse f = store.get(id);
        if (f == null) throw new NotFoundException("Flow not found: " + id);
        return f;
    }

    public FlowSummary update(String id, FlowUpsertRequest req) {
        FlowWithGraphResponse f = get(id);
        f.name = req.name;
        f.description = req.description;
        f.updatedAt = Instant.now();
        return toSummary(f);
    }

    public void delete(String id) {
        if (store.remove(id) == null) {
            throw new NotFoundException("Flow not found: " + id);
        }
    }

    public FlowGraph getGraph(String id) {
        return Objects.requireNonNull(get(id).graph);
    }

    public FlowGraph saveGraph(String id, FlowGraph graph) {
        FlowWithGraphResponse f = get(id);
        f.graph = graph != null ? graph : emptyGraph();
        f.updatedAt = Instant.now();
        return f.graph;
    }

    public FlowRunResult run(String id, FlowRunRequest req) {
        FlowWithGraphResponse f = get(id);
        if (engine == null) {
            FlowRunResult r = new FlowRunResult();
            r.flowId = f.id;
            r.runId = "r_" + UUID.randomUUID();
            r.outputs = Map.of();
            r.trace = List.of();
            return r;
        }
        Map<String, Object> inputs = req != null ? req.inputs : Map.of();
        return engine.run(f.id, f.graph, inputs);
    }

    private FlowSummary toSummary(FlowWithGraphResponse f) {
        FlowSummary s = new FlowSummary();
        s.id = f.id;
        s.name = f.name;
        s.description = f.description;
        s.createdAt = f.createdAt;
        s.updatedAt = f.updatedAt;
        return s;
    }

    private FlowGraph emptyGraph() {
        FlowGraph g = new FlowGraph();
        g.nodes = new ArrayList<>();
        g.edges = new ArrayList<>();
        return g;
    }
} 