package taskmanagement.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import taskmanagement.dto.TaskRequest;
import taskmanagement.model.Task;
import taskmanagement.repository.TaskRepository;
import java.time.LocalDateTime;

import java.util.List;

/**
 *
 * @author MishaFre96
 *
 * Servicio que contiene la lógica de negocio de las tareas.
 */
@Service
public class TaskService {

    /**
     * Repositorio que permite guardar y consultar tareas en la base de datos.
     */
    private final TaskRepository taskRepository;

    /**
     * Constructor del servicio.
     */
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Crea una nueva tarea en el sistema.
     *
     * Reglas:
     * - el título no puede ser null ni vacío
     * - la descripción no puede ser null ni vacía
     * - el autor se guarda en minúsculas
     *
     * Si los datos no son válidos, se lanza un error 400 (BAD REQUEST).
     *
     * @param request datos recibidos desde el endpoint
     * @param author email del usuario que crea la tarea
     * @return tarea guardada en la base de datos
     */
    public Task createTask(TaskRequest request, String author) {

        // Primero valido que los datos no estén vacíos
        if (request.getTitle() == null || request.getTitle().isBlank()
                || request.getDescription() == null || request.getDescription().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        // El autor no debería ser null porque viene del login, pero por si acaso lo compruebo
        if (author == null || author.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());

        // Paso el autor a minúsculas para que no haya problemas al filtrar
        String authorLower = author.toLowerCase();
        task.setAuthor(authorLower);

        // Fecha actual para ordenar luego
        task.setCreatedAt(LocalDateTime.now());

        // Guardo en la base de datos
        return taskRepository.save(task);

    }

    /**
     * Devuelve todas las tareas del sistema ordenadas
     * de más recientes a más antiguas.
     *
     * @return lista de tareas
     */
    public List<Task> getAllTasks() {
        return taskRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Devuelve las tareas filtradas por autor.
     * La búsqueda no distingue mayúsculas/minúsculas.
     *
     * @param author email del autor
     * @return lista de tareas del usuario
     */
    public List<Task> getTasksByAuthor(String author) {
        return taskRepository.findByAuthorIgnoreCaseOrderByCreatedAtDesc(author);
    }
}