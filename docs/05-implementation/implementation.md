# Этап 5. Реализация ядра

## Реализация PCMEF

| Слой | Что реализуется |
|---|---|
| Presentation | Compose-экраны: вход, список, детали, редактирование, настройки |
| Control | ViewModel на клиенте, REST controllers на сервере |
| Mediator | `MovieService`, `AuthService`, `JwtService` |
| Entity | `User`, `Role`, `Movie`, `Genre`, `CollectionItem`, `WatchStatus` |
| Foundation | Spring Data repositories, Room DAO, Retrofit API |

## Бизнес-правила

- Один пользователь не может добавить один и тот же фильм в коллекцию дважды.
- Оценка фильма допускается только в диапазоне от 1 до 10.
- Статус просмотра принимает только значения `PLANNED`, `WATCHING`, `WATCHED`, `DROPPED`.
- Удаление фильма из коллекции не удаляет глобальную карточку фильма.
- Доступ к коллекции имеет только владелец и администратор.

## Пример структуры backend

```text
backend/src/main/java/ru/skfu/moviecollection/
├── control/MovieController.java
├── control/AuthController.java
├── mediator/MovieService.java
├── mediator/MovieServiceImpl.java
├── entity/Movie.java
├── entity/CollectionItem.java
├── foundation/MovieRepository.java
└── foundation/MovieMapper.java
```

## Пример структуры mobile

```text
mobile/app/src/main/java/ru/skfu/moviecollection/
├── presentation/MovieListScreen.kt
├── presentation/MovieDetailsScreen.kt
├── control/MovieViewModel.kt
├── api_client/MovieApi.kt
├── local_cache/MovieDao.kt
└── model/MovieDto.kt
```

## Требования к тестам

Минимально тестируются:

- создание фильма;
- получение фильма из коллекции пользователя;
- изменение статуса просмотра;
- генерация и проверка JWT;
- поиск по названию;
- маппинг Entity → DTO.
