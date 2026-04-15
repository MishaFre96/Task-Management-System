package taskmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import taskmanagement.model.Task;

import java.util.List;

/**
 *
 * @author MishaFre96
 *
 * Repositorio de tareas.
 * Permite guardar y consultar tareas en la base de datos.
 */
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Obtener todas las tareas de un autor ignorando mayúsculas/minúsculas
    List<Task> findAllByOrderByCreatedAtDesc();

    // Obtener todas las tareas ordenadas por id de más reciente a más antigua
    List<Task> findByAuthorIgnoreCaseOrderByCreatedAtDesc(String author);
}