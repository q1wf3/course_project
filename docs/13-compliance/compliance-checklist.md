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
| Модульное тестирование backend >40% | Выполнено | `mvn test jacoco:report`: 26 тестов, 0 ошибок; покрытие строк 60.05%, instructions 53.22%, branches 50.00% |

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
4. Графики Git-статистики уже подготовлены:
   - `docs/images/git-commit-activity.png`;
   - `docs/images/git-punch-card.png`.
5. Проверить, что перед финальной сдачей последние изменения запушены в GitHub-репозиторий.

## Основные остаточные риски

1. Админка реализована через protected endpoints `/api/admin/**`; перед сдачей стоит сделать скрин Swagger с этими endpoints.
2. Отчет JaCoCo формируется локально в каталоге сборки `backend/target`; перед сдачей нужно приложить скриншот HTML-отчета в `docs/images/test-coverage.png`.
3. Если после правок появятся новые коммиты, графики Git-статистики в `docs/images` стоит обновить перед финальной отправкой.
