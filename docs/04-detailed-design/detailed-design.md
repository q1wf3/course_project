# Этап 4. Детальное проектирование

## Диаграмма последовательности: добавление фильма

```plantuml
@startuml
actor User
participant "Compose Screen" as UI
participant "MovieViewModel" as VM
participant "Retrofit API" as API
participant "MovieController" as C
participant "MovieService" as S
participant "MovieRepository" as R
database "PostgreSQL" as DB

User -> UI : Заполняет форму
UI -> VM : saveMovie(command)
VM -> API : POST /api/movies
API -> C : HTTP request
C -> S : createMovie(command, userId)
S -> R : save(movie)
R -> DB : INSERT
DB --> R : saved row
R --> S : Movie
S --> C : MovieDto
C --> API : 201 Created
API --> VM : MovieDto
VM --> UI : Success state
@enduml
```

![Диаграмма последовательности добавления фильма](images/create-film.png)

Диаграмма показывает, что создание фильма проходит через Android UI, ViewModel, Retrofit API и backend-контроллер. Сохранение выполняется на сервере: создается карточка фильма и запись коллекции конкретного пользователя.

## Диаграмма последовательности: поиск

```plantuml
@startuml
actor User
participant UI
participant VM
database "Room" as Cache
participant API

User -> UI : Вводит поисковую строку
UI -> VM : search(query)
VM -> API : GET /api/movies/search
alt сеть доступна
    API --> VM : список фильмов
    VM -> Cache : обновить кэш
else сеть недоступна
    VM -> Cache : поиск в локальном кэше
    Cache --> VM : кэшированные фильмы
end
VM --> UI : состояние результата
@enduml
```

![Диаграмма последовательности оффлайн-поиска](../images/report-offline-cache-sequence.png)

Сценарий поиска учитывает два режима работы. При доступном backend данные берутся с сервера и обновляют Room-кэш, а при отсутствии сети приложение читает сохраненные фильмы локально.

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
