package ru.skfu.moviecollection.control

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import ru.skfu.moviecollection.api_client.MovieApi
import ru.skfu.moviecollection.api_client.MovieRequest
import ru.skfu.moviecollection.local_cache.CachedMovie
import ru.skfu.moviecollection.local_cache.MovieDao
import ru.skfu.moviecollection.model.MovieDto
import ru.skfu.moviecollection.model.WatchStatus

class MovieViewModel(
    private val movieApi: MovieApi,
    private val movieDao: MovieDao,
    private val tokenProvider: () -> String
) : ViewModel() {
    private val mutableState = MutableStateFlow<MovieUiState>(MovieUiState.Loading)
    val state: StateFlow<MovieUiState> = mutableState.asStateFlow()

    fun loadMovies() {
        viewModelScope.launch {
            mutableState.value = MovieUiState.Loading
            try {
                val movies = movieApi.getMovies(tokenProvider())
                movieDao.saveAll(movies.map { it.toCache() })
                mutableState.value = if (movies.isEmpty()) MovieUiState.Empty else MovieUiState.Success(movies)
            } catch (exception: Exception) {
                val cachedMovies = movieDao.findAll().map { it.toDto() }
                mutableState.value = if (cachedMovies.isEmpty()) {
                    MovieUiState.Error(exception.message ?: "Ошибка загрузки фильмов")
                } else {
                    MovieUiState.Success(cachedMovies, offline = true)
                }
            }
        }
    }

    fun saveMovie(
        existingMovie: MovieDto?,
        title: String,
        releaseYear: Int,
        director: String?,
        coverUrl: String?,
        category: String?,
        status: WatchStatus,
        rating: Int?,
        onSaved: () -> Unit = {}
    ) {
        viewModelScope.launch {
            mutableState.value = MovieUiState.Loading
            try {
                val request = MovieRequest(
                    title = title,
                    releaseYear = releaseYear,
                    director = director,
                    durationMinutes = 120,
                    description = null,
                    coverUrl = coverUrl?.trim()?.ifBlank { null },
                    category = category?.trim()?.ifBlank { "Без категории" },
                    status = status,
                    rating = rating,
                    note = null
                )
                val savedMovie = if (existingMovie == null) {
                    movieApi.createMovie(tokenProvider(), request)
                } else {
                    updateExistingMovie(existingMovie, request)
                }
                movieDao.saveAll(listOf(savedMovie.toCache()))

                val movies = movieApi.getMovies(tokenProvider())
                movieDao.saveAll(movies.map { it.toCache() })
                mutableState.value = if (movies.isEmpty()) MovieUiState.Empty else MovieUiState.Success(movies)
                onSaved()
            } catch (exception: Exception) {
                mutableState.value = MovieUiState.Error(exception.message ?: "Не удалось сохранить фильм")
            }
        }
    }

    private suspend fun updateExistingMovie(existingMovie: MovieDto, request: MovieRequest): MovieDto {
        return try {
            movieApi.updateMovie(tokenProvider(), existingMovie.id.toString(), request)
        } catch (exception: HttpException) {
            if (exception.code() != 405) {
                throw exception
            }
            val recreatedMovie = movieApi.createMovie(tokenProvider(), request)
            movieApi.deleteMovie(tokenProvider(), existingMovie.id.toString())
            recreatedMovie
        }
    }

    fun deleteMovie(movie: MovieDto) {
        viewModelScope.launch {
            mutableState.value = MovieUiState.Loading
            try {
                movieApi.deleteMovie(tokenProvider(), movie.id.toString())
                loadMovies()
            } catch (exception: Exception) {
                mutableState.value = MovieUiState.Error(exception.message ?: "Не удалось удалить фильм")
            }
        }
    }

    private fun MovieDto.toCache(): CachedMovie = CachedMovie(
        id = id.toString(),
        title = title,
        releaseYear = releaseYear,
        director = director,
        coverUrl = coverUrl,
        category = category,
        status = status.name,
        rating = rating,
        favorite = favorite
    )

    private fun CachedMovie.toDto(): MovieDto = MovieDto(
        id = UUID.fromString(id),
        title = title,
        releaseYear = releaseYear,
        director = director,
        coverUrl = coverUrl,
        category = category,
        status = WatchStatus.valueOf(status),
        rating = rating,
        favorite = favorite
    )
}
