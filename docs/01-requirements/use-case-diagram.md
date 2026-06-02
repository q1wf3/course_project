# Диаграмма вариантов использования

```plantuml
@startuml
left to right direction
actor "Гость" as Guest
actor "USER" as User
actor "ADMIN" as Admin

usecase "Зарегистрироваться" as Register
usecase "Войти" as Login
usecase "Просмотреть коллекцию" as List
usecase "Добавить фильм" as Create
usecase "Редактировать фильм" as Edit
usecase "Удалить фильм" as Delete
usecase "Найти фильм" as Search
usecase "Настроить профиль" as Profile
usecase "Посмотреть статистику" as Stats
usecase "Управлять пользователями" as Users
usecase "Изменить роль" as Roles

Guest --> Register
Guest --> Login
User --> List
User --> Create
User --> Edit
User --> Delete
User --> Search
User --> Profile
Admin --> Stats
Admin --> Users
Admin --> Roles
@enduml
```

![Диаграмма вариантов использования](images/use-case-diagram.png)

Диаграмма фиксирует функциональные границы приложения с точки зрения трех акторов: гостя, обычного пользователя и администратора. Она показывает, что административные возможности не смешиваются с пользовательским сценарием ведения коллекции, а доступны только роли `ADMIN`.
