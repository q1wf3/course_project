# Модель предметной области

```plantuml
@startuml
entity "users" as USERS {
    * id : uuid <<PK>>
    --
    email : string
    password_hash : string
    role : string
}

entity "movies" as MOVIES {
    * id : uuid <<PK>>
    --
    title : string
    release_year : int
    director : string
    duration_minutes : int
    cover_url : string
    category : string
}

entity "collection_items" as COLLECTION_ITEMS {
    * id : uuid <<PK>>
    --
    user_id : uuid <<FK>>
    movie_id : uuid <<FK>>
    status : string
    rating : int
    note : string
    favorite : boolean
    updated_at : timestamp
}

entity "genres" as GENRES {
    * id : uuid <<PK>>
    --
    name : string
}

USERS ||--o{ COLLECTION_ITEMS : owns
MOVIES ||--o{ COLLECTION_ITEMS : included_in
MOVIES }o--o{ GENRES : classified_by
@enduml
```

![Модель предметной области](images/domain-model.png)

Модель учитывает многопользовательскую природу приложения. Таблица `collection_items` позволяет хранить разные статусы и оценки одного фильма у разных пользователей.
Связь `users` и `movies` выполнена через промежуточную сущность, потому что пользовательские поля не относятся к самому фильму глобально. Благодаря этому два пользователя могут добавить один и тот же фильм, но хранить разные оценки, заметки и статусы просмотра.
