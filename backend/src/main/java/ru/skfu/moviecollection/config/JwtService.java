package ru.skfu.moviecollection.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.skfu.moviecollection.entity.Role;
import ru.skfu.moviecollection.entity.User;

@Component
public class JwtService {
    private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder BASE64_URL_DECODER = Base64.getUrlDecoder();
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final long TOKEN_TTL_SECONDS = 60L * 60L * 24L;

    private final ObjectMapper objectMapper;
    private final byte[] secret;

    public JwtService(
            ObjectMapper objectMapper,
            @Value("${app.jwt.secret:movie-collection-dev-secret-change-me}") String secret
    ) {
        this.objectMapper = objectMapper;
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
    }

    public String generateToken(User user) {
        var now = Instant.now().getEpochSecond();
        var header = Map.of(
                "alg", "HS256",
                "typ", "JWT"
        );
        var payload = new LinkedHashMap<String, Object>();
        payload.put("sub", user.getId().toString());
        payload.put("email", user.getEmail());
        payload.put("role", user.getRole().name());
        payload.put("iat", now);
        payload.put("exp", now + TOKEN_TTL_SECONDS);

        var encodedHeader = encodeJson(header);
        var encodedPayload = encodeJson(payload);
        var unsignedToken = encodedHeader + "." + encodedPayload;
        return unsignedToken + "." + sign(unsignedToken);
    }

    public UUID resolveUserId(String authorizationHeader) {
        return resolveClaims(authorizationHeader).userId();
    }

    public JwtClaims resolveClaims(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("JWT-токен отсутствует");
        }
        var token = authorizationHeader.substring("Bearer ".length()).trim();
        var parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("JWT-токен имеет некорректный формат");
        }

        var unsignedToken = parts[0] + "." + parts[1];
        if (!sign(unsignedToken).equals(parts[2])) {
            throw new IllegalArgumentException("JWT-подпись некорректна");
        }

        try {
            var payloadJson = new String(BASE64_URL_DECODER.decode(parts[1]), StandardCharsets.UTF_8);
            Map<String, Object> payload = objectMapper.readValue(payloadJson, new TypeReference<Map<String, Object>>() {
            });
            var expiresAt = ((Number) payload.get("exp")).longValue();
            if (Instant.now().getEpochSecond() > expiresAt) {
                throw new IllegalArgumentException("JWT-токен истек");
            }
            return new JwtClaims(
                    UUID.fromString((String) payload.get("sub")),
                    (String) payload.get("email"),
                    Role.valueOf((String) payload.get("role"))
            );
        } catch (IllegalArgumentException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalArgumentException("JWT-токен не удалось прочитать", exception);
        }
    }

    public JwtClaims requireAdmin(String authorizationHeader) {
        var claims = resolveClaims(authorizationHeader);
        if (claims.role() != Role.ADMIN) {
            throw new SecurityException("Требуется роль ADMIN");
        }
        return claims;
    }

    private String encodeJson(Object value) {
        try {
            return BASE64_URL_ENCODER.encodeToString(objectMapper.writeValueAsBytes(value));
        } catch (Exception exception) {
            throw new IllegalStateException("Не удалось сформировать JWT", exception);
        }
    }

    private String sign(String unsignedToken) {
        try {
            var mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret, HMAC_ALGORITHM));
            return BASE64_URL_ENCODER.encodeToString(mac.doFinal(unsignedToken.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("Не удалось подписать JWT", exception);
        }
    }

    public record JwtClaims(UUID userId, String email, Role role) {
    }
}
