package org.example.exception.security;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        log.warn(
                "UNAUTHORIZED. Для доступа к '{}' требуется аутентификация. {}",
                request.getRequestURI(), authException.getMessage());

        String path = Optional.ofNullable(request.getQueryString())
                .map(q -> request.getRequestURI() + "?" + q)
                .orElse(request.getRequestURI());

        path = path.startsWith("/") ? path.substring(1) : path;

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = new ErrorResponse(
                path,
                HttpStatus.UNAUTHORIZED.value(),
                "Требуется авторизация",
                "UNAUTHORIZED",
                OffsetDateTime.now()
        );

        response.getWriter().write(objectMapper.writeValueAsString(
                errorResponse));
    }
}