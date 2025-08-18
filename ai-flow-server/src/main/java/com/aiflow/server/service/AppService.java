package com.aiflow.server.service;

import com.aiflow.server.dto.AppDtos.AiAppSummary;
import com.aiflow.server.dto.AppDtos.AiAppUpsertRequest;
import com.aiflow.server.entity.AppEntity;
import com.aiflow.server.exception.NotFoundException;
import com.aiflow.server.mapper.AppMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppService {

    private final AppMapper appMapper;
    private final ObjectMapper objectMapper;

    public List<AiAppSummary> list(String nameLike, String type) {
        QueryWrapper<AppEntity> wrapper = new QueryWrapper<>();
        if (nameLike != null && !nameLike.isBlank()) {
            wrapper.like("name", nameLike);
        }
        if (type != null && !type.isBlank()) {
            wrapper.like("config_json", "\"appType\":\"" + type + "\"");
        }
        wrapper.orderByDesc("created_at");
        
        List<AppEntity> entities = appMapper.selectList(wrapper);
        return entities.stream()
                .map(this::toSummary)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public AiAppSummary create(AiAppUpsertRequest req) {
        AppEntity entity = new AppEntity();
        entity.setAppId(IdService.newId());
        entity.setName(req.name);
        entity.setDescription(req.description);
        entity.setConfigJson(serializeConfig(req));
        
        appMapper.insert(entity);
        return toSummary(entity);
    }

    public AiAppSummary get(String id) {
        AppEntity entity = getEntityByAppId(id);
        return toSummary(entity);
    }

    public AiAppSummary update(String id, AiAppUpsertRequest req) {
        AppEntity entity = getEntityByAppId(id);
        entity.setName(req.name);
        entity.setDescription(req.description);
        entity.setConfigJson(serializeConfig(req));
        
        appMapper.updateById(entity);
        return toSummary(entity);
    }

    public void delete(String id) {
        AppEntity entity = getEntityByAppId(id);
        appMapper.deleteById(entity.getId());
    }
    
    private AppEntity getEntityByAppId(String appId) {
        QueryWrapper<AppEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("app_id", appId);
        AppEntity entity = appMapper.selectOne(wrapper);
        if (entity == null) {
            throw new NotFoundException("App not found: " + appId);
        }
        return entity;
    }

    private AiAppSummary toSummary(AppEntity entity) {
        AiAppSummary summary = new AiAppSummary();
        summary.id = entity.getAppId();
        summary.name = entity.getName();
        summary.description = entity.getDescription();
        summary.createdAt = entity.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant();
        summary.updatedAt = entity.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant();
        
        // 从配置JSON中解析其他字段
        AppConfig config = deserializeConfig(entity.getConfigJson());
        if (config != null) {
            summary.iconUrl = config.iconUrl;
            summary.appType = config.appType;
            summary.model = config.model;
            summary.prompt = config.prompt;
            summary.openingRemark = config.openingRemark;
        }
        
        return summary;
    }
    
    private String serializeConfig(AiAppUpsertRequest req) {
        try {
            AppConfig config = new AppConfig();
            config.iconUrl = req.iconUrl;
            config.appType = req.appType;
            config.model = req.model;
            config.prompt = req.prompt;
            config.openingRemark = req.openingRemark;
            return objectMapper.writeValueAsString(config);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize app config", e);
        }
    }
    
    private AppConfig deserializeConfig(String configJson) {
        try {
            if (configJson == null || configJson.isBlank()) {
                return new AppConfig();
            }
            return objectMapper.readValue(configJson, AppConfig.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize app config", e);
        }
    }
    
    // 内部配置类
    private static class AppConfig {
        public String iconUrl;
        public String appType;
        public String model;
        public String prompt;
        public String openingRemark;
        
        public AppConfig() {}
    }
} 