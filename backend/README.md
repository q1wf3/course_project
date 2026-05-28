# Backend

Spring Boot REST API для мобильного приложения коллекции фильмов.

## Запуск

```bat
.\run-dev.bat
```

Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```

## Основные endpoints

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/movies`
- `GET /api/movies/search`
- `POST /api/movies`
- `PUT /api/movies/{movieId}`
- `PUT /api/movies/{movieId}/status`
- `DELETE /api/movies/{movieId}`

## PCMEF

- `control` — REST-контроллеры и DTO;
- `mediator` — сервисы и бизнес-логика;
- `entity` — доменные сущности;
- `foundation` — репозитории и мапперы;
- `config` — конфигурация приложения.
