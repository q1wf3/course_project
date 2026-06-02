# Паттерны проектирования

| Паттерн | Где используется | Назначение |
| --- | --- | --- |
| Repository | `UserRepository`, `MovieRepository`, `CollectionItemRepository` | Изоляция доступа к данным. |
| Data Mapper | `MovieMapper` | Преобразование JPA-сущности в DTO. |
| DTO | `MovieDto`, `AuthResponse`, `AdminUserDto` | Передача данных между backend и mobile. |
| Service Layer | `AuthServiceImpl`, `MovieServiceImpl` | Размещение бизнес-логики вне контроллеров. |
| MVVM | `MovieViewModel` + Compose screens | Управление состоянием Android UI. |
| Dependency Injection | Spring beans, Android ViewModel | Передача зависимостей без ручного создания по всему коду. |

