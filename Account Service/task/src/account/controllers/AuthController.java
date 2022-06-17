package account.controllers;


import account.models.*;
import account.models.exceptions.*;
import account.repositories.UserRepository;
import account.services.DataLoader;
import account.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Set;

@RestController

public class AuthController {


    @Autowired
    PasswordEncoder encoder;

    @Autowired
    UserRepository repository;

    @Autowired
    Set<String> breachedPass;

    @Autowired
    DataLoader dataLoader;

    @Autowired
    EventService log;

    @PostMapping("/api/auth/signup")
    public ResponseEntity<User> signup(@Valid @RequestBody User user) {
        User userValidate = User.validateCredentials(user);

        if (userValidate == null) {
            throw new BadRequestException();
        }

        if (repository.findByEmail(user.getEmail().toLowerCase()) != null) {
            throw new UserExistException();
        }

        if (breachedPass.contains(user.getPassword())) {
            throw new BreachedPassException();
        }

        userValidate.setPassword(encoder.encode(user.getPassword()));

        if (repository.count() == 0) {
            userValidate.addGroup(dataLoader.getByRole(UserRole.ADMINISTRATOR));
        } else {
            userValidate.addGroup(dataLoader.getByRole(UserRole.USER));
        }
        userValidate.setEnabled(true);
        repository.save(userValidate);
        log.addEvent(Event.CREATE_USER, user.getEmail(),"/api/auth/signup");
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/api/auth/changepass")
    public ResponseEntity<ChangePassResponse> changePass(@Valid @RequestBody ChangePassRequest req,
                                                         Errors errors) {


        if (errors.hasErrors()) {
            throw new PassLenException();
        }

        if (breachedPass.contains(req.getNew_password())) {
            throw new BreachedPassException();
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails details = (UserDetails) auth.getPrincipal();
        User user = repository.findByEmail(details.getUsername().toLowerCase());
        log.addEvent(Event.CHANGE_PASSWORD, user.getEmail() , "/api/auth/changepass");
        if (encoder.matches(req.getNew_password(), user.getPassword())) {
            throw new SamePassException();
        }

        user.setPassword(encoder.encode(req.getNew_password()));
        repository.save(user);

        return new ResponseEntity<>(new ChangePassResponse(user.getEmail().toLowerCase()), HttpStatus.OK);

    }
}
