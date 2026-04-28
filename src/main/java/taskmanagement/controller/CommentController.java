package taskmanagement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import taskmanagement.model.Comment;
import taskmanagement.service.CommentService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author MishaFre96
 *
 * Controlador para la gestión de comentarios.
 * Contiene los endpoints:
 *  POST /api/tasks/{taskId}/comments para agregar comentarios.
 *  GET /api/tasks/{taskId}/comments para obtener los comentarios de una tarea.
 */

@RestController
@RequestMapping("/api/tasks")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * POST /api/tasks/{taskId}/comments
     * Añade un comentario a una tarea.
     * @param taskId ID de la tarea
     * @param body JSON con el campo "text"
     * @return 200 OK si se guarda correctamente
     */
    @PostMapping("/{taskId}/comments")
    public ResponseEntity <Map<String, String>> addComment(@PathVariable Long taskId,
                                                           @RequestBody Map<String, String> body) {

        // Obtenermos el email del usuario autenticado
        String authorEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        String text = body.get("text");

        // Llamamos al servicio
        commentService.addComment(taskId, text, authorEmail);

        // Devolvemos 200 OK sin contenido como pide el enunciado
        return ResponseEntity.ok().build();
    }

    /**
     * GET /api/tasks/{taskId}/comments
     * Devuelve todos los comentarios de una tarea, ordenados de más nuevo a más antiguo.
     * @param taskId ID de la tarea
     * @return lista de comentarios en JSON
     */
    @GetMapping("/{taskId}/comments")
    public ResponseEntity<List<Map<String, String>>> getComments(@PathVariable Long taskId) {

        // Obtenemos los comentarios
        List<Comment> comments = commentService.getCommentsByTask(taskId);

        // Construimos la respuesta
        List<Map<String, String>> response = new ArrayList<>();
        for (Comment comment : comments) {
            Map<String, String> map = new HashMap<>();
            map.put("id", String.valueOf(comment.getId()));
            map.put("task_id", String.valueOf(comment.getTask().getId()));
            map.put("text", comment.getText());
            map.put("author", comment.getAuthor());
            response.add(map);
        }
        return ResponseEntity.ok(response);
    }
}
