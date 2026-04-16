package taskmanagement.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import taskmanagement.model.User;
import taskmanagement.repository.UserRepository;

/**
 *
 * @author MishaFre96
 *
 * Servicio que gestiona el registro de usuarios.
 * Valida los datos y guarda el usuario en la base de datos.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registra un usuario nuevo.
     *
     * Reglas básicas:
     * - email y password no pueden estar vacíos
     * - password mínimo 6 caracteres
     * - email con formato válido simple
     * - email no repetido
     */
    public void registerUser(String email, String password) {

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (password.length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (!email.matches(".+@.+\\..+")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);
    }

    /**
     * Busca un usuario por su email
     */
    public User findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email).orElse(null);
    }
}