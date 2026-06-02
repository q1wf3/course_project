package ru.skfu.moviecollection.api_client

import retrofit2.http.GET
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import ru.skfu.moviecollection.model.MovieDto

interface AdminApi {
    @GET("admin/stats")
    suspend fun stats(@Header("Authorization") token: String): AdminStatsResponse

    @GET("admin/users")
    suspend fun users(@Header("Authorization") token: String): List<AdminUserResponse>

    @GET("admin/users/{userId}/movies")
    suspend fun userMovies(
        @Header("Authorization") token: String,
        @Path("userId") userId: String
    ): List<MovieDto>

    @PUT("admin/users/{userId}/role")
    suspend fun changeRole(
        @Header("Authorization") token: String,
        @Path("userId") userId: String,
        @Query("role") role: String
    ): AdminUserResponse

    @DELETE("admin/users/{userId}")
    suspend fun deleteUser(
        @Header("Authorization") token: String,
        @Path("userId") userId: String
    )
}

data class AdminStatsResponse(
    val usersCount: Long,
    val moviesCount: Long,
    val collectionItemsCount: Long
)

data class AdminUserResponse(
    val id: String,
    val email: String,
    val role: String,
    val moviesCount: Long
)
