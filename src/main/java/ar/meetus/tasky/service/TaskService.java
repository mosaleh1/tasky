package ar.meetus.tasky.service;

import ar.meetus.tasky.dto.TaskRequest;
import ar.meetus.tasky.dto.TaskResponse;
import ar.meetus.tasky.model.Task;
import ar.meetus.tasky.model.User;
import ar.meetus.tasky.repository.TaskRepository;
import ar.meetus.tasky.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public TaskResponse createTask(TaskRequest request) {
        User currentUser = getCurrentUser();

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus() != null ? request.getStatus() : Task.TaskStatus.OPEN);
        task.setUser(currentUser);

        Task savedTask = taskRepository.save(task);
        return new TaskResponse(savedTask);
    }

    public List<TaskResponse> getAllTasks() {
        User currentUser = getCurrentUser();
        List<Task> tasks = taskRepository.findByUserId(currentUser.getId());
        return tasks.stream()
                .map(TaskResponse::new)
                .collect(Collectors.toList());
    }

    public TaskResponse updateTask(Long taskId, TaskRequest request) {
        User currentUser = getCurrentUser();

        Task task = taskRepository.findByIdAndUserId(taskId, currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Task not found or access denied"));

        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }

        Task updatedTask = taskRepository.save(task);
        return new TaskResponse(updatedTask);
    }

    public void deleteTask(Long taskId) {
        User currentUser = getCurrentUser();

        Task task = taskRepository.findByIdAndUserId(taskId, currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Task not found or access denied"));

        taskRepository.delete(task);
    }
}