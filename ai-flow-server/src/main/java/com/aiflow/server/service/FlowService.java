package com.aiflow.server.service;

import com.aiflow.server.dto.FlowDtos.FlowGraph;
import com.aiflow.server.dto.FlowDtos.FlowRunRequest;
import com.aiflow.server.dto.FlowDtos.FlowRunResult;
import com.aiflow.server.dto.FlowDtos.FlowSummary;
import com.aiflow.server.dto.FlowDtos.FlowUpsertRequest;
import com.aiflow.server.dto.FlowDtos.FlowWithGraphResponse;
import com.aiflow.server.entity.FlowEntity;
import com.aiflow.server.exception.NotFoundException;
import com.aiflow.server.engine.Engine;
import com.aiflow.server.mapper.FlowMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlowService {

    private final FlowMapper flowMapper;
    private final ObjectMapper objectMapper;
    private Engine engine; // lazy set via setter for simplicity

    public void setEngine(Engine engine) { this.engine = engine; }

    public List<FlowSummary> list(String nameLike) {
        QueryWrapper<FlowEntity> wrapper = new QueryWrapper<>();
        if (nameLike != null && !nameLike.isBlank()) {
            wrapper.like("name", nameLike);
        }
        wrapper.orderByDesc("created_at");
        
        List<FlowEntity> entities = flowMapper.selectList(wrapper);
        return entities.stream()
                .map(this::toSummary)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public FlowSummary create(FlowUpsertRequest req) {
        FlowEntity entity = new FlowEntity();
        entity.setFlowId(IdService.newId());
        entity.setName(req.name);
        entity.setDescription(req.description);
        entity.setGraphJson(serializeGraph(emptyGraph()));
        
        flowMapper.insert(entity);
        return toSummary(entity);
    }

    public FlowWithGraphResponse get(String id) {
        FlowEntity entity = getEntityByFlowId(id);
        return toFlowWithGraph(entity);
    }

    public FlowSummary update(String id, FlowUpsertRequest req) {
        FlowEntity entity = getEntityByFlowId(id);
        entity.setName(req.name);
        entity.setDescription(req.description);
        
        flowMapper.updateById(entity);
        return toSummary(entity);
    }

    public void delete(String id) {
        FlowEntity entity = getEntityByFlowId(id);
        flowMapper.deleteById(entity.getId());
    }

    public FlowGraph getGraph(String id) {
        FlowEntity entity = getEntityByFlowId(id);
        return deserializeGraph(entity.getGraphJson());
    }

    public FlowGraph saveGraph(String id, FlowGraph graph) {
        FlowEntity entity = getEntityByFlowId(id);
        FlowGraph finalGraph = graph != null ? graph : emptyGraph();
        entity.setGraphJson(serializeGraph(finalGraph));
        
        flowMapper.updateById(entity);
        return finalGraph;
    }

    public FlowRunResult run(String id, FlowRunRequest req) {
        FlowEntity entity = getEntityByFlowId(id);
        FlowGraph graph = deserializeGraph(entity.getGraphJson());
        
        if (engine == null) {
            FlowRunResult r = new FlowRunResult();
            r.flowId = entity.getFlowId();
            r.runId = "r_" + UUID.randomUUID();
            r.outputs = Map.of();
            r.trace = List.of();
            return r;
        }
        Map<String, Object> inputs = req != null ? req.inputs : Map.of();
        return engine.run(entity.getFlowId(), graph, inputs);
    }
    
    private FlowEntity getEntityByFlowId(String flowId) {
        QueryWrapper<FlowEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("flow_id", flowId);
        FlowEntity entity = flowMapper.selectOne(wrapper);
        if (entity == null) {
            throw new NotFoundException("Flow not found: " + flowId);
        }
        return entity;
    }

    private FlowSummary toSummary(FlowEntity entity) {
        FlowSummary s = new FlowSummary();
        s.id = entity.getFlowId();
        s.name = entity.getName();
        s.description = entity.getDescription();
        s.createdAt = entity.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant();
        s.updatedAt = entity.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant();
        return s;
    }
    
    private FlowWithGraphResponse toFlowWithGraph(FlowEntity entity) {
        FlowWithGraphResponse response = new FlowWithGraphResponse();
        response.id = entity.getFlowId();
        response.name = entity.getName();
        response.description = entity.getDescription();
        response.createdAt = entity.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant();
        response.updatedAt = entity.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant();
        response.graph = deserializeGraph(entity.getGraphJson());
        return response;
    }

    private FlowGraph emptyGraph() {
        FlowGraph g = new FlowGraph();
        g.nodes = new ArrayList<>();
        g.edges = new ArrayList<>();
        return g;
    }
    
    private String serializeGraph(FlowGraph graph) {
        try {
            return objectMapper.writeValueAsString(graph);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize graph", e);
        }
    }
    
    private FlowGraph deserializeGraph(String graphJson) {
        try {
            if (graphJson == null || graphJson.isBlank()) {
                return emptyGraph();
            }
            return objectMapper.readValue(graphJson, FlowGraph.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize graph", e);
        }
    }
} 