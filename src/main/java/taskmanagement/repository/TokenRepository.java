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
 * @author MishaFre96
 *
 * Repositorio con los métodos para manejar un token.
 */
public interface TokenRepository extends JpaRepository<Token, Long> {

    /**
     * Busca un token por su valor.
     *
     * @param tokenValue el string del token.
     * @return Optional con el token si existe, vacío si no.
     */
    Optional<Token> findByTokenValue(String tokenValue);

    /**
     * Elimina los tokens que han expirado (su fecha de expiración es anterior a la indicada).
     * No es requisito del proyecto, pero así mantenemos la tabla limpia.
     *
     * @param now fecha límite: se borran tokens con expiración anterior a este instante.
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Token t WHERE t.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);
}