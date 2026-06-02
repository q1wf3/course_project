# Диаграмма проектных классов

```plantuml
@startuml
class MovieController {
    +list(authorization)
    +getById(authorization, movieId)
    +search(authorization, query, status)
    +create(authorization, command)
    +update(authorization, movieId, command)
    +delete(authorization, movieId)
}

interface MovieService {
    +createMovie(userId, command)
    +updateMovie(userId, movieId, command)
    +getCollection(userId)
    +search(userId, query, status)
}

class MovieServiceImpl
interface MovieRepository
interface CollectionItemRepository
class MovieMapper
class JwtService

MovieController --> MovieService
MovieController --> JwtService
MovieService <|.. MovieServiceImpl
MovieServiceImpl --> MovieRepository
MovieServiceImpl --> CollectionItemRepository
MovieServiceImpl --> MovieMapper
@enduml
```

![Диаграмма проектных классов](images/design-classes-diagram.png)

Диаграмма отражает основной backend-сценарий работы с фильмами. REST-контроллер принимает запрос, извлекает пользователя из JWT и передает выполнение сервису.
Класс `MovieServiceImpl` является центром бизнес-логики: он проверяет принадлежность фильма пользователю, обновляет сущности и возвращает DTO через `MovieMapper`. Репозитории остаются инфраструктурным слоем и не используются напрямую из контроллера.
