package taskmanagement.dto;

/**
 * @author MishaFre96
 *
 * DTO para recibir la solicitud de cambio de estado de una tarea.
 */
public class StatusRequest {

    private String status;

    public StatusRequest() {}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}