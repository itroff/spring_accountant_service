package account.models;

public class ChangeAccessRequest {

    public enum Operation {
        LOCK, UNLOCK
    }

    private String user;
    private Operation operation;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }
}
