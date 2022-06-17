package account.models.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "")
public class CustomNotFoundException extends RuntimeException{
    private String message;

    public CustomNotFoundException(String message) {
        super(message);
    }
}
