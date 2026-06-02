# Трассировка требований

| ID | Требование | Реализация | Проверка |
| --- | --- | --- | --- |
| FR-01 | Регистрация пользователя | `AuthController`, `AuthServiceImpl`, `LoginScreen` | `AuthControllerTest`, ручная проверка |
| FR-02 | Вход и получение JWT | `JwtService`, `AuthApi`, `ApiClient` | `JwtServiceTest`, Swagger |
| FR-03 | Просмотр коллекции | `MovieController.list`, `MovieViewModel`, `MovieListScreen` | `MovieControllerTest`, Android |
| FR-04 | Создание фильма | `MovieServiceImpl.createMovie`, `MovieEditScreen` | `MovieServiceImplTest` |
| FR-05 | Поиск | `MovieController.search`, `MovieSearchScreen` | `MovieControllerTest`, ручная проверка |
| FR-06 | Админка | `AdminController`, `AdminScreen` | `AdminControllerTest` |
| NFR-01 | PostgreSQL как основная БД | `application-postgres.yml`, `docker-compose.yml` | Docker Compose |
| NFR-02 | Оффлайн-кэш | `AppDatabase`, `MovieDao`, `CachedMovie` | Запуск приложения без backend |
| NFR-03 | Покрытие тестами выше 40% | JaCoCo report | `mvn test jacoco:report` |

