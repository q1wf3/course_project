package ru.skfu.moviecollection.control.dto;

import java.util.UUID;
import ru.skfu.moviecollection.entity.Role;

public record AdminUserDto(
        UUID id,
        String email,
        Role role,
        long moviesCount
) {
}
