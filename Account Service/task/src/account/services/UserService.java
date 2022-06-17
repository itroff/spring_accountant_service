package account.services;

import account.models.Event;
import account.models.User;
import account.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Transactional
@Service
public class UserService {

    public static final int MAX_FAILED_ATTEMPTS = 5;

    @Autowired
    private UserRepository repo;

    public User getUser(String email) {
        return repo.findByEmail(email.toLowerCase());
    }
    public void increaseFailedAttempts(User user) {
        int newFailAttempts = user.getFailedAttempt() + 1;
        repo.updateFailedAttempts(newFailAttempts, user.getEmail().toLowerCase());
    }

    public void resetFailedAttempts(String email) {
        repo.updateFailedAttempts(0, email);
    }

    public void lock(User user) {
        user.setAccountNonLocked(false);
        repo.save(user);
    }

    public void unLock(User user) {
        user.setAccountNonLocked(true);
        repo.save(user);
    }

}
