package org.example.exception.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.warn(
                "FORBIDDEN. Отсутствуют права доступа для доступа к '{}'. {}",
                request.getRequestURI(), accessDeniedException.getMessage());

        String path = Optional.ofNullable(request.getQueryString())
                .map(q -> request.getRequestURI() + "?" + q)
                .orElse(request.getRequestURI());

        path = path.startsWith("/") ? path.substring(1) : path;

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = new ErrorResponse(
                path,
                HttpStatus.FORBIDDEN.value(),
                "Недостаточно прав доступа",
                "FORBIDDEN",
                OffsetDateTime.now()
        );

        response.getWriter().write(objectMapper.writeValueAsString(
                errorResponse));
    }
}
