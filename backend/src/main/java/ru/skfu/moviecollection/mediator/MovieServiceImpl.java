package ru.skfu.moviecollection.mediator;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skfu.moviecollection.control.dto.CreateMovieCommand;
import ru.skfu.moviecollection.control.dto.MovieDto;
import ru.skfu.moviecollection.entity.CollectionItem;
import ru.skfu.moviecollection.entity.Movie;
import ru.skfu.moviecollection.entity.WatchStatus;
import ru.skfu.moviecollection.foundation.CollectionItemRepository;
import ru.skfu.moviecollection.foundation.MovieMapper;
import ru.skfu.moviecollection.foundation.MovieRepository;
import ru.skfu.moviecollection.foundation.UserRepository;

@Service
@Transactional
public class MovieServiceImpl implements MovieService {
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final CollectionItemRepository collectionItemRepository;
    private final MovieMapper movieMapper;

    public MovieServiceImpl(
            UserRepository userRepository,
            MovieRepository movieRepository,
            CollectionItemRepository collectionItemRepository,
            MovieMapper movieMapper
    ) {
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.collectionItemRepository = collectionItemRepository;
        this.movieMapper = movieMapper;
    }

    @Override
    public MovieDto createMovie(UUID userId, CreateMovieCommand command) {
        var owner = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        var movie = new Movie(
                command.title(),
                command.releaseYear(),
                command.director(),
                command.durationMinutes(),
                command.description(),
                command.coverUrl(),
                command.category()
        );
        movieRepository.save(movie);
        var item = new CollectionItem(owner, movie, command.status());
        applyCollectionFields(item, command);
        return movieMapper.toDto(collectionItemRepository.save(item));
    }

    @Override
    public MovieDto updateMovie(UUID userId, UUID movieId, CreateMovieCommand command) {
        var item = collectionItemRepository.findByOwnerIdAndMovieId(userId, movieId)
                .orElseThrow(() -> new IllegalArgumentException("Фильм не найден в коллекции"));
        item.getMovie().updateDetails(
                command.title(),
                command.releaseYear(),
                command.director(),
                command.durationMinutes(),
                command.description(),
                command.coverUrl(),
                command.category()
        );
        movieRepository.save(item.getMovie());
        applyCollectionFields(item, command);
        return movieMapper.toDto(item);
    }

    @Override
    @Transactional(readOnly = true)
    public MovieDto getMovie(UUID userId, UUID movieId) {
        var item = collectionItemRepository.findByOwnerIdAndMovieId(userId, movieId)
                .orElseThrow(() -> new IllegalArgumentException("Фильм не найден в коллекции"));
        return movieMapper.toDto(item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieDto> getCollection(UUID userId) {
        return collectionItemRepository.findByOwnerIdOrderByMovieTitle(userId)
                .stream()
                .map(movieMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieDto> search(UUID userId, String query, WatchStatus status) {
        var source = status == null
                ? collectionItemRepository.findByOwnerIdOrderByMovieTitle(userId)
                : collectionItemRepository.findByOwnerIdAndStatusOrderByMovieTitle(userId, status);
        var normalizedQuery = query == null ? "" : query.toLowerCase();
        return source.stream()
                .filter(item -> item.getMovie().getTitle().toLowerCase().contains(normalizedQuery))
                .map(movieMapper::toDto)
                .toList();
    }

    @Override
    public MovieDto changeStatus(UUID userId, UUID movieId, WatchStatus status) {
        var item = collectionItemRepository.findByOwnerIdAndMovieId(userId, movieId)
                .orElseThrow(() -> new IllegalArgumentException("Фильм не найден в коллекции"));
        item.changeStatus(status);
        return movieMapper.toDto(item);
    }

    @Override
    public void deleteFromCollection(UUID userId, UUID movieId) {
        var item = collectionItemRepository.findByOwnerIdAndMovieId(userId, movieId)
                .orElseThrow(() -> new IllegalArgumentException("Фильм не найден в коллекции"));
        collectionItemRepository.delete(item);
    }

    private void applyCollectionFields(CollectionItem item, CreateMovieCommand command) {
        item.changeStatus(command.status());
        if (command.rating() == null) {
            item.clearRating();
        } else {
            item.rate(command.rating());
        }
        item.updateNote(command.note());
    }
}
