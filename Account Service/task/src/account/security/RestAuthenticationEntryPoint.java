package account.security;


import account.models.Event;
import account.models.User;
import account.models.UserRole;
import account.services.DataLoader;
import account.services.EventService;
import account.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class RestAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

    @Autowired
    UserService service;

    @Autowired
    EventService log;

    @Autowired
    DataLoader dataLoader;


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

        if (authException.getClass() == BadCredentialsException.class) {
            final String authorization = request.getHeader("Authorization");
            if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
                // Authorization: Basic base64credentials
                String base64Credentials = authorization.substring("Basic".length()).trim();
                byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
                String credentials = new String(credDecoded, StandardCharsets.UTF_8);
                // credentials = username:password
                final String[] values = credentials.split(":", 2);

                String email = values[0].toLowerCase();
                log.addEventWithSubject(Event.LOGIN_FAILED, request.getServletPath(),
                        request.getServletPath(), email);
                User user = service.getUser(email);

                if (user != null) {

                    if (user.isEnabled() && user.isAccountNonLocked()) {
                        if (user.getFailedAttempt() < UserService.MAX_FAILED_ATTEMPTS - 1) {
                            service.increaseFailedAttempts(user);
                        } else {
                            log.addEventWithSubject(Event.BRUTE_FORCE, request.getServletPath(),
                                    request.getServletPath(), email);
                            log.addEventWithSubject(Event.LOCK_USER, "Lock user " + user.getEmail(),
                                    request.getServletPath(), email);
                            if(!user.getUserGroups().contains(dataLoader.getByRole(UserRole.ADMINISTRATOR))) {
                                service.lock(user);
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User account is locked");
                                return;
                            }

                        }
                    }

                }
            }
        } else if (authException.getClass() == LockedException.class) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User account is locked");
            return;
        }

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Wrong password");

    }

    @Override
    public void afterPropertiesSet() {
        setRealmName("account");
        super.afterPropertiesSet();
    }
}
