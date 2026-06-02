# ER-диаграмма

```plantuml
@startuml
entity "users" as USERS {
    * id : UUID <<PK>>
    --
    email : VARCHAR <<UK>>
    password_hash : VARCHAR
    role : VARCHAR
}

entity "movies" as MOVIES {
    * id : UUID <<PK>>
    --
    title : VARCHAR
    release_year : INT
    director : VARCHAR
    duration_minutes : INT
    description : VARCHAR
    cover_url : VARCHAR
    category : VARCHAR
}

entity "collection_items" as COLLECTION_ITEMS {
    * id : UUID <<PK>>
    --
    user_id : UUID <<FK>>
    movie_id : UUID <<FK>>
    status : VARCHAR
    rating : INT
    note : VARCHAR
    favorite : BOOLEAN
    updated_at : TIMESTAMP
}

entity "genres" as GENRES {
    * id : UUID <<PK>>
    --
    name : VARCHAR <<UK>>
}

entity "movie_genres" as MOVIE_GENRES {
    * movie_id : UUID <<PK, FK>>
    * genre_id : UUID <<PK, FK>>
}

USERS ||--o{ COLLECTION_ITEMS : owns
MOVIES ||--o{ COLLECTION_ITEMS : appears_in
MOVIES ||--o{ MOVIE_GENRES
GENRES ||--o{ MOVIE_GENRES
@enduml
```

![ER-диаграмма](images/er-diagram.png)

Ограничение `UNIQUE (user_id, movie_id)` запрещает добавлять один и тот же фильм в коллекцию одного пользователя дважды.
ER-диаграмма показывает, что серверная база данных хранит не только фильмы, но и персональную связь пользователя с фильмом. Именно таблица `collection_items` содержит статус просмотра, оценку, заметку и признак избранного.
