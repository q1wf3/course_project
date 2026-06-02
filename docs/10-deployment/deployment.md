# Этап 10. Развертывание

## Требования к окружению

| Компонент | Версия |
|---|---|
| Java JDK | 17+ |
| Maven | 3.8+ |
| PostgreSQL | 15+ |
| Docker Desktop | актуальная стабильная версия |
| Android Studio | актуальная стабильная версия |
| Android SDK | API 34+ |

## Настройка PostgreSQL

```sql
CREATE DATABASE movie_collection;
CREATE USER movie_user WITH PASSWORD 'movie_password';
GRANT ALL PRIVILEGES ON DATABASE movie_collection TO movie_user;
```

## Запуск backend

### Быстрый запуск через Docker Compose

```bash
docker compose up -d postgres backend
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

Backend работает с PostgreSQL из compose-сервиса `postgres`. Начальный администратор создается автоматически:

```text
admin@movie.local / admin123
```

pgAdmin запускается только при необходимости:

```bash
docker compose --profile tools up -d pgadmin
```

Доступ к pgAdmin:

```text
http://localhost:5050
admin@admin.ru / admin
```

### Локальный запуск через Maven

Локальный запуск также использует PostgreSQL, а не H2. Перед стартом должна быть создана БД из раздела настройки PostgreSQL.

```bash
cd backend
mvn spring-boot:run
```

Ожидаемый адрес сервера:

```text
http://localhost:8080
```

## Запуск Android-клиента

1. Открыть папку `mobile/` в Android Studio.
2. В `local.properties` проверить путь к Android SDK.
3. В настройках клиента указать базовый URL backend:

```text
http://10.0.2.2:8080/api
```

4. Запустить приложение на эмуляторе или устройстве.

## Сборка APK

```bash
cd mobile
./gradlew assembleDebug
```

Итоговый APK находится в:

```text
mobile/app/build/outputs/apk/debug/
```
