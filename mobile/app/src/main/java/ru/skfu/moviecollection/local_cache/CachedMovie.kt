package ru.skfu.moviecollection.local_cache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_movies")
data class CachedMovie(
    @PrimaryKey val id: String,
    @ColumnInfo(defaultValue = "local")
    val ownerId: String,
    val title: String,
    val releaseYear: Int,
    val director: String?,
    val coverUrl: String?,
    val category: String?,
    val status: String,
    val rating: Int?,
    val favorite: Boolean,
    val pendingAction: String?
)
