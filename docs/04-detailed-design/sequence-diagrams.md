# Диаграммы последовательностей

## Создание фильма

```plantuml
@startuml
actor User
participant "MovieEditScreen" as UI
participant "MovieViewModel" as VM
participant "MovieApi" as API
participant "MovieController" as C
participant "MovieServiceImpl" as S
database "PostgreSQL" as DB

User -> UI : Заполняет форму
UI -> VM : saveMovie(command)
VM -> API : POST /api/movies
API -> C : HTTP request + JWT
C -> S : createMovie(userId, command)
S -> DB : save movie and collection item
DB --> S : saved entities
S --> C : MovieDto
C --> API : 201 Created
API --> VM : MovieDto
VM --> UI : Обновленное состояние
@enduml
```

![Диаграмма последовательности создания фильма](images/create-film.png)

Сценарий показывает полный путь создания карточки: от формы Android-приложения до сохранения данных в PostgreSQL. Важный момент состоит в том, что клиент передает JWT, а backend определяет владельца коллекции по токену, а не доверяет пользовательскому вводу.

## Вход пользователя

```plantuml
@startuml
actor Guest
participant "LoginScreen" as UI
participant "AuthApi" as API
participant "AuthController" as C
participant "AuthServiceImpl" as S
participant "JwtService" as JWT

Guest -> UI : Ввод email и пароля
UI -> API : POST /api/auth/login
API -> C : AuthRequest
C -> S : login(request)
S -> JWT : generateToken(user)
JWT --> S : JWT
S --> C : AuthResponse
C --> API : JSON
API --> UI : token, role
@enduml
```

![Диаграмма последовательности авторизации](images/authorization.png)

Диаграмма авторизации показывает, как email и пароль обрабатываются backend-сервисом. После успешной проверки пароль больше не передается, а мобильное приложение использует JWT для последующих запросов.

## Просмотр админки

```plantuml
@startuml
actor Admin
participant "AdminScreen" as UI
participant "AdminApi" as API
participant "AdminController" as C
participant "JwtService" as JWT
participant "Repositories" as DB

Admin -> UI : Открывает панель
UI -> API : GET /api/admin/stats
API -> C : Authorization header
C -> JWT : requireAdmin(token)
JWT --> C : JwtClaims(role=ADMIN)
C -> DB : count users/movies/items
DB --> C : counters
C --> UI : AdminStatsDto
@enduml
```

![Диаграмма проверки администратора](images/check-admin.png)

Административный сценарий начинается с проверки роли через `JwtService.requireAdmin`. Если токен не принадлежит пользователю с ролью `ADMIN`, backend не должен отдавать статистику или список учетных записей.
