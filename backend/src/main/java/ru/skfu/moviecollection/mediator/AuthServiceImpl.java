package ru.skfu.moviecollection.mediator;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skfu.moviecollection.config.JwtService;
import ru.skfu.moviecollection.control.dto.AuthRequest;
import ru.skfu.moviecollection.control.dto.AuthResponse;
import ru.skfu.moviecollection.entity.User;
import ru.skfu.moviecollection.foundation.UserRepository;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponse register(AuthRequest request) {
        userRepository.findByEmail(request.email()).ifPresent(user -> {
            throw new IllegalStateException("Пользователь с таким email уже существует");
        });
        var user = new User(request.email(), passwordEncoder.encode(request.password()));
        var savedUser = userRepository.save(user);
        return toResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(AuthRequest request) {
        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Неверный email или пароль"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Неверный email или пароль");
        }
        return toResponse(user);
    }

    private AuthResponse toResponse(User user) {
        return new AuthResponse(jwtService.generateToken(user), user.getId(), user.getEmail(), user.getRole());
    }
}
