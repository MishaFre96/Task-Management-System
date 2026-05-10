package taskmanagement.model;

import jakarta.persistence.*;

/**
 * @author MishaFre96
 *
 * Entidad User.
 * Representa un usuario del sistema y se guarda en la base de datos.
 */
@Entity
@Table(name = "users")
public class User {

    /**
     * ID del usuario (clave primaria).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Email del usuario único.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Contraseña del usuario encriptada antes de guardarse.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Constructor vacío necesario para JPA.
     */
    public User() {
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    /**
     * Guarda el email en minúsculas para evitar problemas de mayúsculas/minúsculas.
     */
    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}