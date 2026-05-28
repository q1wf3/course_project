# Этап 2. Архитектурное проектирование

## Обоснование PCMEF

PCMEF выбран как базовый архитектурный паттерн, потому что проект содержит мобильный интерфейс, серверную бизнес-логику, предметные сущности и два уровня хранения данных: PostgreSQL на сервере и Room-кэш на клиенте. Разделение на Presentation, Control, Mediator, Entity и Foundation снижает связность и позволяет тестировать бизнес-логику отдельно от UI и инфраструктуры.

## Распределение слоев

| Слой | Мобильный клиент | Сервер |
|---|---|---|
| Presentation | Compose screens | Swagger UI |
| Control | ViewModel, UI state | REST Controllers |
| Mediator | Не содержит бизнес-правил сервера | Services |
| Entity | UI models, DTO | JPA entities |
| Foundation | Retrofit, Room DAO | Spring Data repositories |

## Диаграмма пакетов

```mermaid
flowchart TB
    subgraph Android["Mobile Client"]
        P["presentation"]
        VM["control: ViewModel"]
        API["foundation: Retrofit"]
        ROOM["foundation: Room DAO"]
        P --> VM
        VM --> API
        VM --> ROOM
    end
    subgraph Server["Spring Boot Server"]
        CTRL["control: Controllers"]
        SVC["mediator: Services"]
        ENT["entity: JPA Entities"]
        REPO["foundation: Repositories"]
        CTRL --> SVC
        SVC --> ENT
        SVC --> REPO
        REPO --> DB[("PostgreSQL")]
    end
    API --> CTRL
```

## Интерфейсы между слоями

### Control → Mediator

```java
public interface MovieService {
    MovieDto createMovie(CreateMovieCommand command, UUID userId);
    MovieDto updateMovie(UUID movieId, UpdateMovieCommand command, UUID userId);
    void deleteMovie(UUID movieId, UUID userId);
    MovieDto getMovie(UUID movieId, UUID userId);
    List<MovieDto> searchMovies(MovieSearchQuery query, UUID userId);
}
```

### Mediator → Foundation

```java
public interface MovieRepository {
    Optional<Movie> findByIdAndOwnerId(UUID movieId, UUID ownerId);
    List<Movie> search(UUID ownerId, String query, WatchStatus status);
    Movie save(Movie movie);
    void delete(Movie movie);
}
```

## ADR-001. Выбор Android Native

| Поле | Решение |
|---|---|
| Контекст | Требуется мобильное приложение с оффлайн-кэшем и Material Design |
| Решение | Использовать Kotlin, Jetpack Compose, ViewModel, StateFlow, Retrofit и Room |
| Последствия | Клиент соответствует требованиям мобильной траектории и хорошо демонстрирует обработку состояний |

## ADR-002. Выбор Spring Boot

| Поле | Решение |
|---|---|
| Контекст | Методичка требует Java + Spring Boot сервер для мобильной траектории |
| Решение | Использовать Spring Boot 3, Spring Security, Spring Data JPA |
| Последствия | REST API, JWT и работа с PostgreSQL реализуются стандартными средствами |

## Проверка зависимостей

Зависимости направлены сверху вниз:

```text
Presentation → Control → Mediator → Entity/Foundation
```

Foundation не зависит от Presentation, а Entity не зависит от UI и инфраструктурных классов.

