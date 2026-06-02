package ru.skfu.moviecollection.mediator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.skfu.moviecollection.config.JwtService;
import ru.skfu.moviecollection.control.dto.AuthRequest;
import ru.skfu.moviecollection.entity.Role;
import ru.skfu.moviecollection.entity.User;
import ru.skfu.moviecollection.foundation.UserRepository;

class AuthServiceImplTest {
    private final UserRepository userRepository = mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final JwtService jwtService = mock(JwtService.class);
    private final AuthServiceImpl authService = new AuthServiceImpl(userRepository, passwordEncoder, jwtService);

    @Test
    void registerCreatesUserAndReturnsToken() {
        var request = new AuthRequest("new@example.com", "123456");
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.password())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");

        var result = authService.register(request);

        assertEquals("jwt-token", result.token());
        assertEquals(request.email(), result.email());
        assertEquals(Role.USER, result.role());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerRejectsExistingEmail() {
        var request = new AuthRequest("test@yandex.ru", "123456");
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(new User(request.email(), "hash")));

        assertThrows(IllegalStateException.class, () -> authService.register(request));
    }

    @Test
    void loginReturnsTokenForValidPassword() {
        var request = new AuthRequest("test@yandex.ru", "123456");
        var user = new User(request.email(), "encoded");
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.password(), user.getPasswordHash())).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        var result = authService.login(request);

        assertEquals("jwt-token", result.token());
        assertEquals(user.getId(), result.userId());
    }

    @Test
    void loginRejectsUnknownEmailOrBadPassword() {
        var request = new AuthRequest("test@yandex.ru", "123456");
        var user = new User(request.email(), "encoded");
        when(userRepository.findByEmail(request.email()))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.password(), user.getPasswordHash())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> authService.login(request));
        assertThrows(IllegalArgumentException.class, () -> authService.login(request));
    }
}
