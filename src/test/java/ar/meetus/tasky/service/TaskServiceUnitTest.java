package ar.meetus.tasky.service;

import ar.meetus.tasky.dto.TaskRequest;
import ar.meetus.tasky.dto.TaskResponse;
import ar.meetus.tasky.model.Task;
import ar.meetus.tasky.model.User;
import ar.meetus.tasky.repository.TaskRepository;
import ar.meetus.tasky.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TaskServiceUnitTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TaskService taskService;

    private User user;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user@example.com");
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
    }

    @Test
    @DisplayName("createTask sets default OPEN status when not provided")
    void createTaskDefaultStatus() {
        TaskRequest request = new TaskRequest();
        request.setTitle("Sample");
        // no status set

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> {
            Task t = inv.getArgument(0);
            t.setId(10L);
            return t;
        });

        TaskResponse response = taskService.createTask(request);

        verify(taskRepository).save(captor.capture());
        Task saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo(Task.TaskStatus.OPEN);
        assertThat(response.getStatus()).isEqualTo(Task.TaskStatus.OPEN);
    }

    @Test
    @DisplayName("updateTask updates only provided fields")
    void updateTaskPartial() {
        Task existing = new Task();
        existing.setId(5L);
        existing.setTitle("Old");
        existing.setDescription("Desc");
        existing.setStatus(Task.TaskStatus.OPEN);
        existing.setUser(user);

        when(taskRepository.findByIdAndUserId(5L, user.getId())).thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskRequest update = new TaskRequest();
        update.setTitle("New Title");
        // description null -> should remain
        update.setStatus(Task.TaskStatus.IN_PROGRESS);

        TaskResponse response = taskService.updateTask(5L, update);

        assertThat(response.getTitle()).isEqualTo("New Title");
        assertThat(response.getDescription()).isEqualTo("Desc");
        assertThat(response.getStatus()).isEqualTo(Task.TaskStatus.IN_PROGRESS);
    }
}
