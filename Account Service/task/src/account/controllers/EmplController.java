package account.controllers;

import account.models.Payment;
import account.models.ResponseStatus;
import account.models.User;
import account.models.UserPayment;
import account.models.exceptions.BadRequestException;
import account.repositories.UserRepository;
import account.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@Validated
public class EmplController {

    @Autowired
    UserRepository repository;

    @Autowired
    PaymentService service;

    @ExceptionHandler({ConstraintViolationException.class, org.hibernate.exception.ConstraintViolationException.class})
    public void springHandleNotFound(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

    @GetMapping("/api/empl/payment")
    public ResponseEntity<Object> payment(@RequestParam(required = false) String period) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails details = (UserDetails) auth.getPrincipal();
        User user = repository.findByEmail(details.getUsername().toLowerCase());
        try {
            List<UserPayment> payments = service.getPayment(period, user);
            if (payments.size() == 1) {
                return new ResponseEntity<>(payments.get(0), HttpStatus.OK);
            }
            return new ResponseEntity<>(payments, HttpStatus.OK);
        } catch (Exception ex) {
            throw new BadRequestException();
        }

    }

    @PostMapping("/api/acct/payments")
    public ResponseEntity<ResponseStatus> payments(@RequestBody List<@Valid Payment> payments) {

        try {
            service.savePayments(payments);
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
            throw new BadRequestException();
        }
        return new ResponseEntity<>(new ResponseStatus("Added successfully!"), HttpStatus.OK);

    }

    @PutMapping("/api/acct/payments")
    public ResponseEntity<ResponseStatus> paymentUpdate(@RequestBody @Valid Payment payment) {

        try {
            service.updatePayment(payment);
        } catch (RuntimeException ex) {
            throw new BadRequestException();
        }
        return new ResponseEntity<>(new ResponseStatus("Updated successfully!"), HttpStatus.OK);
    }

}
