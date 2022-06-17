package account.repositories;

import account.models.User;
import account.models.UserGroup;
import account.models.UserRole;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GroupRepository extends CrudRepository<UserGroup, Long> {
    @Query("SELECT u FROM principle_groups u where code=:code")
    Optional<UserGroup> findByCode(@Param("code") UserRole code);
}
