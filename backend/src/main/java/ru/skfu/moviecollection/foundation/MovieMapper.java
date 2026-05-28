package ru.skfu.moviecollection.foundation;

import org.springframework.stereotype.Component;
import ru.skfu.moviecollection.control.dto.MovieDto;
import ru.skfu.moviecollection.entity.CollectionItem;

@Component
public class MovieMapper {
    public MovieDto toDto(CollectionItem item) {
        var movie = item.getMovie();
        return new MovieDto(
                movie.getId(),
                movie.getTitle(),
                movie.getReleaseYear(),
                movie.getDirector(),
                movie.getCoverUrl(),
                movie.getCategory(),
                item.getStatus(),
                item.getRating(),
                item.isFavorite()
        );
    }
}

