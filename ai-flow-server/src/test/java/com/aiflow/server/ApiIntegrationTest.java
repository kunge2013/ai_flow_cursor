package com.aiflow.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApiIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void testAllApis() throws Exception {
        // Flows: create
        String createFlowBody = "{\"name\":\"test-flow\",\"description\":\"d\"}";
        String createFlowResp = mockMvc.perform(post("/api/flows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createFlowBody))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        JsonNode flow = objectMapper.readTree(createFlowResp);
        String flowId = flow.get("id").asText();
        assertThat(flowId).isNotBlank();

        // Flows: get
        mockMvc.perform(get("/api/flows/" + flowId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(flowId));

        // Flows: update
        String updateFlowBody = "{\"name\":\"test-flow-2\",\"description\":\"dd\"}";
        mockMvc.perform(put("/api/flows/" + flowId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateFlowBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test-flow-2"));

        // Flows: save graph (start + llm)
        String graphBody = "{" +
                "\"nodes\":[{" +
                "  \"id\":\"start-1\",\"type\":\"rect\",\"x\":200,\"y\":120,\"text\":\"开始\",\"properties\":{\"role\":\"start\"}" +
                "},{" +
                "  \"id\":\"n-1\",\"type\":\"llm\",\"x\":360,\"y\":160,\"text\":\"LLM\",\"properties\":{\"title\":\"LLM\",\"model\":\"gpt-4o-mini\",\"input\":\"${question}\",\"output\":\"result\"}" +
                "}]," +
                "\"edges\":[{" +
                "  \"sourceNodeId\":\"start-1\",\"targetNodeId\":\"n-1\"" +
                "}]" +
                "}";
        mockMvc.perform(put("/api/flows/" + flowId + "/graph")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(graphBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nodes").isArray());

        // Flows: get graph
        mockMvc.perform(get("/api/flows/" + flowId + "/graph"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nodes").isArray());

        // Flows: run
        String runReq = "{\"inputs\":{\"question\":\"你好\"}}";
        mockMvc.perform(post("/api/flows/" + flowId + "/run")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(runReq))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flowId").value(flowId))
                .andExpect(jsonPath("$.trace").isArray());

        // Flows: list
        mockMvc.perform(get("/api/flows").param("name", "test"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        // Apps: create
        String createAppBody = "{\"name\":\"app1\",\"description\":\"d\",\"appType\":\"simple\"}";
        String createAppResp = mockMvc.perform(post("/api/apps")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createAppBody))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String appId = objectMapper.readTree(createAppResp).get("id").asText();

        // Apps: get
        mockMvc.perform(get("/api/apps/" + appId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(appId));

        // Apps: update
        String updateAppBody = "{\"name\":\"app1x\",\"description\":\"dx\",\"appType\":\"simple\"}";
        mockMvc.perform(put("/api/apps/" + appId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateAppBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("app1x"));

        // Apps: list
        mockMvc.perform(get("/api/apps").param("type", "simple"))
                .andExpect(status().isOk());

        // Models: create
        String createModelBody = "{\"name\":\"gpt-4o-mini\",\"provider\":\"openai\",\"model\":\"gpt-4o-mini\",\"enabled\":true}";
        String createModelResp = mockMvc.perform(post("/api/models")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createModelBody))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String modelId = objectMapper.readTree(createModelResp).get("id").asText();

        // Models: get
        mockMvc.perform(get("/api/models/" + modelId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(modelId));

        // Models: update
        String updateModelBody = "{\"id\":\"" + modelId + "\",\"name\":\"gpt-4o-mini\",\"provider\":\"openai\",\"model\":\"gpt-4o-mini\",\"enabled\":false}";
        mockMvc.perform(put("/api/models/" + modelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateModelBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled").value(false));

        // Models: list with filter
        mockMvc.perform(get("/api/models").param("enabled", "false"))
                .andExpect(status().isOk());

        // KB: create
        String createKbBody = "{\"name\":\"kb1\",\"description\":\"d\",\"tags\":[\"a\",\"b\"]}";
        String createKbResp = mockMvc.perform(post("/api/kb")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createKbBody))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String kbId = objectMapper.readTree(createKbResp).get("id").asText();

        // KB: get
        mockMvc.perform(get("/api/kb/" + kbId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(kbId));

        // KB: update
        String updateKbBody = "{\"name\":\"kb1x\",\"description\":\"dx\",\"tags\":[\"a\"]}";
        mockMvc.perform(put("/api/kb/" + kbId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateKbBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("kb1x"));

        // KB: list
        mockMvc.perform(get("/api/kb").param("name", "kb"))
                .andExpect(status().isOk());

        // Cleanup: delete resources
        mockMvc.perform(delete("/api/models/" + modelId))
                .andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/apps/" + appId))
                .andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/kb/" + kbId))
                .andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/flows/" + flowId))
                .andExpect(status().isNoContent());
    }
} 