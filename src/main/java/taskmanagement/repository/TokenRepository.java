package taskmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import taskmanagement.model.Token;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 *
 * @author MishaFre96
 *
 * Repositorio con los métodos para manejar un token.
 */
public interface TokenRepository extends JpaRepository<Token, Long> {

    // Buscar un token por su tokenValue
    Optional<Token> findByTokenValue(String tokenValue);

    /* Borrar token caducados con fecha de validez anterior a la acutal
       No es requisito de la fase 3 del proyecto, pero me pareció bien incluirlo.
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Token t WHERE t.expiresAt < :now")
    void deleteExpiredTokens(@Param("now")LocalDateTime now);
}
