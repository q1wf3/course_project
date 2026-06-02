# Проверка API

## Swagger UI

После запуска backend Swagger доступен по адресу:

```text
http://localhost:8080/swagger-ui/index.html
```

## Проверка входа

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@movie.local","password":"admin123"}'
```

## Проверка списка фильмов

```bash
curl http://localhost:8080/api/movies \
  -H "Authorization: Bearer <JWT>"
```

## Проверка статистики администратора

```bash
curl http://localhost:8080/api/admin/stats \
  -H "Authorization: Bearer <ADMIN_JWT>"
```

