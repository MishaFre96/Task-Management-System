package taskmanagement.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import taskmanagement.model.User;
import taskmanagement.service.CustomUserDetailsService;
import taskmanagement.service.TokenService;

import java.io.IOException;

/**
 *
 * @author MishaFre96
 *
 * Filtro que intercepta peticiones para validar el token Bearer.
 * Si el token es válido autentica al usuario en Spring Security.
 */
@Component
public class BearerTokenAuthenticationFilter  extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final CustomUserDetailsService userDetailsService;

    public BearerTokenAuthenticationFilter(TokenService tokenService, CustomUserDetailsService userDetailsService) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        // Excluimos el endpoint del token
        if (path.startsWith("/api/auth/token")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Obtenemos la cabecera Authorization
        String authHeader = request.getHeader("Authorization");

        // Si no existe o no empieza por "Bearer" saltamos el filtro
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraemos el token eliminando la palabra "Bearer " del inicio
        String token = authHeader.substring(7);

        // Validamos el token para obtener null si no válido o User si sí válido
        User user = tokenService.validateToken(token);

        // Si el token es inválido o caducó no autenticamos
        if(user == null) {
            filterChain.doFilter(request, response);
            return;
        }

        /**
         * Convertimos nuestro User en UserDetails,
         * usando el servicio que tenemos para cargar el User
         * mediante su email.
         */
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

            // Creamos el objeto de autenticación con el usuario
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken
                    (userDetails, null, userDetails.getAuthorities());

            // Guardamos la autenticación en el contexto Spring Security
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Manejamos las excepciones
        } catch (UsernameNotFoundException e) {
            // No hacemos nada, simplemente no autenticamos
        }

        // Continuar la petición (tanto si se autenticó como si no)
        filterChain.doFilter(request, response);
    }

}
