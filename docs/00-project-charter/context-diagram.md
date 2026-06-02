# Контекстная диаграмма

Контекстная диаграмма показывает границы системы Movie Collection и внешние сущности, с которыми она взаимодействует.

```plantuml
@startuml
actor "Пользователь USER" as User
actor "Администратор ADMIN" as Admin
component "Android-приложение
Movie Collection" as Android
component "Spring Boot
REST API" as Backend
database "PostgreSQL" as DB
database "Room-кэш" as Cache
component "Swagger UI" as Swagger
component "Docker Desktop" as Docker

User --> Android
Admin --> Android
Android --> Backend
Android --> Cache
Backend --> DB
Swagger --> Backend
Docker --> Backend
Docker --> DB
@enduml
```

![Контекстная диаграмма](images/context-diagram.png)

Основная граница системы включает Android-клиент, REST API, PostgreSQL и локальный Room-кэш. Android Studio, Docker Desktop, браузер и эмулятор являются средой исполнения и демонстрации.
На диаграмме отдельно показаны пользовательские роли и инфраструктурные компоненты. Это помогает отличить собственно разрабатываемую систему от внешней среды, которая используется для запуска, проверки и демонстрации проекта.
