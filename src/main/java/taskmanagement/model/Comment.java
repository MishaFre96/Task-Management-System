package taskmanagement.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 *
 * @author MishaFre96
 *
 * Entidad que representa un comentario asociado a una tarea.
 */
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    // Constructor vacio para JPA
    public Comment() {}

    // Getters y setters
    public Long getId(){
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor (String author) {
        this.author  = author;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Task getTask() {
        return task;
    }

    public void setTask (Task task) {
        this.task = task;
    }

}
