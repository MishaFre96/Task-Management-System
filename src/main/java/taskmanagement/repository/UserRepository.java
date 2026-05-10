package taskmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import taskmanagement.model.User;

import java.util.Optional;

/**
 * @author MishaFre96
 *
 * Repositorio de usuarios.
 * Se usa para acceder a la base de datos de usuarios.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por email sin importar mayúsculas o minúsculas.
     *
     * @param email email del usuario
     * @return usuario si existe, o vacío si no
     */
    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * Comprueba si ya existe un usuario con ese email.
     *
     * @param email email a comprobar
     * @return true si existe, false si no
     */
    boolean existsByEmailIgnoreCase(String email);
}