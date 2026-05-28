package ru.skfu.moviecollection.local_cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieDao {
    @Query("SELECT * FROM cached_movies ORDER BY title")
    suspend fun findAll(): List<CachedMovie>

    @Query("SELECT * FROM cached_movies WHERE lower(title) LIKE '%' || lower(:query) || '%' ORDER BY title")
    suspend fun search(query: String): List<CachedMovie>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAll(movies: List<CachedMovie>)
}

