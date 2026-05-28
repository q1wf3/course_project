# Этап 9. REST API

## Базовый URL

```text
http://localhost:8080/api
```

## Endpoints

| Метод | Endpoint | Описание | Доступ |
|---|---|---|---|
| POST | `/auth/register` | Регистрация | Public |
| POST | `/auth/login` | Вход | Public |
| GET | `/movies` | Список фильмов пользователя | USER |
| GET | `/movies/{id}` | Фильм по ID | USER |
| POST | `/movies` | Создание фильма | USER |
| PUT | `/movies/{id}` | Обновление фильма | USER |
| DELETE | `/movies/{id}` | Удаление фильма | USER |
| GET | `/movies/search` | Поиск и фильтрация | USER |
| GET | `/genres` | Список жанров | USER |
| POST | `/genres` | Создание жанра | ADMIN |

## Пример запроса создания фильма

```json
{
  "title": "Blade Runner 2049",
  "releaseYear": 2017,
  "director": "Denis Villeneuve",
  "durationMinutes": 164,
  "genreIds": ["uuid-genre-id"],
  "status": "WATCHED",
  "rating": 9,
  "note": "Пересмотреть режиссерскую работу и визуальный стиль"
}
```

## Пример ответа

```json
{
  "id": "uuid-movie-id",
  "title": "Blade Runner 2049",
  "releaseYear": 2017,
  "director": "Denis Villeneuve",
  "status": "WATCHED",
  "rating": 9,
  "favorite": false
}
```

## OpenAPI

После реализации Spring Boot подключить `springdoc-openapi-starter-webmvc-ui`. Swagger UI должен быть доступен по адресу:

```text
http://localhost:8080/swagger-ui/index.html
```

