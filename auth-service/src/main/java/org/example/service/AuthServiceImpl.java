package org.example.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.AuthResponse;
import org.example.dto.NewUserRequestDto;
import org.example.dto.LoginRequest;
import org.example.exception.UserAlreadyExistsException;
import org.example.model.user.User;
import org.example.model.user.UserAuth;
import org.example.repository.UserRepository;
import org.example.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    PasswordEncoder encoder;
    UserRepository userRepository;
    AuthenticationManager authManager;
    JwtUtil jwtUtil;

    @Override
    @Transactional
    public void registration(NewUserRequestDto user) {
        log.info("Добавляем нового пользователя.");
        findUserByUsernameOrEmail(user.getUsername(), user.getEmail());

        String encodedPassword = encoder.encode(user.getPassword());

        User newUser = User.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .userAuth(UserAuth.builder().password(encodedPassword).build())
                .build();

        userRepository.save(newUser);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("Проводим аутентификацию пользователя.");
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        return new AuthResponse(jwtUtil.generateToken(request.getUsername()));
    }

    private void findUserByUsernameOrEmail(String username, String email) {
        log.info("Проверяем есть ли такой пользователь.");
        userRepository.findByUsernameOrEmail(username, email).ifPresent(user -> {
            throw new UserAlreadyExistsException("Пользователь уже существует.");
        });
    }
}
