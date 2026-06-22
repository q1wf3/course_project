# Backend

Spring Boot REST API для мобильного приложения коллекции фильмов.

## Запуск

Локально:

Перед запуском нужен PostgreSQL:

```text
jdbc:postgresql://127.0.0.1:5433/movie_collection
movie_user / movie_password
```

```bash
mvn spring-boot:run
```

Через Docker Compose из корня проекта:

```bash
docker compose up -d postgres backend
```

Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```

## Основные endpoints

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/movies`
- `GET /api/movies/{movieId}`
- `GET /api/movies/search`
- `POST /api/movies`
- `PUT /api/movies/{movieId}`
- `PUT /api/movies/{movieId}/status`
- `DELETE /api/movies/{movieId}`
- `GET /api/admin/stats`
- `GET /api/admin/users`
- `GET /api/admin/users/{userId}/movies`
- `PUT /api/admin/users/{userId}/role`
- `DELETE /api/admin/users/{userId}`

Админские endpoints требуют JWT пользователя с ролью `ADMIN`.
Начальный администратор создается автоматически:

```text
admin@movie.local / admin123
```

## PCMEF

- `control` — REST-контроллеры и DTO;
- `mediator` — сервисы и бизнес-логика;
- `entity` — доменные сущности;
- `foundation` — репозитории и мапперы;
- `config` — конфигурация приложения.
