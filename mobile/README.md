# Mobile

Android-клиент для ведения коллекции фильмов.

## Запуск

Открыть папку `mobile` в Android Studio, дождаться Gradle Sync и запустить конфигурацию `app`.

Backend для эмулятора доступен по адресу:

```text
http://10.0.2.2:8080/api/
```

## PCMEF

- `presentation` — Compose-экраны;
- `control` — ViewModel и состояние интерфейса;
- `api_client` — Retrofit-клиент;
- `local_cache` — Room-кэш;
- `model` — DTO и модели интерфейса.
