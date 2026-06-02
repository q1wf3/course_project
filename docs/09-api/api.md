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
| GET | `/movies/{movieId}` | Фильм по ID | USER |
| POST | `/movies` | Создание фильма | USER |
| GET | `/movies/search` | Поиск и фильтрация | USER |
| PUT | `/movies/{movieId}` | Обновление фильма | USER |
| PUT | `/movies/{movieId}/status` | Изменение статуса просмотра | USER |
| DELETE | `/movies/{movieId}` | Удаление фильма | USER |
| GET | `/admin/stats` | Общая статистика пользователей и коллекций | ADMIN |
| GET | `/admin/users` | Список учетных записей | ADMIN |
| GET | `/admin/users/{userId}/movies` | Коллекция выбранного пользователя | ADMIN |
| PUT | `/admin/users/{userId}/role?role=ADMIN` | Изменение роли пользователя | ADMIN |
| DELETE | `/admin/users/{userId}` | Удаление учетной записи | ADMIN |

Все защищенные endpoints принимают заголовок:

```http
Authorization: Bearer <JWT>
```

Токен формируется backend после регистрации или входа. Payload содержит `sub`, `email`, `role`, `iat`, `exp`, подпись выполняется алгоритмом HS256.

Начальный администратор создается при старте backend в PostgreSQL:

```text
admin@movie.local / admin123
```

## Пример запроса создания фильма

```json
{
  "title": "Blade Runner 2049",
  "releaseYear": 2017,
  "director": "Denis Villeneuve",
  "durationMinutes": 164,
  "description": "Визуальная научная фантастика",
  "coverUrl": "https://example.com/blade-runner-2049.jpg",
  "category": "Фантастика",
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
  "coverUrl": "https://example.com/blade-runner-2049.jpg",
  "category": "Фантастика",
  "status": "WATCHED",
  "rating": 9,
  "favorite": false
}
```

## OpenAPI

Swagger UI доступен по адресу:

```text
http://localhost:8080/swagger-ui/index.html
```
