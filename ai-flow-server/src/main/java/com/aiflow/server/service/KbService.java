package com.aiflow.server.service;

import com.aiflow.server.dto.KbDtos.KnowledgeBaseSummary;
import com.aiflow.server.dto.KbDtos.KnowledgeBaseUpsertRequest;
import com.aiflow.server.exception.NotFoundException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class KbService {

    private final Map<String, KnowledgeBaseSummary> store = new ConcurrentHashMap<>();

    public List<KnowledgeBaseSummary> list(String nameLike) {
        return store.values().stream()
                .filter(k -> nameLike == null || nameLike.isBlank() || k.name.toLowerCase().contains(nameLike.toLowerCase()))
                .sorted(Comparator.comparing((KnowledgeBaseSummary k) -> k.createdAt).reversed())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public KnowledgeBaseSummary create(KnowledgeBaseUpsertRequest req) {
        KnowledgeBaseSummary k = new KnowledgeBaseSummary();
        k.id = IdService.newId();
        k.name = req.name;
        k.description = req.description;
        k.tags = req.tags;
        k.createdAt = Instant.now();
        k.updatedAt = k.createdAt;
        store.put(k.id, k);
        return k;
    }

    public KnowledgeBaseSummary get(String id) {
        KnowledgeBaseSummary k = store.get(id);
        if (k == null) throw new NotFoundException("KB not found: " + id);
        return k;
    }

    public KnowledgeBaseSummary update(String id, KnowledgeBaseUpsertRequest req) {
        KnowledgeBaseSummary k = get(id);
        k.name = req.name;
        k.description = req.description;
        k.tags = req.tags;
        k.updatedAt = java.time.Instant.now();
        return k;
    }

    public void delete(String id) {
        if (store.remove(id) == null) {
            throw new NotFoundException("KB not found: " + id);
        }
    }
} 