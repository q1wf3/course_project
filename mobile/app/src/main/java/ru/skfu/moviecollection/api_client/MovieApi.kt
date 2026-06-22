package ru.skfu.moviecollection.api_client

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import ru.skfu.moviecollection.model.MovieDto
import ru.skfu.moviecollection.model.WatchStatus

interface MovieApi {
    @GET("movies")
    suspend fun getMovies(@Header("Authorization") token: String): List<MovieDto>

    @GET("movies/search")
    suspend fun searchMovies(
        @Header("Authorization") token: String,
        @Query("query") query: String?,
        @Query("status") status: WatchStatus?
    ): List<MovieDto>

    @POST("movies")
    suspend fun createMovie(
        @Header("Authorization") token: String,
        @Body request: MovieRequest
    ): MovieDto

    @PUT("movies/{movieId}")
    suspend fun updateMovie(
        @Header("Authorization") token: String,
        @Path("movieId") movieId: String,
        @Body request: MovieRequest
    ): MovieDto

    @DELETE("movies/{movieId}")
    suspend fun deleteMovie(
        @Header("Authorization") token: String,
        @Path("movieId") movieId: String
    )

    @GET("complaints/my")
    suspend fun myComplaints(@Header("Authorization") token: String): List<ComplaintResponse>

    @POST("complaints")
    suspend fun createComplaint(
        @Header("Authorization") token: String,
        @Body request: ComplaintRequest
    )
}

data class MovieRequest(
    val title: String,
    val releaseYear: Int,
    val director: String?,
    val durationMinutes: Int,
    val description: String?,
    val coverUrl: String?,
    val category: String?,
    val status: WatchStatus,
    val rating: Int?,
    val note: String?
)



data class ComplaintRequest(
    val movieId: String,
    val reason: String,
    val description: String
)
