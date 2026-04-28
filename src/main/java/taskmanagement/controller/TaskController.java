package taskmanagement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import taskmanagement.dto.AssignRequest;
import taskmanagement.dto.StatusRequest;
import taskmanagement.dto.TaskRequest;
import taskmanagement.model.Task;
import taskmanagement.service.TaskService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author MishaFre96
 *
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
        response.put("status", task.getStatus().name());
        response.put("author", task.getAuthor());
        response.put("assignee", task.getAssignee());

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/tasks
     * Devuelve todas las tareas o filtra por autor si se indica.
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getTask(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String assignee) {

        List<Task> tasks;

        if (author != null && !author.isBlank() && assignee != null && !assignee.isBlank()) {
            tasks = taskService.getTasksByAuthorAndAssignee(author, assignee);
        } else if (author != null && !author.isBlank()) {
            tasks = taskService.getTasksByAuthor(author);
        } else if (assignee != null && !assignee.isBlank()) {
            tasks = taskService.getTasksByAssignee(assignee);
        } else {
            tasks = taskService.getAllTasks();
        }

        List<Map<String, Object>> response = new ArrayList<>();
        for (Task task : tasks) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", String.valueOf(task.getId()));
            map.put("title", task.getTitle());
            map.put("description", task.getDescription());
            map.put("status", task.getStatus().name());
            map.put("author", task.getAuthor());
            map.put("assignee", task.getAssignee());
            map.put("total_comments", taskService.getCommentCount(task));   
            response.add(map);
        }
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/tasks/{taskId}/assign
     * Asigna o desasigna una tarea a otro usuario.
     * Solo el autor de la tarea puede hacerlo.
     *
     * @param taskId ID de la tarea.
     * @param request cuerpo con el campo "assignee".
     * @return la tarea actualizada.
     */
    @PutMapping("/{taskId}/assign")
    public ResponseEntity<Map<String, String>> assignTask(
            @PathVariable Long taskId,
            @RequestBody AssignRequest request) {

        // Obtenemos el email del usuario autenticado
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        // Llamamos al servicio para asignar/desasignar
        Task updatedTask = taskService.assignTask(taskId, request.getAssignee(), currentUserEmail);

        // Construimos la respuesta JSON
        Map<String, String> response = new HashMap<>();
        response.put("id", String.valueOf(updatedTask.getId()));
        response.put("title", updatedTask.getTitle());
        response.put("description", updatedTask.getDescription());
        response.put("status", updatedTask.getStatus().name());
        response.put("author", updatedTask.getAuthor());
        response.put("assignee", updatedTask.getAssignee());

        // Devolvemos 200 OK con la tarea actualizada
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/tasks/{taskId}/status
     * Cambia el estado de una tarea.
     * Solo el autor o el asignado pueden hacerlo.
     *
     * @param taskId ID de la tarea
     * @param request cuerpo con el campo "status"
     * @return la tarea actualizada
     */
    @PutMapping("/{taskId}/status")
    public ResponseEntity<Map<String, String>> updateStatus(
            @PathVariable Long taskId,
            @RequestBody StatusRequest request) {

        // Obtenemos el email de usuario autenticado
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        // Llamamos al servicio para cambiar el estado
        Task updatedTask = taskService.updateStatus(taskId, request.getStatus(), currentUserEmail);

        // Construimos la respuesta JSON
        Map<String, String> response = new HashMap<>();
        response.put("id", String.valueOf(updatedTask.getId()));
        response.put("title", updatedTask.getTitle());
        response.put("description", updatedTask.getDescription());
        response.put("status", updatedTask.getStatus().name());
        response.put("author", updatedTask.getAuthor());
        response.put("assignee", updatedTask.getAssignee());

        // Devolvemos 200 OK con la tarea actualizada
        return ResponseEntity.ok(response);
    }
}