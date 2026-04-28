package taskmanagement.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import taskmanagement.model.Comment;
import taskmanagement.model.Task;
import taskmanagement.repository.CommentRepository;
import taskmanagement.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author MishaFre96
 *
 * Servicio que gestiona la lógica de los comentarios.
 */
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;

    public CommentService(CommentRepository commentRepository, TaskRepository taskRepository) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
    }

    /**
     * Crea un nuevo comentario en una tarea.
     *
     * @param taskId ID de la tarea a comentar
     * @param text texto del comentario (no puede estar vacío)
     * @param authorEmail usuario autenticado que escribe el comentario
     * @throws ResponseStatusException si la tarea no existe o el texto no es válido
     */
    public void addComment(Long taskId, String text, String authorEmail) {

        // Validamos que el comentario no este vacío.
        if (text == null || text.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        // Buscamos la tarea por su ID
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // Creamos y rellenamos el comentario
        Comment comment = new Comment();
        comment.setText(text);
        comment.setTask(task);
        comment.setAuthor(authorEmail);
        comment.setCreatedAt(LocalDateTime.now());

        // Guardamos en la BD
        commentRepository.save(comment);
    }

    /**
     * Devuelve todos los comentarios de una tarea ordenados de más reciente a antigua.
     *
     * @param taskId ID de la tarea
     * @return lista de comentarios
     * @throws ResponseStatusException si la tarea no existe
     */
    public List<Comment> getCommentsByTask(Long taskId) {

        // Verificamos que la tarea existe.
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return commentRepository.findByTaskOrderByCreatedAtDesc(task);
    }
}
