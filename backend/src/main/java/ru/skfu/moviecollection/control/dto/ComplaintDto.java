package ru.skfu.moviecollection.control.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import ru.skfu.moviecollection.entity.ComplaintStatus;

public record ComplaintDto(
        UUID id,
        UUID reporterId,
        String reporterEmail,
        UUID movieId,
        String movieTitle,
        String reason,
        String description,
        ComplaintStatus status,
        String adminComment,
        LocalDateTime createdAt,
        LocalDateTime resolvedAt
) {
}
