package taskmanagement.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import taskmanagement.model.Token;
import taskmanagement.model.User;
import taskmanagement.repository.TokenRepository;
import taskmanagement.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 *
 * @author MishaFre96
 *
 * Servicio que gestiona tokens.
 * Genera, valida y elimina tokens caducados.
 */
@Service
public class TokenService {

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    // Duración del token en horas (24 por ejemplo)
    private static final long EXPIRATION_HOURS = 24;

    public TokenService(TokenRepository tokenRepository, UserRepository userRepository) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    /**
     * Genera un nuevo token para eel usuario y lo guarda en la BBDD.
     * @param email usuario autenticado
     * @return el token generado (UUID)
     */
    @Transactional
    public String generateToken(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        // Crear nuevo token
        Token token = new Token();
        token.setTokenValue(UUID.randomUUID().toString());
        token.setUser(user);
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusHours(EXPIRATION_HOURS));
        token.setRevoked(false);

        tokenRepository.save(token);
        return token.getTokenValue();
    }

    /**
     * Valida un token y devuelve el usuario asociado si es válido
     */
    public User validateToken(String tokenValue) {
        Token token = tokenRepository.findByTokenValue(tokenValue).orElse(null);
        if(token == null || token.getExpiresAt().isBefore(LocalDateTime.now()) || token.isRevoked()){
            return null;
        }
        return token.getUser();
    }

    /**
     * Elimina todos los tokens caducados de la BBDD.
     */
    public void cleanExpiredTokens() {
        tokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }
}
