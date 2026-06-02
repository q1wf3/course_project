package ru.skfu.moviecollection.local_cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieDao {
    @Query("SELECT * FROM cached_movies WHERE ownerId = :ownerId AND (pendingAction IS NULL OR pendingAction != 'DELETE') ORDER BY title")
    suspend fun findAll(ownerId: String): List<CachedMovie>

    @Query("SELECT * FROM cached_movies WHERE ownerId = :ownerId AND pendingAction IS NOT NULL ORDER BY title")
    suspend fun findPending(ownerId: String): List<CachedMovie>

    @Query("SELECT * FROM cached_movies WHERE ownerId = :ownerId AND id = :id LIMIT 1")
    suspend fun findById(ownerId: String, id: String): CachedMovie?

    @Query("SELECT * FROM cached_movies WHERE ownerId = :ownerId AND lower(title) LIKE '%' || lower(:query) || '%' AND (pendingAction IS NULL OR pendingAction != 'DELETE') ORDER BY title")
    suspend fun search(ownerId: String, query: String): List<CachedMovie>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAll(movies: List<CachedMovie>)

    @Query("DELETE FROM cached_movies WHERE ownerId = :ownerId AND pendingAction IS NULL")
    suspend fun deleteSynced(ownerId: String)

    @Query("DELETE FROM cached_movies WHERE ownerId = :ownerId AND id = :id")
    suspend fun deleteById(ownerId: String, id: String)
}
