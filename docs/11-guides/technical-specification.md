# Техническая спецификация

## Назначение

Movie Collection предназначено для ведения личной коллекции фильмов в Android-приложении с серверным хранением данных и оффлайн-кэшем.

## Технологии

| Часть | Технологии |
| --- | --- |
| Mobile | Kotlin, Android, Jetpack Compose, Room, Retrofit, Coil |
| Backend | Java 17, Spring Boot, Spring Security, Spring Data JPA |
| Database | PostgreSQL 16 |
| Infrastructure | Docker Compose |
| Testing | JUnit, Mockito, JaCoCo |

## Минимальные сценарии

- регистрация;
- вход;
- добавление фильма;
- редактирование фильма;
- удаление фильма;
- поиск;
- оффлайн-просмотр;
- просмотр админки.

## Нефункциональные требования

- пароль хранится как BCrypt-хэш;
- protected endpoints требуют JWT;
- данные сохраняются в PostgreSQL volume;
- Android-клиент поддерживает русскоязычный ввод;
- покрытие backend-тестами выше 40%.

