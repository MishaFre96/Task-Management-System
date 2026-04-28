package taskmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import taskmanagement.model.Comment;
import taskmanagement.model.Task;

import java.util.List;

/**
 *
 * @author MishaFre96
 *
 * Repositorio de la entidad Comment.
 * Proporciona métodos para acceder a los comentarios de tareas de la BD.
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Obtiene los comentarios de una tarea ordenados por fecha de creación descendente.
     *
     * @param task la tarea de la cual queremos ver los comentarios.
     * @return lista de comentarios ordenados.
     */
    List<Comment> findByTaskOrderByCreatedAtDesc(Task task);

    /**
     * Cuenta cuántos comentarios tiene una tarea.
     * @param task la tarea
     * @return número de comentarios
     */
    long countByTask(Task task);
}
