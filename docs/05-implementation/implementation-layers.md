# Реализация слоев

## Android

| Слой | Компоненты |
| --- | --- |
| Presentation | `LoginScreen`, `MovieListScreen`, `MovieEditScreen`, `MovieSearchScreen`, `ProfileScreen`, `SettingsScreen`, `AdminScreen`. |
| Control | `MovieViewModel`, `MovieUiState`. |
| Foundation | `ApiClient`, `AuthApi`, `MovieApi`, `AdminApi`, `AppDatabase`, `MovieDao`. |

## Backend

| Слой | Компоненты |
| --- | --- |
| Control | `AuthController`, `MovieController`, `AdminController`, DTO. |
| Mediator | `AuthServiceImpl`, `MovieServiceImpl`, `JwtService`. |
| Entity | `User`, `Movie`, `CollectionItem`, `Genre`, `Role`, `WatchStatus`. |
| Foundation | JPA-репозитории и `MovieMapper`. |

Реализация сохраняет правило: UI не обращается напрямую к PostgreSQL, а backend-контроллеры не выполняют бизнес-правила самостоятельно.

