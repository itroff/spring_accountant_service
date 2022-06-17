package account.models;


import account.models.exceptions.CustomException;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import net.bytebuddy.implementation.bind.annotation.Default;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.*;

@Entity(name = "user")
@Table(name = "user")
public class User {

    public User(String name, String lastname, String password, String email) {
        this.name = name;
        this.lastname = lastname;
        this.password = password;
        this.email = email;
        this.enabled = true;
    }

    public User() {
    }

    @Column
    private String name;

    @Column
    private String lastname;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column
    @Size(min = 12, message = "The password length must be at least 12 chars!")
    private String password;

    @Column(unique = true)
    private String email;

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean enabled;


    @Column(name = "account_non_locked")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean accountNonLocked = true;

    @Column(name = "failed_attempt")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int failedAttempt;

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public int getFailedAttempt() {
        return failedAttempt;
    }

    public void setFailedAttempt(int failedAttempt) {
        this.failedAttempt = failedAttempt;
    }


    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email.toLowerCase();
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public static User validateCredentials(User user) {
        if (user.email == null || user.password == null || user.name == null || user.lastname == null) {
            return null;
        }

        if (user.email.isEmpty() || user.password.isEmpty()  || user.name.isEmpty()  || user.lastname.isEmpty() ) {
            return null;
        }

        if (user.email.endsWith("@acme.com")) {
            return user;
        }
        return null;
    }

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    }, fetch = FetchType.EAGER)
    @JoinTable(name = "user_groups",
            joinColumns =@JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id"
            ))

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Set<UserGroup> userGroups = new TreeSet<>();

    @JsonProperty("roles")
    public List<String> getRoles() {
        List<String> roles = new ArrayList<>();
        for (UserGroup ug : userGroups) {
            roles.add("ROLE_" + ug.getCode().name());
        }
        Collections.sort(roles);
        return roles;
    }

    public Set<UserGroup> getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(Set<UserGroup> userGroups) {
        this.userGroups = userGroups;
    }

    public void deleteRole(UserGroup group) {
        userGroups.remove(group);
    }

    public void addGroup(UserGroup ug) throws CustomException {
        if (this.userGroups.contains(ug)) {
            throw new CustomException("add already existing role");
        }
        if ((this.userGroups.contains(new UserGroup(UserRole.USER) )
                || this.userGroups.contains(new UserGroup(UserRole.ACCOUNTANT))
                || this.userGroups.contains(new UserGroup(UserRole.AUDITOR)))
                && ug.getCode() == UserRole.ADMINISTRATOR) {
            throw new CustomException("The user cannot combine administrative and business roles!");
        }

        if (this.userGroups.contains(new UserGroup(UserRole.ADMINISTRATOR))
                && (ug.getCode() == UserRole.ACCOUNTANT
                || ug.getCode() == UserRole.USER
                || ug.getCode() == UserRole.AUDITOR)) {
                throw new CustomException("The user cannot combine administrative and business roles!");
        }
        this.userGroups.add(ug);
    }
}
