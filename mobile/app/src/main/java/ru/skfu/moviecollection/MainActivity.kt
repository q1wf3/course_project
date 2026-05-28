package ru.skfu.moviecollection

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import ru.skfu.moviecollection.api_client.ApiClient
import ru.skfu.moviecollection.control.MovieViewModel
import ru.skfu.moviecollection.local_cache.AppDatabase
import ru.skfu.moviecollection.model.MovieDto
import ru.skfu.moviecollection.presentation.LoginScreen
import ru.skfu.moviecollection.presentation.MovieDetailsScreen
import ru.skfu.moviecollection.presentation.MovieEditScreen
import ru.skfu.moviecollection.presentation.MovieListScreen
import ru.skfu.moviecollection.presentation.SettingsScreen
import ru.skfu.moviecollection.ui.theme.MovieCollectionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MovieCollectionTheme {
                var token by remember { mutableStateOf("") }
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }
                val database = remember { AppDatabase.getInstance(this) }
                val viewModel = remember {
                    MovieViewModel(
                        movieApi = ApiClient.movieApi,
                        movieDao = database.movieDao(),
                        tokenProvider = { "Bearer $token" }
                    )
                }

                LaunchedEffect(currentScreen) {
                    if (currentScreen == Screen.List) {
                        viewModel.loadMovies()
                    }
                }

                when (val screen = currentScreen) {
                    Screen.Login -> LoginScreen(
                        authApi = ApiClient.authApi,
                        onLoggedIn = {
                            token = it
                            currentScreen = Screen.List
                        }
                    )
                    Screen.List -> MovieListScreen(
                        viewModel = viewModel,
                        onMovieClick = { currentScreen = Screen.Details(it) },
                        onAddClick = { currentScreen = Screen.Edit(null) },
                        onSettingsClick = { currentScreen = Screen.Settings }
                    )
                    is Screen.Details -> MovieDetailsScreen(
                        movie = screen.movie,
                        onBack = { currentScreen = Screen.List },
                        onEdit = { currentScreen = Screen.Edit(screen.movie) },
                        onDelete = {
                            viewModel.deleteMovie(screen.movie)
                            currentScreen = Screen.List
                        }
                    )
                    is Screen.Edit -> MovieEditScreen(
                        movie = screen.movie,
                        onBack = { currentScreen = Screen.List },
                        onSaved = { title, releaseYear, director, coverUrl, category, status, rating ->
                            viewModel.saveMovie(
                                existingMovie = screen.movie,
                                title = title,
                                releaseYear = releaseYear,
                                director = director,
                                coverUrl = coverUrl,
                                category = category,
                                status = status,
                                rating = rating,
                                onSaved = { currentScreen = Screen.List }
                            )
                        }
                    )
                    Screen.Settings -> SettingsScreen(
                        onBack = { currentScreen = Screen.List },
                        onLogout = {
                            token = ""
                            currentScreen = Screen.Login
                        }
                    )
                }
            }
        }
    }
}

private sealed interface Screen {
    data object Login : Screen
    data object List : Screen
    data class Details(val movie: MovieDto) : Screen
    data class Edit(val movie: MovieDto?) : Screen
    data object Settings : Screen
}
