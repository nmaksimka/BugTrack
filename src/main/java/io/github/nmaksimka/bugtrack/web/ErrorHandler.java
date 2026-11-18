package io.github.nmaksimka.bugtrack.web;

import io.github.nmaksimka.bugtrack.common.ForbiddenException;
import io.github.nmaksimka.bugtrack.common.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String forbidden(ForbiddenException e, Model model) {
        model.addAttribute("title", "Нет доступа");
        model.addAttribute("message", e.getMessage());
        return "error/message";
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String notFound(NotFoundException e, Model model) {
        model.addAttribute("title", "Не найдено");
        model.addAttribute("message", e.getMessage());
        return "error/message";
    }
}
