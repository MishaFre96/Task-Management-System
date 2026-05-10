package taskmanagement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import taskmanagement.service.UserService;

import java.util.Map;

/**
 * @author MishaFre96
 *
 * Controlador para la gestión de usuarios.
 * Aquí está el endpoint de registro (/api/accounts).
 */
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final UserService userService;

    public AccountController(UserService userService) {
        this.userService = userService;
    }

    /**
     * POST /api/accounts
     *
     * Registra un usuario con email y password.
     * Devuelve 200 OK si todo es correcto.
     *
     * @param body JSON con "email" y "password"
     */
    @PostMapping
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        userService.registerUser(email, password);

        return ResponseEntity.ok().build();
    }
}