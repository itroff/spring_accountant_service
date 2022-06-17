package account.controllers;


import account.models.*;
import account.models.ResponseStatus;
import account.models.exceptions.CustomException;
import account.models.exceptions.CustomNotFoundException;
import account.models.exceptions.PassLenException;
import account.repositories.UserRepository;
import account.services.DataLoader;
import account.services.EventService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/admin/")
@Validated
public class AdminController {

    @Autowired
    UserRepository repository;

    @Autowired
    DataLoader dataLoader;

    @Autowired
    EventService log;

    @ExceptionHandler({HttpMessageNotReadableException.class, org.hibernate.exception.ConstraintViolationException.class})
    public void springHandleNotFound(HttpServletResponse response) throws IOException, CustomNotFoundException {
        response.sendError(HttpStatus.NOT_FOUND.value(), "Role not found!");
    }

    @GetMapping("user")
    public List<User> users() {
        return (List<User>) repository.findAll();
    }

    @DeleteMapping("user/{email}")
    public ResponseEntity<DeleteUserResponse> deleteUser(@PathVariable String email) {
        User user = repository.findByEmail(email);
        if (user == null) {
            throw new CustomNotFoundException("User not found!");
        }
        log.addEvent(Event.DELETE_USER, user.getEmail() , "/api/admin/user");
        if (user.getUserGroups().contains(dataLoader.getByRole(UserRole.ADMINISTRATOR))){
            throw new CustomException("Can't remove ADMINISTRATOR role!");
        }
        try {
            repository.delete(repository.findByEmail(email));
        } catch (Exception ex) {
            throw new CustomException("Error while deleting user!");
        }
        return new ResponseEntity<>(new DeleteUserResponse(email), HttpStatus.OK);
    }

    @PutMapping("user/role")
    public ResponseEntity<User> changeRole(@Valid @RequestBody ChangeRoleRequest request, Errors errors) {
            if (errors.hasErrors()) {
                throw new CustomNotFoundException("Role not found!");
            }
            User user = repository.findByEmail(request.getUser().toLowerCase());
            if (user == null) {
                throw new CustomNotFoundException("User not found!");
            }

            if (request.getOperation() == ChangeRoleRequest.RoleAction.GRANT) {
                user.addGroup(dataLoader.getByRole(request.getRole()));
                log.addEvent(Event.GRANT_ROLE, "Grant role " + request.getRole().name() + " to " + request.getUser().toLowerCase() , "/api/admin/user/role");
            } else if(request.getOperation() == ChangeRoleRequest.RoleAction.REMOVE) {

                if(request.getRole() == UserRole.ADMINISTRATOR) {
                    throw new CustomException("Can't remove ADMINISTRATOR role!");
                }
               if (!user.getUserGroups().contains(dataLoader.getByRole(request.getRole()))) {
                    throw new CustomException("The user does not have a role!");
                }
                if (user.getUserGroups().size() == 1) {
                    throw new CustomException("The user must have at least one role!");
                }

                user.deleteRole(dataLoader.getByRole(request.getRole()));
                log.addEvent(Event.REMOVE_ROLE, "Remove role " + request.getRole().name() + " from " + request.getUser().toLowerCase() , "/api/admin/user/role");

            } else {
                throw new CustomNotFoundException("Role not found!");
            }
            repository.save(user);
            return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("user/access")
    public ResponseEntity<ResponseStatus> access(@Valid @RequestBody ChangeAccessRequest req) {
        User user = repository.findByEmail(req.getUser().toLowerCase());
        if (user == null) {
            throw new CustomNotFoundException("User not found!");
        }
        if (req.getOperation() == ChangeAccessRequest.Operation.LOCK  &&
                user.getUserGroups().contains(dataLoader.getByRole(UserRole.ADMINISTRATOR))) {
            throw new CustomException("Can't lock the ADMINISTRATOR!");
        }
        String status = "";
        if(req.getOperation() == ChangeAccessRequest.Operation.LOCK) {
            status = "User " + req.getUser().toLowerCase() +" locked!";
            user.setAccountNonLocked(false);
            log.addEvent(Event.LOCK_USER, "Lock user " + user.getEmail(), "/api/admin/user/access");
        } else {
            status = "User " + req.getUser().toLowerCase() +" unlocked!";
            user.setAccountNonLocked(true);
            user.setFailedAttempt(0);
            log.addEvent(Event.UNLOCK_USER, "Unlock user " + user.getEmail(), "/api/admin/user/access");
        }
        user.setAccountNonLocked(req.getOperation() != ChangeAccessRequest.Operation.LOCK);
        repository.save(user);
        return new ResponseEntity<>(new ResponseStatus(status), HttpStatus.OK);
    }
}
