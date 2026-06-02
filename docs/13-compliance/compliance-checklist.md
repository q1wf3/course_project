# Проверка соответствия методическим требованиям

Проект выбран по мобильной траектории: Android Native + Java Spring Boot backend.

## Обязательные требования мобильной траектории

| Требование | Статус | Где реализовано |
|---|---|---|
| Мобильное приложение с 5+ экранами | Выполнено | `mobile/app/src/main/java/.../presentation`: Login, List, Details, Edit, Settings |
| Kotlin + Jetpack Compose | Выполнено | `mobile/app/build.gradle.kts`, `presentation/*Screen.kt` |
| ViewModel + StateFlow | Выполнено | `MovieViewModel.kt`, `MovieUiState.kt` |
| Retrofit + OkHttp | Выполнено | `api_client/ApiClient.kt`, `MovieApi.kt`, `AuthApi.kt` |
| Room-кэш и оффлайн-режим | Выполнено | `local_cache/AppDatabase.kt`, `MovieDao.kt`, `CachedMovie.kt` |
| Java 17 + Spring Boot backend | Выполнено | `backend/pom.xml`, `MovieCollectionApplication.java` |
| REST API 8+ endpoints | Выполнено | `AuthController.java`, `MovieController.java` |
| OpenAPI/Swagger | Выполнено | `springdoc-openapi-starter-webmvc-ui`, `/swagger-ui/index.html` |
| PostgreSQL | Выполнено | основной `application.yml`, `application-postgres.yml`, `docker-compose.yml`; H2 не используется в основном запуске |
| JWT-аутентификация | Выполнено | `JwtService.java`, `AuthServiceImpl.java`, `MovieController.java` |
| BCrypt-хеширование паролей | Выполнено | `SecurityConfig.java`, `AuthServiceImpl.java` |
| Роли USER/ADMIN | Выполнено | `Role.java`, JWT содержит `role`, `AdminController.java`, Android-экран `AdminScreen.kt` |
| Docker + docker-compose | Выполнено | `backend/Dockerfile`, `docker-compose.yml` |
| Модульное тестирование backend >40% | Выполнено | `mvn test jacoco:report`: 10 тестов, 0 ошибок; покрытие строк 44.07%, instructions 42.53% |

## Документация по этапам

| Этап методички | Статус | Файл |
|---|---|---|
| Паспорт проекта | Выполнено | `docs/00-project-charter/project-charter.md` |
| Требования | Выполнено | `docs/01-requirements/requirements.md` |
| Архитектура PCMEF | Выполнено | `docs/02-architecture/architecture.md` |
| Проектирование БД | Выполнено | `docs/03-database/database.md` |
| Детальное проектирование | Выполнено | `docs/04-detailed-design/detailed-design.md` |
| Реализация | Выполнено | `docs/05-implementation/implementation.md` |
| Тестирование | Выполнено | `docs/06-testing/testing.md`; перед сдачей приложить скриншот JaCoCo-отчета |
| Рефакторинг | Выполнено | `docs/07-refactoring/refactoring.md` |
| UI | Выполнено | `docs/08-ui/ui.md` |
| REST API | Выполнено | `docs/09-api/api.md` |
| Развертывание | Выполнено | `docs/10-deployment/deployment.md` |
| Руководство пользователя | Выполнено | `docs/11-user-guide/user-guide.md` |
| Итоговый отчет | Выполнено | `docs/12-final-report/final-report.md` |

## Что нужно приложить перед сдачей

1. Скриншот Swagger UI: `docs/images/swagger-ui.png`.
2. Скриншоты Android-экранов: `docs/images/mobile-*.png`.
3. Скриншот JaCoCo-покрытия: `docs/images/test-coverage.png`.
4. Скриншоты GitHub Insights:
   - `docs/images/git-commit-activity.png`;
   - `docs/images/git-punch-card.png`.
5. Проверить, что в GitHub-репозитории есть история коммитов, а не только один финальный commit.

## Основные остаточные риски

1. В локальной папке отсутствует `.git`, поэтому Git-статистика должна проверяться в GitHub-репозитории.
2. Админка реализована через protected endpoints `/api/admin/**`; перед сдачей стоит сделать скрин Swagger с этими endpoints.
3. Отчет JaCoCo уже сформирован локально в `backend/target/site/jacoco/`; перед сдачей нужно приложить скриншот `index.html` в `docs/images/test-coverage.png`.
