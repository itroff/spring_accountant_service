package account.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.persistence.*;
import java.util.Set;

@Entity(name = "principle_groups")
@Table(name = "principle_groups")
public class UserGroup implements Comparable<UserGroup>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole code;

    private String name;

   /* @JsonValue
    public String toJson() {
        System.out.println("ROLE_" + this.code.name());
        return "ROLE_" + this.code.name();
    }
*/
    public UserGroup(UserRole code) {
        this.code = code;
    }

    public UserGroup() {
    }

    @ManyToMany(mappedBy = "userGroups", fetch = FetchType.EAGER)
    private Set<User> users;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserRole getCode() {
        return code;
    }

    public void setCode(UserRole code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString(){
        return this.code.name();
    }

    @Override
    public boolean equals(Object other) {

        if (this == other) {
            return true;
        }
        if (!(other instanceof UserGroup)) {
            return false;
        }

        UserGroup another = (UserGroup) other;
        if (this.code == another.code) {
            return true;
        }
        return false;

    }

    @Override
    public int hashCode() {
        return 17 + (code == null ? 0 : code.hashCode());
    }

    @Override
    public int compareTo(UserGroup another) {
        return another.code.compareTo(this.code);
       // return code.compareTo(another.code);
    }

}
