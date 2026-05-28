package ru.skfu.moviecollection.control.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.skfu.moviecollection.entity.WatchStatus;

public record CreateMovieCommand(
        @NotBlank String title,
        @Min(1888) @Max(2100) int releaseYear,
        String director,
        @Min(1) int durationMinutes,
        String description,
        String coverUrl,
        String category,
        @NotNull WatchStatus status,
        @Min(1) @Max(10) Integer rating,
        String note
) {
}

