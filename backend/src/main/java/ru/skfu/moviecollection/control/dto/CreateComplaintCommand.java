package ru.skfu.moviecollection.control.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CreateComplaintCommand(
        @NotNull UUID movieId,
        @NotBlank @Size(max = 120) String reason,
        @NotBlank @Size(max = 2000) String description
) {
}
