package ru.skfu.moviecollection.mediator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skfu.moviecollection.control.dto.CreateMovieCommand;
import ru.skfu.moviecollection.entity.CollectionItem;
import ru.skfu.moviecollection.entity.Movie;
import ru.skfu.moviecollection.entity.User;
import ru.skfu.moviecollection.entity.WatchStatus;
import ru.skfu.moviecollection.foundation.CollectionItemRepository;
import ru.skfu.moviecollection.foundation.MovieMapper;
import ru.skfu.moviecollection.foundation.MovieRepository;
import ru.skfu.moviecollection.foundation.UserRepository;

@ExtendWith(MockitoExtension.class)
class MovieServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private CollectionItemRepository collectionItemRepository;

    private final MovieMapper movieMapper = new MovieMapper();

    @InjectMocks
    private MovieServiceImpl movieService;

    @Test
    void createMovieSavesMovieAndCollectionItem() {
        movieService = new MovieServiceImpl(userRepository, movieRepository, collectionItemRepository, movieMapper);
        var user = new User("user@example.com", "hash");
        var command = new CreateMovieCommand(
                "Interstellar",
                2014,
                "Christopher Nolan",
                169,
                "Sci-fi drama",
                null,
                "Фантастика",
                WatchStatus.WATCHED,
                10,
                "Favorite"
        );
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(collectionItemRepository.save(any(CollectionItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = movieService.createMovie(user.getId(), command);

        assertEquals("Interstellar", result.title());
        assertEquals(WatchStatus.WATCHED, result.status());
        assertEquals(10, result.rating());
        verify(movieRepository).save(any(Movie.class));
        verify(collectionItemRepository).save(any(CollectionItem.class));
    }

    @Test
    void getMovieReturnsOnlyMovieFromUserCollection() {
        movieService = new MovieServiceImpl(userRepository, movieRepository, collectionItemRepository, movieMapper);
        var user = new User("user@example.com", "hash");
        var movie = new Movie("Arrival", 2016, "Denis Villeneuve", 116, null, null, "Фантастика");
        var item = new CollectionItem(user, movie, WatchStatus.PLANNED);
        when(collectionItemRepository.findByOwnerIdAndMovieId(user.getId(), movie.getId())).thenReturn(Optional.of(item));

        var result = movieService.getMovie(user.getId(), movie.getId());

        assertEquals(movie.getId(), result.id());
        assertEquals("Arrival", result.title());
    }
}
