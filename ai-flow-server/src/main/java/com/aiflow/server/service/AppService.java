package com.aiflow.server.service;

import com.aiflow.server.dto.AppDtos.AiAppSummary;
import com.aiflow.server.dto.AppDtos.AiAppUpsertRequest;
import com.aiflow.server.exception.NotFoundException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class AppService {

    private final Map<String, AiAppSummary> store = new ConcurrentHashMap<>();

    public List<AiAppSummary> list(String nameLike, String type) {
        return store.values().stream()
                .filter(a -> nameLike == null || nameLike.isBlank() || a.name.toLowerCase().contains(nameLike.toLowerCase()))
                .filter(a -> type == null || type.isBlank() || type.equalsIgnoreCase(a.appType))
                .sorted(Comparator.comparing((AiAppSummary a) -> a.createdAt).reversed())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public AiAppSummary create(AiAppUpsertRequest req) {
        AiAppSummary a = new AiAppSummary();
        a.id = IdService.newId();
        a.name = req.name;
        a.description = req.description;
        a.iconUrl = req.iconUrl;
        a.appType = req.appType;
        a.model = req.model;
        a.prompt = req.prompt;
        a.openingRemark = req.openingRemark;
        a.createdAt = Instant.now();
        a.updatedAt = a.createdAt;
        store.put(a.id, a);
        return a;
    }

    public AiAppSummary get(String id) {
        AiAppSummary a = store.get(id);
        if (a == null) throw new NotFoundException("App not found: " + id);
        return a;
    }

    public AiAppSummary update(String id, AiAppUpsertRequest req) {
        AiAppSummary a = get(id);
        a.name = req.name;
        a.description = req.description;
        a.iconUrl = req.iconUrl;
        a.appType = req.appType;
        a.model = req.model;
        a.prompt = req.prompt;
        a.openingRemark = req.openingRemark;
        a.updatedAt = Instant.now();
        return a;
    }

    public void delete(String id) {
        if (store.remove(id) == null) {
            throw new NotFoundException("App not found: " + id);
        }
    }
} 