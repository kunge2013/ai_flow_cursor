package com.aiflow.server.service;

import com.aiflow.server.dto.ModelDtos.ModelConfig;
import com.aiflow.server.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ModelService {

    private final Map<String, ModelConfig> store = new ConcurrentHashMap<>();

    public List<ModelConfig> list(Boolean enabled) {
        return store.values().stream()
                .filter(m -> enabled == null || enabled.equals(m.enabled))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ModelConfig create(ModelConfig req) {
        ModelConfig m = new ModelConfig();
        m.id = IdService.newId();
        m.name = req.name;
        m.provider = req.provider;
        m.model = req.model;
        m.enabled = req.enabled != null ? req.enabled : Boolean.TRUE;
        store.put(m.id, m);
        return m;
    }

    public ModelConfig get(String id) {
        ModelConfig m = store.get(id);
        if (m == null) throw new NotFoundException("Model not found: " + id);
        return m;
    }

    public ModelConfig update(String id, ModelConfig req) {
        ModelConfig m = get(id);
        m.name = req.name;
        m.provider = req.provider;
        m.model = req.model;
        m.enabled = req.enabled;
        return m;
    }

    public void delete(String id) {
        if (store.remove(id) == null) {
            throw new NotFoundException("Model not found: " + id);
        }
    }
} 