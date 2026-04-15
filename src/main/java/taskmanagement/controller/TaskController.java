package taskmanagement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import taskmanagement.dto.TaskRequest;
import taskmanagement.model.Task;
import taskmanagement.service.TaskService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador de tareas.
 * Gestiona creación y consulta de tareas.
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * POST /api/tasks
     * Crea una nueva tarea para el usuario autenticado.
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> createTask(@RequestBody TaskRequest request) {

        String author = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Task task = taskService.createTask(request, author);

        Map<String, String> response = new HashMap<>();
        response.put("id", String.valueOf(task.getId()));
        response.put("title", task.getTitle());
        response.put("description", task.getDescription());
        response.put("status", task.getStatus());
        response.put("author", task.getAuthor());

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/tasks
     * Devuelve todas las tareas o filtra por autor si se indica.
     */
    @GetMapping
    public ResponseEntity<List<Map<String, String>>> getTasks(@RequestParam(required = false) String author) {

        List<Task> tasks;

        if (author == null || author.isBlank()) {
            tasks = taskService.getAllTasks();
        } else {
            tasks = taskService.getTasksByAuthor(author);
        }

        List<Map<String, String>> response = new ArrayList<>();
        for (Task task : tasks) {
            Map<String, String> map = new HashMap<>();
            map.put("id", String.valueOf(task.getId()));
            map.put("title", task.getTitle());
            map.put("description", task.getDescription());
            map.put("status", task.getStatus());
            map.put("author", task.getAuthor());
            response.add(map);
        }
        return ResponseEntity.ok(response);
    }
}