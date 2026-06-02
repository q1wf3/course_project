# Интерфейсы системы

## REST API

| Интерфейс | Назначение |
| --- | --- |
| `AuthApi` | Регистрация и вход пользователя. |
| `MovieApi` | CRUD, поиск и изменение статуса фильмов. |
| `AdminApi` | Статистика, пользователи, роли и коллекции. |

## Backend interfaces

| Интерфейс | Реализация |
| --- | --- |
| `AuthService` | `AuthServiceImpl` |
| `MovieService` | `MovieServiceImpl` |
| `UserRepository` | Spring Data JPA |
| `MovieRepository` | Spring Data JPA |
| `CollectionItemRepository` | Spring Data JPA |

## Android local interfaces

| Компонент | Назначение |
| --- | --- |
| `MovieDao` | Чтение и запись локального кэша фильмов. |
| `AppDatabase` | Конфигурация Room. |
| `ApiClient` | Создание Retrofit-клиентов и передача JWT. |

