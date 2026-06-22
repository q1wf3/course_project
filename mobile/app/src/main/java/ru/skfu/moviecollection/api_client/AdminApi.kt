package ru.skfu.moviecollection.api_client

import retrofit2.http.GET
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Body
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

    @GET("admin/complaints")
    suspend fun complaints(
        @Header("Authorization") token: String,
        @Query("status") status: String? = null
    ): List<ComplaintResponse>

    @PUT("admin/complaints/{complaintId}/status")
    suspend fun updateComplaintStatus(
        @Header("Authorization") token: String,
        @Path("complaintId") complaintId: String,
        @Body request: UpdateComplaintStatusRequest
    ): ComplaintResponse
}

data class AdminStatsResponse(
    val usersCount: Long,
    val moviesCount: Long,
    val collectionItemsCount: Long,
    val openComplaintsCount: Long
)

data class AdminUserResponse(
    val id: String,
    val email: String,
    val role: String,
    val moviesCount: Long
)


data class ComplaintResponse(
    val id: String,
    val reporterId: String,
    val reporterEmail: String,
    val movieId: String,
    val movieTitle: String,
    val reason: String,
    val description: String,
    val status: String,
    val adminComment: String?,
    val createdAt: String?,
    val resolvedAt: String?
)

data class UpdateComplaintStatusRequest(
    val status: String,
    val adminComment: String? = null
)
