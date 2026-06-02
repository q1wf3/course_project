package ru.skfu.moviecollection.control;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.skfu.moviecollection.config.JwtService;
import ru.skfu.moviecollection.control.dto.CreateMovieCommand;
import ru.skfu.moviecollection.control.dto.MovieDto;
import ru.skfu.moviecollection.entity.WatchStatus;
import ru.skfu.moviecollection.mediator.MovieService;

@RestController
@RequestMapping("/api/movies")
public class MovieController {
    private final MovieService movieService;
    private final JwtService jwtService;

    public MovieController(MovieService movieService, JwtService jwtService) {
        this.movieService = movieService;
        this.jwtService = jwtService;
    }

    @GetMapping
    public List<MovieDto> list(@RequestHeader("Authorization") String authorization) {
        return movieService.getCollection(resolveUserId(authorization));
    }

    @GetMapping("/{movieId}")
    public MovieDto getById(
            @RequestHeader("Authorization") String authorization,
            @PathVariable UUID movieId
    ) {
        return movieService.getMovie(resolveUserId(authorization), movieId);
    }

    @GetMapping("/search")
    public List<MovieDto> search(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) WatchStatus status
    ) {
        return movieService.search(resolveUserId(authorization), query, status);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovieDto create(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody CreateMovieCommand command
    ) {
        return movieService.createMovie(resolveUserId(authorization), command);
    }

    @PutMapping("/{movieId}")
    public MovieDto update(
            @RequestHeader("Authorization") String authorization,
            @PathVariable UUID movieId,
            @Valid @RequestBody CreateMovieCommand command
    ) {
        return movieService.updateMovie(resolveUserId(authorization), movieId, command);
    }

    @PutMapping("/{movieId}/status")
    public MovieDto changeStatus(
            @RequestHeader("Authorization") String authorization,
            @PathVariable UUID movieId,
            @RequestParam WatchStatus status
    ) {
        return movieService.changeStatus(resolveUserId(authorization), movieId, status);
    }

    @DeleteMapping("/{movieId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestHeader("Authorization") String authorization, @PathVariable UUID movieId) {
        movieService.deleteFromCollection(resolveUserId(authorization), movieId);
    }

    private UUID resolveUserId(String authorization) {
        return jwtService.resolveUserId(authorization);
    }
}
