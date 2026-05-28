package ru.skfu.moviecollection.control.dto;

import java.util.UUID;
import ru.skfu.moviecollection.entity.WatchStatus;

public record MovieDto(
        UUID id,
        String title,
        int releaseYear,
        String director,
        String coverUrl,
        String category,
        WatchStatus status,
        Integer rating,
        boolean favorite
) {
}

