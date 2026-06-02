package ru.skfu.moviecollection.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import ru.skfu.moviecollection.entity.User;

class JwtServiceTest {
    @Test
    void generateTokenReturnsReadableSignedJwt() {
        var jwtService = new JwtService(new ObjectMapper(), "test-secret");
        var user = new User("user@example.com", "hash");

        var token = jwtService.generateToken(user);
        var resolvedUserId = jwtService.resolveUserId("Bearer " + token);

        assertEquals(3, token.split("\\.").length);
        assertEquals(user.getId(), resolvedUserId);
    }

    @Test
    void resolveUserIdRejectsTamperedToken() {
        var jwtService = new JwtService(new ObjectMapper(), "test-secret");
        var token = jwtService.generateToken(new User("user@example.com", "hash"));
        var tamperedToken = token.substring(0, token.length() - 2) + "xx";

        assertThrows(IllegalArgumentException.class, () -> jwtService.resolveUserId("Bearer " + tamperedToken));
    }
}
