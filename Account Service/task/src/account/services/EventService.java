package account.services;

import account.models.Event;
import account.models.LogEvent;
import account.models.User;
import account.repositories.EventsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EventService {

    @Autowired
    EventsRepository repository;

    public void addEventWithSubject(Event event, String object, String path, String subject) {
        LogEvent lg = new LogEvent();
        lg.setAction(event);
        lg.setDate(LocalDateTime.now());
        lg.setObject(object);
        lg.setPath(path);
        lg.setSubject(subject);
        repository.save(lg);
    }
    public void addEvent(Event event, String object, String path) {
        String subject;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal().getClass() == String.class) {
            subject = "Anonymous";
        } else {
            UserDetails details = (UserDetails) auth.getPrincipal();
            subject = details.getUsername().toLowerCase();
        }
        addEventWithSubject(event, object, path, subject);
    }
}
