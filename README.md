# Movie Collection

Курсовой проект по программной инженерии: мобильное Android-приложение для ведения личной коллекции фильмов с серверной частью на Spring Boot, PostgreSQL, JWT-авторизацией и оффлайн-кэшем.

## Тема проекта

**Разработка мобильного приложения для ведения коллекции фильмов**

Приложение позволяет пользователю хранить личную фильмотеку: добавлять фильмы, указывать обложку, категорию, режиссера, статус просмотра, оценку, заметки и избранное. Для администратора предусмотрена отдельная панель управления пользователями, ролями и коллекциями.

## Стек технологий

| Часть проекта | Технологии |
|---|---|
| Android-клиент | Kotlin, Jetpack Compose, Room, Retrofit, Coil, Material UI |
| Backend | Java 17, Spring Boot, Spring Security, Spring Data JPA, REST API |
| База данных | PostgreSQL 16 |
| Документация API | Swagger / OpenAPI |
| Безопасность | JWT, BCrypt, роли `USER` и `ADMIN` |
| Развертывание | Docker Compose |
| Архитектура | PCMEF |

## Основные возможности

- регистрация и вход пользователя;
- разделение ролей `USER` и `ADMIN`;
- добавление, редактирование и удаление фильмов;
- ввод русскоязычных названий, категорий, режиссеров и заметок;
- загрузка обложек по URL;
- статусы просмотра: планирую, смотрю, просмотрено, брошено;
- оценка фильма от 1 до 10;
- поиск и фильтрация коллекции;
- профиль пользователя с персональными данными;
- темная и светлая тема в настройках;
- оффлайн-кэш коллекции через Room;
- административная панель для просмотра пользователей, ролей, статистики и коллекций;
- REST API с документацией Swagger;
- PostgreSQL как основная серверная база данных.

## Структура проекта

```text
course_project-main/
├── backend/                 # Spring Boot REST API
├── mobile/                  # Android-приложение
├── docs/                    # Документация по этапам курсового проекта
├── docker-compose.yml       # PostgreSQL, backend и pgAdmin
├── movie-collection.code-workspace
└── README.md
```

## Быстрый запуск через Docker

Запуск PostgreSQL и backend:

```bash
docker compose up -d postgres backend
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

pgAdmin запускается отдельно:

```bash
docker compose --profile tools up -d pgadmin
```

Адрес pgAdmin:

```text
http://localhost:5050
```

Данные для входа в pgAdmin:

```text
admin@admin.ru / admin
```

## Учетные записи для проверки

Администратор создается автоматически при запуске backend:

```text
admin@movie.local / admin123
```

Тестовый пользователь:

```text
test@yandex.ru / 123456
```

## Локальный запуск backend

Перед локальным запуском должен быть доступен PostgreSQL с базой `movie_collection` и пользователем `movie_user`.
При запуске PostgreSQL через Docker Compose база доступна на `127.0.0.1:5433`, чтобы не конфликтовать с локальным PostgreSQL на `5432`.

```bash
cd backend
mvn spring-boot:run
```

## Запуск Android-приложения

1. Открыть папку `mobile` в Android Studio.
2. Дождаться синхронизации Gradle.
3. Запустить модуль `app` на эмуляторе или физическом устройстве.

Для Android-эмулятора backend доступен по адресу:

```text
http://10.0.2.2:8080/api
```

## Тестирование

Backend-тесты:

```bash
cd backend
mvn test
```

JaCoCo HTML-отчет:

```bash
cd backend
mvn test jacoco:report
```

После выполнения отчет будет доступен локально в каталоге Maven-сборки:

```text
backend/target/site/jacoco/index.html
```

Каталог `backend/target` является build artifact и не добавляется в git.

Проверка компиляции Android-клиента:

```bash
cd mobile
sh gradlew :app:compileDebugKotlin
```

## Документация

Документация находится в папке [docs](docs/README.md). Каждый раздел содержит собственный `README.md` с целью этапа, таблицей артефактов, ссылками на диаграммы и контролем соответствия методическим требованиям.

| Раздел | Содержание |
|---|---|
| [00-project-charter](docs/00-project-charter/README.md) | Инициация, бизнес-анализ, глоссарий, стейкхолдеры, SWOT и ROI |
| [01-requirements](docs/01-requirements/README.md) | Требования, варианты использования, модель предметной области и трассировка |
| [02-architecture](docs/02-architecture/README.md) | Архитектура, PCMEF, зависимости, интерфейсы и ADR |
| [03-database](docs/03-database/README.md) | PostgreSQL, ER-диаграмма, DDL и ORM |
| [04-detailed-design](docs/04-detailed-design/README.md) | Проектные классы, последовательности и спецификации методов |
| [05-implementation](docs/05-implementation/README.md) | Реализация, структура кода, слои и паттерны |
| [06-testing](docs/06-testing/README.md) | План тестирования, сценарии проверок и JaCoCo |
| [07-refactoring](docs/07-refactoring/README.md) | Рефакторинг, code smells, Data Mapper и Identity Map |
| [08-ui](docs/08-ui/README.md) | Пользовательский интерфейс, навигация и экраны Android-приложения |
| [09-api](docs/09-api/README.md) | REST API, OpenAPI, Postman и ручная проверка endpoints |
| [10-deployment](docs/10-deployment/README.md) | Docker Compose, PostgreSQL, backend и pgAdmin |
| [11-guides](docs/11-guides/README.md) | Руководства пользователя, администратора и техническая спецификация |
| [12-project-management](docs/12-project-management/README.md) | WBS, диаграмма Ганта и COCOMO |
| [13-compliance](docs/13-compliance/README.md) | Чек-лист соответствия требованиям методички |

Общие изображения диаграмм для пояснительной записки лежат в [docs/images](docs/images/README.md).

## Полезные ссылки

- GitHub-репозиторий проекта: <https://github.com/q1wf3/course_project>
- Swagger UI после запуска backend: <http://localhost:8080/swagger-ui/index.html>
- pgAdmin после запуска профиля `tools`: <http://localhost:5050>
