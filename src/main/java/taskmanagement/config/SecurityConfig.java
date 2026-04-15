package taskmanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author MishaFre96
 *
 * Clase de configuración de seguridad.
 * Aquí se indica qué endpoints requieren login y cuáles no.
 */
@Configuration
public class SecurityConfig {

    /**
     * Servicio que se usa para cargar usuarios desde la base de datos.
     */
    private final UserDetailsService userDetailsService;

    /**
     * Constructor.
     */
    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Codificador de contraseñas.
     * Se usa BCrypt para guardar contraseñas de forma segura.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuración de seguridad de la aplicación.
     *
     * - Login con usuario y contraseña (Basic Auth)
     * - No se usan sesiones
     * - CSRF desactivado para permitir peticiones de test
     *
     * Endpoints:
     * - /api/accounts : público (registro)
     * - /api/tasks : requiere estar autenticado
     * - /error, /actuator/shutdown, /h2-console : públicos
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/accounts").permitAll()
                        .requestMatchers("/api/tasks/**").authenticated()
                        .requestMatchers("/error", "/actuator/shutdown", "/h2-console/**").permitAll()
                        .anyRequest().denyAll()
                )
                .userDetailsService(userDetailsService)
                .build();
    }
}