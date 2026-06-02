# Диаграмма зависимостей

```plantuml
@startuml
component "Presentation:
Compose screens" as UI
component "Control:
MovieViewModel" as VM
component "Foundation:
Retrofit API" as API
component "Foundation:
Room DAO" as ROOM
database "PostgreSQL" as DB
component "Backend Control:
REST controllers" as BC
component "Backend Mediator:
services" as BM
component "Backend Entity:
JPA entities" as BE
component "Backend Foundation:
repositories" as BF

UI --> VM
VM --> API
VM --> ROOM
API --> BC
BC --> BM
BM --> BE
BM --> BF
BF --> DB
@enduml
```

![Диаграмма зависимостей](images/dependency-diagram.png)

Зависимости направлены от интерфейса к бизнес-логике и хранилищам. Контроллеры не содержат бизнес-правил, а делегируют работу сервисам.
Такое направление зависимостей снижает связность: изменение UI не требует переписывать backend-сервисы, а изменение способа хранения данных не должно напрямую затрагивать Compose-экраны.
