package ar.meetus.tasky.task;

import ar.meetus.tasky.dto.LoginRequest;
import ar.meetus.tasky.dto.RegisterRequest;
import ar.meetus.tasky.dto.TaskRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TaskIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setName("Alice");
        registerRequest.setEmail("alice@example.com");
        registerRequest.setPassword("secret123");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("alice@example.com");
        loginRequest.setPassword("secret123");
    }

    private String obtainToken() throws Exception {
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
            .andExpect(status().isOk());

        String loginResponse = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        JsonNode node = objectMapper.readTree(loginResponse);
        return node.get("accessToken").asText();
    }

    @Test
    @DisplayName("Full task CRUD flow")
    void taskCrudFlow() throws Exception {
        String token = obtainToken();

        // Create Task
        TaskRequest create = new TaskRequest();
        create.setTitle("First Task");
        create.setDescription("Do something important");

        String createResponse = mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(create)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value("First Task"))
            .andReturn().getResponse().getContentAsString();

        JsonNode createdNode = objectMapper.readTree(createResponse);
        Long taskId = createdNode.get("id").asLong();

        // List Tasks
        mockMvc.perform(get("/tasks")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(taskId));

        // Update Task
        TaskRequest update = new TaskRequest();
        update.setTitle("Updated Task");
        update.setDescription("Updated description");

        mockMvc.perform(put("/tasks/" + taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(update)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Updated Task"));

        // Delete Task
        mockMvc.perform(delete("/tasks/" + taskId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Task deleted successfully"));

        // Verify deletion by listing again
        String listAfterDelete = mockMvc.perform(get("/tasks")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        JsonNode listNode = objectMapper.readTree(listAfterDelete);
        assertThat(listNode.isArray()).isTrue();
        assertThat(listNode.size()).isZero();
    }

    @Test
    @DisplayName("Access without token is unauthorized")
    void accessWithoutToken() throws Exception {
        mockMvc.perform(get("/tasks"))
            .andExpect(status().isForbidden()); // Could be 401 or 403 depending on config
    }
}
