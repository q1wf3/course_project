package ru.skfu.moviecollection.control.dto;

import java.util.UUID;
import ru.skfu.moviecollection.entity.Role;

public record AuthResponse(
        String token,
        UUID userId,
        String email,
        Role role
) {
}
