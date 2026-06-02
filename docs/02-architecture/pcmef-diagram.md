# PCMEF-диаграмма

```plantuml
@startuml
package "Android" {
    [Presentation
LoginScreen, MovieListScreen, MovieEditScreen, AdminScreen] as P1
    [Control
MovieViewModel, MovieUiState] as C1
    [Foundation
Retrofit, Room, SharedPreferences] as F1
}

package "Backend" {
    [Control
AuthController, MovieController, AdminController] as C2
    [Mediator
AuthServiceImpl, MovieServiceImpl, JwtService] as M2
    [Entity
User, Movie, CollectionItem] as E2
    [Foundation
Repositories, MovieMapper] as F2
}

P1 --> C1
C1 --> F1
F1 --> C2
C2 --> M2
M2 --> E2
M2 --> F2
@enduml
```

![PCMEF-диаграмма](images/pcmef-diagram.png)

PCMEF выбран как понятная учебная структура: Presentation отвечает за UI, Control за обработку пользовательских действий, Mediator за бизнес-логику, Entity за предметную модель, Foundation за доступ к данным.
На клиенте основная связка проходит от Compose-экранов к `MovieViewModel`, затем к Retrofit и Room. На сервере REST-контроллеры передают работу сервисам, а сервисы используют JPA-сущности и репозитории.
