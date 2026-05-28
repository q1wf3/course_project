# Этап 10. Развертывание

## Требования к окружению

| Компонент | Версия |
|---|---|
| Java JDK | 17+ |
| Maven | 3.8+ |
| PostgreSQL | 15+ |
| Android Studio | актуальная стабильная версия |
| Android SDK | API 34+ |

## Настройка PostgreSQL

```sql
CREATE DATABASE movie_collection;
CREATE USER movie_user WITH PASSWORD 'movie_password';
GRANT ALL PRIVILEGES ON DATABASE movie_collection TO movie_user;
```

## Запуск backend

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
gradle assembleDebug
```

Итоговый APK находится в:

```text
mobile/app/build/outputs/apk/debug/
```
