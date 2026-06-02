package ru.skfu.moviecollection.control

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.util.UUID
import kotlinx.coroutines.CancellationException
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
    private val tokenProvider: () -> String,
    private val ownerIdProvider: () -> String
) : ViewModel() {
    private val mutableState = MutableStateFlow<MovieUiState>(MovieUiState.Loading)
    val state: StateFlow<MovieUiState> = mutableState.asStateFlow()

    private val mutableSaveState = MutableStateFlow<SaveMovieState>(SaveMovieState.Idle)
    val saveState: StateFlow<SaveMovieState> = mutableSaveState.asStateFlow()

    fun resetSaveState() {
        mutableSaveState.value = SaveMovieState.Idle
    }

    fun loadMovies() {
        viewModelScope.launch {
            mutableState.value = MovieUiState.Loading
            val ownerId = ownerIdProvider()
            val cachedMovies = movieDao.findAll(ownerId).map { it.toDto() }
            if (cachedMovies.isNotEmpty()) {
                mutableState.value = MovieUiState.Success(cachedMovies, offline = true)
            }
            try {
                syncPendingMovies(ownerId)
                val movies = movieApi.getMovies(tokenProvider())
                movieDao.deleteSynced(ownerId)
                movieDao.saveAll(movies.map { it.toCache(ownerId) })
                mutableState.value = if (movies.isEmpty()) MovieUiState.Empty else MovieUiState.Success(movies)
            } catch (exception: Exception) {
                val freshCachedMovies = movieDao.findAll(ownerId).map { it.toDto() }
                mutableState.value = if (freshCachedMovies.isEmpty()) {
                    MovieUiState.Error(exception.message ?: "Ошибка загрузки фильмов")
                } else {
                    MovieUiState.Success(freshCachedMovies, offline = true)
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
            mutableSaveState.value = SaveMovieState.Saving
            val ownerId = ownerIdProvider()
            val request = MovieRequest(
                title = title.trim(),
                releaseYear = releaseYear,
                director = director?.trim()?.ifBlank { null },
                durationMinutes = 120,
                description = null,
                coverUrl = coverUrl?.trim()?.ifBlank { null },
                category = category?.trim()?.ifBlank { "Без категории" },
                status = status,
                rating = rating,
                note = null
            )
            try {
                val savedMovie = if (existingMovie == null) {
                    movieApi.createMovie(tokenProvider(), request)
                } else {
                    updateExistingMovie(existingMovie, request)
                }
                movieDao.saveAll(listOf(savedMovie.toCache(ownerId)))

                val currentMovies = when (val currentState = mutableState.value) {
                    is MovieUiState.Success -> currentState.movies
                    else -> movieDao.findAll(ownerId).map { it.toDto() }
                }
                updateMoviesInState(currentMovies, savedMovie, offline = false)
                mutableSaveState.value = SaveMovieState.Saved
                onSaved()
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                val localMovie = MovieDto(
                    id = existingMovie?.id ?: UUID.randomUUID(),
                    title = request.title.ifBlank { "Без названия" },
                    releaseYear = request.releaseYear,
                    director = request.director,
                    coverUrl = request.coverUrl,
                    category = request.category,
                    status = request.status,
                    rating = request.rating,
                    favorite = existingMovie?.favorite ?: false
                )
                val existingCache = existingMovie?.let { movieDao.findById(ownerId, it.id.toString()) }
                val pendingAction = if (existingMovie == null || existingCache?.pendingAction == PendingCreate) {
                    PendingCreate
                } else {
                    PendingUpdate
                }
                movieDao.saveAll(listOf(localMovie.toCache(ownerId, pendingAction)))
                val currentMovies = when (val currentState = mutableState.value) {
                    is MovieUiState.Success -> currentState.movies
                    else -> movieDao.findAll(ownerId).map { it.toDto() }
                }
                updateMoviesInState(currentMovies, localMovie, offline = true)
                mutableSaveState.value = SaveMovieState.Saved
                onSaved()
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
            val ownerId = ownerIdProvider()
            try {
                movieApi.deleteMovie(tokenProvider(), movie.id.toString())
                movieDao.deleteById(ownerId, movie.id.toString())
                removeMovieFromState(movie.id, offline = false)
            } catch (exception: Exception) {
                val cachedMovie = movieDao.findById(ownerId, movie.id.toString())
                if (cachedMovie?.pendingAction == PendingCreate) {
                    movieDao.deleteById(ownerId, movie.id.toString())
                } else {
                    movieDao.saveAll(listOf(movie.toCache(ownerId, PendingDelete)))
                }
                removeMovieFromState(movie.id, offline = true)
            }
        }
    }

    fun seedDemoMovies() {
        viewModelScope.launch {
            val ownerId = ownerIdProvider()
            val existingMovies = movieDao.findAll(ownerId).map { it.toDto() }
            val existingTitles = existingMovies.map { it.title.lowercase() }.toSet()
            val demoMovies = demoMovies()
                .filterNot { it.title.lowercase() in existingTitles }
            if (demoMovies.isEmpty()) {
                mutableState.value = MovieUiState.Success(existingMovies, offline = true)
                return@launch
            }
            movieDao.saveAll(demoMovies.map { it.toCache(ownerId, PendingCreate) })
            val movies = existingMovies
                .plus(demoMovies)
                .sortedBy { it.title.lowercase() }
            mutableState.value = MovieUiState.Success(movies, offline = true)
        }
    }

    private suspend fun syncPendingMovies(ownerId: String) {
        movieDao.findPending(ownerId).forEach { cachedMovie ->
            when (cachedMovie.pendingAction) {
                PendingCreate -> {
                    val savedMovie = movieApi.createMovie(tokenProvider(), cachedMovie.toRequest())
                    movieDao.deleteById(ownerId, cachedMovie.id)
                    movieDao.saveAll(listOf(savedMovie.toCache(ownerId)))
                }
                PendingUpdate -> {
                    val savedMovie = updateExistingMovie(cachedMovie.toDto(), cachedMovie.toRequest())
                    movieDao.saveAll(listOf(savedMovie.toCache(ownerId)))
                }
                PendingDelete -> {
                    try {
                        movieApi.deleteMovie(tokenProvider(), cachedMovie.id)
                    } catch (exception: HttpException) {
                        if (exception.code() != 404) {
                            throw exception
                        }
                    }
                    movieDao.deleteById(ownerId, cachedMovie.id)
                }
            }
        }
    }

    private fun updateMoviesInState(currentMovies: List<MovieDto>, savedMovie: MovieDto, offline: Boolean) {
        val movies = currentMovies
            .filterNot { it.id == savedMovie.id }
            .plus(savedMovie)
            .sortedBy { it.title.lowercase() }
        mutableState.value = MovieUiState.Success(movies, offline = offline)
    }

    private fun removeMovieFromState(movieId: UUID, offline: Boolean) {
        val currentMovies = (mutableState.value as? MovieUiState.Success)?.movies.orEmpty()
        val movies = currentMovies.filterNot { it.id == movieId }
        mutableState.value = if (movies.isEmpty()) MovieUiState.Empty else MovieUiState.Success(movies, offline = offline)
    }

    private fun MovieDto.toCache(ownerId: String, pendingAction: String? = null): CachedMovie = CachedMovie(
        id = id.toString(),
        ownerId = ownerId,
        title = title,
        releaseYear = releaseYear,
        director = director,
        coverUrl = coverUrl,
        category = category,
        status = status.name,
        rating = rating,
        favorite = favorite,
        pendingAction = pendingAction
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

    private fun CachedMovie.toRequest(): MovieRequest = MovieRequest(
        title = title,
        releaseYear = releaseYear,
        director = director,
        durationMinutes = 120,
        description = null,
        coverUrl = coverUrl,
        category = category,
        status = WatchStatus.valueOf(status),
        rating = rating,
        note = null
    )

    private fun Throwable.toUserMessage(): String {
        return when (this) {
            is HttpException -> when (code()) {
                400 -> "Проверь поля фильма: год должен быть от 1888 до 2100, оценка от 1 до 10."
                401, 403 -> "Сессия истекла. Выйди и зайди снова."
                404 -> "Фильм не найден. Обнови список и попробуй еще раз."
                else -> "Ошибка backend: HTTP ${code()}."
            }
            else -> message ?: "Не удалось сохранить фильм"
        }
    }

    private fun demoMovies(): List<MovieDto> = listOf(
        demoMovie("Побег из Шоушенка", 1994, "Фрэнк Дарабонт", "Драма", WatchStatus.WATCHED, 10),
        demoMovie("Зеленая миля", 1999, "Фрэнк Дарабонт", "Драма", WatchStatus.WATCHED, 9),
        demoMovie("Интерстеллар", 2014, "Кристофер Нолан", "Фантастика", WatchStatus.WATCHED, 10),
        demoMovie("Начало", 2010, "Кристофер Нолан", "Фантастика", WatchStatus.WATCHED, 9),
        demoMovie("Опенгеймер", 2023, "Кристофер Нолан", "Биография", WatchStatus.PLANNED, null),
        demoMovie("Дюна", 2021, "Дени Вильнев", "Фантастика", WatchStatus.WATCHED, 8),
        demoMovie("Дюна: Часть вторая", 2024, "Дени Вильнев", "Фантастика", WatchStatus.PLANNED, null),
        demoMovie("Бегущий по лезвию 2049", 2017, "Дени Вильнев", "Фантастика", WatchStatus.WATCHED, 9),
        demoMovie("Ла-Ла Ленд", 2016, "Дэмьен Шазелл", "Мюзикл", WatchStatus.WATCHED, 8),
        demoMovie("Одержимость", 2014, "Дэмьен Шазелл", "Драма", WatchStatus.WATCHED, 9),
        demoMovie("Грань будущего", 2014, "Даг Лайман", "Боевик", WatchStatus.WATCHED, 8),
        demoMovie("Матрица", 1999, "Лана и Лилли Вачовски", "Фантастика", WatchStatus.WATCHED, 10),
        demoMovie("Форрест Гамп", 1994, "Роберт Земекис", "Драма", WatchStatus.WATCHED, 9),
        demoMovie("Криминальное чтиво", 1994, "Квентин Тарантино", "Криминал", WatchStatus.WATCHING, 9),
        demoMovie("Унесенные призраками", 2001, "Хаяо Миядзаки", "Анимация", WatchStatus.PLANNED, null)
    )

    private fun demoMovie(
        title: String,
        releaseYear: Int,
        director: String,
        category: String,
        status: WatchStatus,
        rating: Int?
    ): MovieDto = MovieDto(
        id = UUID.nameUUIDFromBytes("demo-$title-$releaseYear".toByteArray()),
        title = title,
        releaseYear = releaseYear,
        director = director,
        coverUrl = null,
        category = category,
        status = status,
        rating = rating,
        favorite = rating == 10
    )

    private companion object {
        const val PendingCreate = "CREATE"
        const val PendingUpdate = "UPDATE"
        const val PendingDelete = "DELETE"
    }
}

sealed interface SaveMovieState {
    data object Idle : SaveMovieState
    data object Saving : SaveMovieState
    data object Saved : SaveMovieState
    data class Error(val message: String) : SaveMovieState
}
