package taskmanagement.dto;

/**
 * @author MishaFre96
 *
 * DTO para recibir la solicitud de asignar/desasignar una tarea.
 */
public class AssignRequest {

    private String assignee;

    public AssignRequest() {}

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee (String assignee) {
        this.assignee = assignee;
    }
}