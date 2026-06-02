package ru.skfu.moviecollection.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import ru.skfu.moviecollection.config.JwtService;
import ru.skfu.moviecollection.control.dto.CreateMovieCommand;
import ru.skfu.moviecollection.control.dto.MovieDto;
import ru.skfu.moviecollection.entity.WatchStatus;
import ru.skfu.moviecollection.mediator.MovieService;

import static org.mockito.Mockito.mock;

class MovieControllerTest {
    private final MovieService movieService = mock(MovieService.class);
    private final JwtService jwtService = mock(JwtService.class);
    private final MovieController controller = new MovieController(movieService, jwtService);

    @Test
    void listReturnsUserCollection() {
        var userId = UUID.randomUUID();
        var authorization = "Bearer token";
        var movies = List.of(movieDto("Матрица"));
        when(jwtService.resolveUserId(authorization)).thenReturn(userId);
        when(movieService.getCollection(userId)).thenReturn(movies);

        var result = controller.list(authorization);

        assertEquals(movies, result);
        verify(movieService).getCollection(userId);
    }

    @Test
    void createDelegatesCommandToService() {
        var userId = UUID.randomUUID();
        var authorization = "Bearer token";
        var command = command("Сталкер");
        var dto = movieDto("Сталкер");
        when(jwtService.resolveUserId(authorization)).thenReturn(userId);
        when(movieService.createMovie(userId, command)).thenReturn(dto);

        var result = controller.create(authorization, command);

        assertEquals(dto, result);
        verify(movieService).createMovie(userId, command);
    }

    @Test
    void searchPassesQueryAndStatusToService() {
        var userId = UUID.randomUUID();
        var authorization = "Bearer token";
        var movies = List.of(movieDto("Интерстеллар"));
        when(jwtService.resolveUserId(authorization)).thenReturn(userId);
        when(movieService.search(userId, "интер", WatchStatus.WATCHED)).thenReturn(movies);

        var result = controller.search(authorization, "интер", WatchStatus.WATCHED);

        assertEquals(movies, result);
        verify(movieService).search(userId, "интер", WatchStatus.WATCHED);
    }

    @Test
    void updateStatusAndDeleteUseResolvedUserId() {
        var userId = UUID.randomUUID();
        var movieId = UUID.randomUUID();
        var authorization = "Bearer token";
        var dto = movieDto("Начало");
        when(jwtService.resolveUserId(authorization)).thenReturn(userId);
        when(movieService.getMovie(userId, movieId)).thenReturn(dto);
        when(movieService.updateMovie(userId, movieId, command("Начало"))).thenReturn(dto);
        when(movieService.changeStatus(userId, movieId, WatchStatus.WATCHING)).thenReturn(dto);

        assertEquals(dto, controller.getById(authorization, movieId));
        assertEquals(dto, controller.update(authorization, movieId, command("Начало")));
        assertEquals(dto, controller.changeStatus(authorization, movieId, WatchStatus.WATCHING));
        controller.delete(authorization, movieId);

        verify(movieService).deleteFromCollection(userId, movieId);
    }

    private static CreateMovieCommand command(String title) {
        return new CreateMovieCommand(
                title,
                1999,
                "Режиссер",
                120,
                "Описание",
                null,
                "Фантастика",
                WatchStatus.PLANNED,
                8,
                "Заметка"
        );
    }

    private static MovieDto movieDto(String title) {
        return new MovieDto(
                UUID.randomUUID(),
                title,
                1999,
                "Режиссер",
                null,
                "Фантастика",
                WatchStatus.PLANNED,
                8,
                false
        );
    }
}
