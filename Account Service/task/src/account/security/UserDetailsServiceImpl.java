package account.security;

import account.models.User;
import account.repositories.UserRepository;
import account.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {


    @Autowired
    UserService service;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, LockedException {
        User user = service.getUser(username);

        if (user == null) {
            throw new UsernameNotFoundException("Not found: " + username);
        }
        /*   if (!user.isAccountNonLocked()) {
            throw new LockedException("User is locked");
        }*/

        return new UserDetailsImpl(user);
    }
}
