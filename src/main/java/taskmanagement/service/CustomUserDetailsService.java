package taskmanagement.service;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import taskmanagement.repository.UserRepository;
import taskmanagement.model.User;

/**
 * @author MishaFre96
 *
 * Servicio usado por Spring Security para autenticar usuarios.
 * Convierte un usuario de la base de datos en un UserDetails.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Busca un usuario por email y lo adapta al formato de Spring Security.
     *
     * @param email email del usuario que intenta autenticarse.
     * @return UserDetails con los datos del usuario.
     * @throws UsernameNotFoundException si el usuario no existe.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities("USER")
                .build();
    }
}