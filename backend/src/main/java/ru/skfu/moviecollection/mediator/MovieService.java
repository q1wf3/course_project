package ru.skfu.moviecollection.mediator;

import java.util.List;
import java.util.UUID;
import ru.skfu.moviecollection.control.dto.CreateMovieCommand;
import ru.skfu.moviecollection.control.dto.MovieDto;
import ru.skfu.moviecollection.entity.WatchStatus;

public interface MovieService {
    MovieDto createMovie(UUID userId, CreateMovieCommand command);

    MovieDto updateMovie(UUID userId, UUID movieId, CreateMovieCommand command);

    List<MovieDto> getCollection(UUID userId);

    List<MovieDto> search(UUID userId, String query, WatchStatus status);

    MovieDto changeStatus(UUID userId, UUID movieId, WatchStatus status);

    void deleteFromCollection(UUID userId, UUID movieId);
}

