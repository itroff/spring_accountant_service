package account.repositories;

import account.models.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

   @Query("SELECT u FROM user u where lower(email)=:email")
   User findByEmail(@Param("email") String email);

   @Query("UPDATE user u SET failed_attempt = ?1 WHERE lower(email) = ?2")
   @Modifying
   public void updateFailedAttempts(int failAttempts, String email);
}
