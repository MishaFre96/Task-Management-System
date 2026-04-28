package taskmanagement.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import taskmanagement.dto.TaskRequest;
import taskmanagement.model.Task;
import taskmanagement.repository.CommentRepository;
import taskmanagement.repository.TaskRepository;
import taskmanagement.repository.UserRepository;

import java.time.LocalDateTime;

import java.util.ArrayList;
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
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    /**
     * Constructor del servicio.
     */
    public TaskService(TaskRepository taskRepository, UserRepository userRepository, CommentRepository commentRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
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
        task.setAuthor(author.toLowerCase());
        task.setAssignee("none");

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

    /**
     * Asigna o desasigna una tarea a otro usuario.
     * Solo el autor de la tarea puede hacerlo.
     *
     * @param taskId ID de la tarea.
     * @param assigneeEmail email del usuario al que se asigna o "none".
     * @param currentUserEmail email del usuario que asigna la tarea.
     * @return tarea actualizada.
     * @throws ResponseStatusException si la tarea no existe (404),
     * el asignador no es autor (403) o el asignado no es válido (400).
     */
    @Transactional
    public Task assignTask (Long taskId, String assigneeEmail, String currentUserEmail) {

        // Buscar la tarea
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // Verificar que el asignador es el autor
        if(!task.getAuthor().equalsIgnoreCase(currentUserEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        // Validar el assignee
        if(assigneeEmail == null || assigneeEmail.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if("none".equalsIgnoreCase(assigneeEmail)){
            // desasiganamos si el objetivo es "none"
            task.setAssignee("none");
        } else {
            // verificamos que el assignee es un email con formato válido
            if(!assigneeEmail.matches(".+@.+\\..+")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
            // Verificamos que el asignado existe
            boolean userExists = userRepository.existsByEmailIgnoreCase(assigneeEmail);
            if (!userExists) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            // Asignamos la tarea
            task.setAssignee(assigneeEmail.toLowerCase());
        }

        // Guardar la tarea actualizada
        return taskRepository.save(task);
    }

    /**
     * Cambia el estado de una tarea.
     * Solo el autor o el asignado puede hacerlo.
     *
     * @param taskId ID de la tarea.
     * @param newStatus nuevo estado (CREATED, IN_PROGRESS, COMPLETED).
     * @param currentUserEmail email del usuario que hace la petición.
     * @return la tarea actualizada.
     * @throws ResponseStatusException si la tarea no existe, el usuario no está autorizado o el estado no es válido.
     */
    @Transactional
    public Task updateStatus(Long taskId, String newStatus, String currentUserEmail) {


        // Buscamos la tarea
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // Verificamos que el usuario actual no es autor o el asignado
        boolean isAuthor = task.getAuthor().equalsIgnoreCase(currentUserEmail);
        boolean isAssignee = task.getAssignee() != null && !"none".equalsIgnoreCase(task.getAssignee())
                && task.getAssignee().equalsIgnoreCase(currentUserEmail);
        if (!isAuthor && !isAssignee) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        // Validamos el nuevo status
        if(newStatus == null || newStatus.isBlank()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        Task.TaskStatus parsedStatus;
        try {
            parsedStatus = Task.TaskStatus.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        // Actualizamos
        task.setStatus(parsedStatus);

        // Guardamos la tarea
        return taskRepository.save(task);
    }

    /**
     * Devuelve las tareas filtradas por asignado.
     * La búsqueda no distingue mayúsculas/minúsculas.
     *
     * @param assignee email del usuario asignado
     * @return lista de tareas asignadas a ese usuario
     */
    public List<Task> getTasksByAssignee(String assignee) {
        return taskRepository.findByAssigneeIgnoreCaseOrderByCreatedAtDesc(assignee);
    }

    /**
     * Devuelve las tareas que cumplen ambos filtros: author y assignee.
     * Obtiene las tareas que están en ambas listas.
     *
     * @param author email del autor
     * @param assignee email del asignado
     * @return lista de tareas que coinciden con ambos
     */
    public List<Task> getTasksByAuthorAndAssignee(String author, String assignee) {
        List<Task> byAuthor = getTasksByAuthor(author);
        List<Task> byAssignee = getTasksByAssignee(assignee);
        List<Task> result = new ArrayList<>();

        // Recorremos las tareas del autor y vemos si también están en la lista de asignadas
        for (Task task : byAuthor) {
            if (byAssignee.contains(task)) {
                result.add(task);
            }
        }
        return result;
    }

    /**
     * Devuelve el número de comentarios de una tarea.
     * @param task la tarea
     * @return cantidad de comentarios
     */
    public int getCommentCount(Task task) {
        return (int) commentRepository.countByTask(task);
    }
}