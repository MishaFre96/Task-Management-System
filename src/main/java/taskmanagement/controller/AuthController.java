package taskmanagement.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import taskmanagement.service.TokenService;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author MishaFre96
 *
 * Controlador de autenticación de tokens.
 * Endpoint /api/auth/token para obtener un token para usuarios autenticados.
 */

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final TokenService tokenService;

    /**
     * Constructor
     *
     * @param tokenService
     */
    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    /**
     * POST /api/auth/token
     *
     * @return map JSON
     */
    @PostMapping("/token")
    public ResponseEntity<Map<String, String>> getToken() {

        // Obtenemos la autenticación mediante Basic Auth de Spring Security
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Si la autenticación falla, devolvemos 401
        if(auth == null || !auth.isAuthenticated()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = auth.getName();

        // Generamos token para ese usuario
        String tokenValue = tokenService.generateToken(email);

        // Devolvemos el token en un JSON
        Map<String, String> response = new HashMap<>();
        response.put("token", tokenValue);
        return ResponseEntity.ok(response);

    }

}
