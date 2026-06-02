# ORM (Object-Relational Mapping)

Проект использует Spring Data JPA и Hibernate для отображения Java-классов backend на таблицы PostgreSQL.

## JPA-аннотации для ключевых сущностей

### User

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    private UUID id = UUID.randomUUID();

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;
}
```

### Movie

```java
@Entity
@Table(name = "movies")
public class Movie {
    @Id
    private UUID id = UUID.randomUUID();

    @Column(nullable = false)
    private String title;

    @Column(name = "release_year")
    private int releaseYear;

    private String director;
    private Integer durationMinutes;
    private String description;
    private String coverUrl;
    private String category;
}
```

### CollectionItem

```java
@Entity
@Table(name = "collection_items")
public class CollectionItem {
    @Id
    private UUID id = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WatchStatus status;

    private Integer rating;
    private String note;
    private boolean favorite;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}
```

## Репозитории

```java
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
}

public interface MovieRepository extends JpaRepository<Movie, UUID> {
    Optional<Movie> findByTitleIgnoreCaseAndReleaseYear(String title, int releaseYear);
}

public interface CollectionItemRepository extends JpaRepository<CollectionItem, UUID> {
    List<CollectionItem> findByOwnerIdOrderByMovieTitle(UUID ownerId);
    Optional<CollectionItem> findByOwnerIdAndMovieId(UUID ownerId, UUID movieId);
    long countByOwnerId(UUID ownerId);
    void deleteByOwnerId(UUID ownerId);
}
```

## Перечисления

```java
public enum Role {
    USER,
    ADMIN
}

public enum WatchStatus {
    PLANNED,
    WATCHING,
    WATCHED,
    DROPPED
}
```

ORM-слой нужен для того, чтобы сервисы работали с предметными объектами, а не с ручными SQL-запросами. При этом фактическое хранилище остается PostgreSQL.

