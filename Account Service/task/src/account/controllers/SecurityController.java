package account.controllers;

import account.models.LogEvent;
import account.repositories.EventsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SecurityController {

    @Autowired
    EventsRepository repository;

    @GetMapping("/api/security/events")
    public ResponseEntity<List<LogEvent>> events() {
        return new ResponseEntity<>((List<LogEvent>) repository.findAll(), HttpStatus.OK);
    }
}
