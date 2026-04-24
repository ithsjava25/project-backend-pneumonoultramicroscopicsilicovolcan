package org.example.crimearchive.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

//@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERROR_VIEW = "error/error";

    private String buildError(Model model, int status, String title, String message) {
        model.addAttribute("status", status);
        model.addAttribute("title", title);
        model.addAttribute("message", message);
        return ERROR_VIEW;
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(NotFoundException ex, Model model) {
        return buildError(
                model,
                HttpStatus.NOT_FOUND.value(),
                "Sidan finns inte",
                ex.getMessage()
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoResource(NoResourceFoundException ex, Model model) {
        return buildError(
                model,
                HttpStatus.NOT_FOUND.value(),
                "Sidan finns inte",
                "Sidan du försöker nå kunde inte hittas."
        );
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleRuntime(RuntimeException ex, Model model) {
        return buildError(
                model,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internt serverfel",
                "Ett oväntat fel inträffade."
        );
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDenied(AuthorizationDeniedException ex, Model model) {
        return buildError(
                model,
                HttpStatus.FORBIDDEN.value(),
                "Åtkomst nekad",
                "Du har inte behörighet till den här resursen."
        );
    }
}
