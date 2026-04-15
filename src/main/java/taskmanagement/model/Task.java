package taskmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 *
 * @author MishaFre96
 *
 * Entidad que representa una tarea del sistema.
 * Cada tarea pertenece a un usuario (autor) y tiene un estado.
 */
@Entity
@Table(name = "tasks")
public class Task {

    public static final String STATUS_CREATED = "CREATED";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String author;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Task() {
        this.status = STATUS_CREATED;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}