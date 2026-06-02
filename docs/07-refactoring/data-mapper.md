# Data Mapper

В backend используется `MovieMapper`, который преобразует `CollectionItem` в `MovieDto`.

```java
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
```

Польза паттерна:

- контроллеры не знают структуру JPA-сущностей;
- клиент получает только нужные поля;
- преобразование сосредоточено в одном месте.

