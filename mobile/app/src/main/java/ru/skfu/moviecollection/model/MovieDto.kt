package ru.skfu.moviecollection.model

import java.util.UUID

data class MovieDto(
    val id: UUID,
    val title: String,
    val releaseYear: Int,
    val director: String?,
    val coverUrl: String?,
    val category: String?,
    val status: WatchStatus,
    val rating: Int?,
    val favorite: Boolean
)

enum class WatchStatus(val label: String) {
    PLANNED("Планирую"),
    WATCHING("Смотрю"),
    WATCHED("Просмотрено"),
    DROPPED("Брошено")
}

