package com.aiflow.server.controller;

import com.aiflow.server.dto.AppDtos.AiAppSummary;
import com.aiflow.server.dto.AppDtos.AiAppUpsertRequest;
import com.aiflow.server.service.AppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/apps")
@Tag(name = "App", description = "应用管理")
public class AppController {

    private final AppService appService = new AppService();

    @GetMapping
    @Operation(summary = "查询应用列表")
    public List<AiAppSummary> list(
            @Parameter(description = "名称模糊匹配") @RequestParam(name = "name", required = false) String name,
            @Parameter(description = "类型(simple|advanced)") @RequestParam(name = "type", required = false) String type
    ) {
        return appService.list(name, type);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "创建应用")
    public AiAppSummary create(@Valid @RequestBody AiAppUpsertRequest req) {
        return appService.create(req);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取应用详情")
    public AiAppSummary get(@PathVariable("id") String id) {
        return appService.get(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新应用")
    public AiAppSummary update(@PathVariable("id") String id, @Valid @RequestBody AiAppUpsertRequest req) {
        return appService.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "删除应用")
    public void delete(@PathVariable("id") String id) {
        appService.delete(id);
    }
} 