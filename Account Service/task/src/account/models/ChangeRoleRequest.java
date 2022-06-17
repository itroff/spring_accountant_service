package account.models;



public class ChangeRoleRequest {

    public enum RoleAction {
        GRANT, REMOVE
    }
    private String user;
    private UserRole role;
    private RoleAction operation;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public RoleAction getOperation() {
        return operation;
    }

    public void setOperation(RoleAction operation) {
        this.operation = operation;
    }
}
