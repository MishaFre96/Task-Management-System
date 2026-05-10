package taskmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import taskmanagement.model.Task;

import java.util.List;

/**
 * @author MishaFre96
 *
 * Repositorio de tareas.
 * Permite guardar y consultar tareas en la base de datos.
 */
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Obtiene todas las tareas ordenadas por fecha de creación descendente.
     *
     * @return lista de todas las tareas (ordenadas de más reciente a más antigua).
     */
    List<Task> findAllByOrderByCreatedAtDesc();

    /**
     * Busca tareas por autor (ignorando mayúsculas/minúsculas) ordenadas por fecha descendente.
     *
     * @param author email del autor por el que filtrar.
     * @return lista de tareas del autor, ordenadas.
     */
    List<Task> findByAuthorIgnoreCaseOrderByCreatedAtDesc(String author);

    /**
     * Busca tareas por asignado (ignorando mayúsculas/minúsculas) ordenadas por fecha descendente.
     *
     * @param assignee email del asignado por el que filtrar.
     * @return lista de tareas asignadas a esa persona, ordenadas.
     */
    List<Task> findByAssigneeIgnoreCaseOrderByCreatedAtDesc(String assignee);
}