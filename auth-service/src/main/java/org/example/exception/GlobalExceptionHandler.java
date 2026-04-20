package org.example.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.example.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;

import static org.springframework.http.HttpStatus.CONFLICT;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({UserAlreadyExistsException.class})
    @ResponseStatus(CONFLICT)
    private ErrorResponse handleNotFoundException(HttpServletRequest request) {
        return buildErrorResponse(request, CONFLICT, getErrorMessage(CONFLICT));
    }

    private String getErrorMessage(HttpStatus status) {
        return switch (status) {
            case BAD_REQUEST ->
                    "Некорректный запрос";
            case NOT_FOUND -> "Запрашиваемый ресурс не найден";
            case FORBIDDEN -> "Недостаточно прав доступа";
            case UNAUTHORIZED ->
                    "Требуется авторизация";
            case CONFLICT -> "Конфликт данных";
            default -> "Что-то пошло не так. Повторите попытку позже";
        };
    }

    private String getErrorName(HttpStatus status) {
        return switch (status) {
            case BAD_REQUEST -> "BAD REQUEST";
            case NOT_FOUND -> "NOT FOUND";
            case FORBIDDEN -> "FORBIDDEN";
            case UNAUTHORIZED -> "UNAUTHORIZED";
            case CONFLICT -> "CONFLICT";
            default -> "INTERNAL SERVER ERROR";
        };
    }

    private ErrorResponse buildErrorResponse(HttpServletRequest request, HttpStatus status,
                                             String message) {

        return new ErrorResponse(
                request.getRequestURI(),
                status.value(),
                getErrorName(status),
                message,
                OffsetDateTime.now()
        );
    }
}
