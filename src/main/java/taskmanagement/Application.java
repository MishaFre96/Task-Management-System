package taskmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author MishaFre96
 *
 * Clase principal de la aplicación.
 * Es el punto de entrada de Spring Boot.
 */
@SpringBootApplication
public class Application {

    /**
     * Inicia la aplicación Spring Boot.
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}