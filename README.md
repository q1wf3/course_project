# Разработка мобильного приложения для ведения коллекции фильмов

Курсовой проект по программной инженерии. Приложение позволяет вести личную коллекцию фильмов: добавлять фильмы, указывать обложку, категорию, статус просмотра, оценку и группировать коллекцию по категориям.

## Тема

**Разработка мобильного приложения для ведения коллекции фильмов**

## Стек

- **Mobile:** Kotlin, Android, Jetpack Compose, Room, Retrofit, Coil
- **Backend:** Java 17, Spring Boot, Spring Data JPA, REST API, Swagger/OpenAPI
- **Database:** H2 для быстрого запуска, PostgreSQL через Docker при необходимости
- **Architecture:** PCMEF

## Структура проекта

```text
course_project/
├── backend/                 # Spring Boot REST API
├── mobile/                  # Android-приложение
├── docs/                    # Документация по методичке
├── docker-compose.yml       # PostgreSQL и pgAdmin
├── run-dev.bat              # запуск backend
├── run-postgres.bat         # запуск PostgreSQL
└── README.md
```

## Основные функции

- регистрация и вход пользователя;
- добавление, редактирование и удаление фильмов;
- загрузка обложки фильма по URL;
- оценка фильма от 1 до 10;
- выбор статуса просмотра: планирую, смотрю, просмотрено, брошено;
- группировка фильмов по русскоязычным категориям;
- локальный кэш коллекции на устройстве;
- REST API с документацией Swagger.

## Запуск

Backend:

```bat
.\run-dev.bat
```

Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```

Mobile:

```text
Открыть папку mobile в Android Studio и запустить app на эмуляторе.
```

## Документация

Документация находится в папке `docs/`:

- `00-project-charter` — паспорт проекта;
- `01-requirements` — требования;
- `02-architecture` — архитектура PCMEF;
- `03-database` — модель данных;
- `04-detailed-design` — детальное проектирование;
- `05-implementation` — реализация;
- `06-testing` — тестирование;
- `07-refactoring` — рефакторинг;
- `08-ui` — интерфейс;
- `09-api` — REST API;
- `10-deployment` — развертывание;
- `11-user-guide` — руководство пользователя;
- `12-final-report` — итоговый отчет.
