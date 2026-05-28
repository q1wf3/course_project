# Этап 4. Детальное проектирование

## Диаграмма последовательности: добавление фильма

```mermaid
sequenceDiagram
    actor User
    participant UI as Compose Screen
    participant VM as MovieViewModel
    participant API as Retrofit API
    participant C as MovieController
    participant S as MovieService
    participant R as MovieRepository
    participant DB as PostgreSQL
    User->>UI: Заполняет форму
    UI->>VM: saveMovie(command)
    VM->>API: POST /api/movies
    API->>C: HTTP request
    C->>S: createMovie(command, userId)
    S->>R: save(movie)
    R->>DB: INSERT
    DB-->>R: saved row
    R-->>S: Movie
    S-->>C: MovieDto
    C-->>API: 201 Created
    API-->>VM: MovieDto
    VM-->>UI: Success state
```

## Диаграмма последовательности: поиск

```mermaid
sequenceDiagram
    actor User
    participant UI
    participant VM
    participant Cache as Room
    participant API
    User->>UI: Вводит поисковую строку
    UI->>VM: search(query)
    VM->>API: GET /api/movies/search
    alt сеть доступна
        API-->>VM: список фильмов
        VM->>Cache: обновить кэш
    else сеть недоступна
        VM->>Cache: поиск в локальном кэше
        Cache-->>VM: кэшированные фильмы
    end
    VM-->>UI: состояние результата
```

## Ключевые классы

| Класс | Слой | Назначение |
|---|---|---|
| MovieListScreen | Presentation | Отображает список фильмов |
| MovieViewModel | Control | Управляет состоянием UI |
| MovieController | Control | REST API для фильмов |
| MovieServiceImpl | Mediator | Бизнес-логика фильмов |
| Movie | Entity | Сущность фильма |
| CollectionItem | Entity | Запись коллекции пользователя |
| MovieRepository | Foundation | Доступ к фильмам |
| MovieMapper | Foundation/Mediator boundary | Преобразует Entity в DTO |

## Спецификация методов

| Метод | Назначение | Ошибки |
|---|---|---|
| `createMovie(command, userId)` | Создает фильм и добавляет его в коллекцию | 400 при ошибке валидации |
| `updateMovie(movieId, command, userId)` | Обновляет карточку фильма пользователя | 404 если фильм не найден |
| `searchMovies(query, userId)` | Ищет фильмы по строке и фильтрам | Возвращает пустой список |
| `changeStatus(movieId, status, userId)` | Меняет статус просмотра | 403 при чужой записи |

