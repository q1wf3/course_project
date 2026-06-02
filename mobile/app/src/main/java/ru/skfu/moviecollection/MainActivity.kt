package ru.skfu.moviecollection

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.LocaleList
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.skfu.moviecollection.api_client.ApiClient
import ru.skfu.moviecollection.control.MovieViewModel
import ru.skfu.moviecollection.control.MovieUiState
import ru.skfu.moviecollection.local_cache.AppDatabase
import ru.skfu.moviecollection.model.MovieDto
import ru.skfu.moviecollection.presentation.AdminScreen
import ru.skfu.moviecollection.presentation.LoginScreen
import ru.skfu.moviecollection.presentation.MovieDetailsScreen
import ru.skfu.moviecollection.presentation.MovieEditScreen
import ru.skfu.moviecollection.presentation.MovieListScreen
import ru.skfu.moviecollection.presentation.MovieSearchScreen
import ru.skfu.moviecollection.presentation.ProfileScreen
import ru.skfu.moviecollection.presentation.SettingsScreen
import ru.skfu.moviecollection.ui.theme.MovieCollectionTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        applyRussianLocale()
        super.onCreate(savedInstanceState)

        setContent {
            val preferences = remember {
                getSharedPreferences("movie_collection_settings", MODE_PRIVATE)
            }
            var darkTheme by remember { mutableStateOf(preferences.getBoolean("dark_theme", true)) }
            var token by remember { mutableStateOf(preferences.getString("token", "").orEmpty()) }
            var userId by remember { mutableStateOf(preferences.getString("user_id", "").orEmpty()) }
            var email by remember { mutableStateOf(preferences.getString("email", "").orEmpty()) }
            var role by remember { mutableStateOf(preferences.getString("role", "USER").orEmpty()) }
            var profileName by remember { mutableStateOf(preferences.getString("profile_name", "").orEmpty()) }
            var avatarUrl by remember { mutableStateOf(preferences.getString("avatar_url", "").orEmpty()) }
            var bio by remember { mutableStateOf(preferences.getString("profile_bio", "").orEmpty()) }
            var favoriteGenre by remember { mutableStateOf(preferences.getString("favorite_genre", "").orEmpty()) }
            var currentScreen by remember {
                mutableStateOf<Screen>(
                    when {
                        token.isBlank() -> Screen.Login
                        role == "ADMIN" -> Screen.Admin
                        else -> Screen.List
                    }
                )
            }

            MovieCollectionTheme(darkTheme = darkTheme) {
                val database = remember { AppDatabase.getInstance(this) }
                val viewModel = remember {
                    MovieViewModel(
                        movieApi = ApiClient.movieApi,
                        movieDao = database.movieDao(),
                        tokenProvider = { "Bearer $token" },
                        ownerIdProvider = { userId.ifBlank { "local" } }
                    )
                }
                val saveState by viewModel.saveState.collectAsState()
                val movieState by viewModel.state.collectAsState()
                val movies = (movieState as? MovieUiState.Success)?.movies.orEmpty()

                LaunchedEffect(currentScreen) {
                    if (currentScreen == Screen.List || currentScreen == Screen.Search || currentScreen == Screen.Profile) {
                        viewModel.loadMovies()
                    } else if (currentScreen is Screen.Edit) {
                        viewModel.resetSaveState()
                    }
                }

                val logout = {
                    token = ""
                    userId = ""
                    email = ""
                    role = "USER"
                    preferences.edit()
                        .remove("token")
                        .remove("user_id")
                        .remove("email")
                        .remove("role")
                        .apply()
                    currentScreen = Screen.Login
                }

                val mainContent: @Composable () -> Unit = {
                    when (currentScreen) {
                        Screen.List -> MovieListScreen(
                            viewModel = viewModel,
                            onMovieClick = { currentScreen = Screen.Details(it) },
                            onAddClick = { currentScreen = Screen.Edit(null) },
                            onSeedDemoClick = { viewModel.seedDemoMovies() }
                        )
                        Screen.Search -> MovieSearchScreen(
                            viewModel = viewModel,
                            onMovieClick = { currentScreen = Screen.Details(it) }
                        )
                        Screen.Profile -> ProfileScreen(
                            email = email.ifBlank { "user@movie.local" },
                            isAdmin = role == "ADMIN",
                            movies = movies,
                            profileName = profileName,
                            avatarUrl = avatarUrl,
                            bio = bio,
                            favoriteGenre = favoriteGenre,
                            onProfileChange = { nextName, nextAvatar, nextBio, nextGenre ->
                                profileName = nextName
                                avatarUrl = nextAvatar
                                bio = nextBio
                                favoriteGenre = nextGenre
                                preferences.edit()
                                    .putString("profile_name", nextName)
                                    .putString("avatar_url", nextAvatar)
                                    .putString("profile_bio", nextBio)
                                    .putString("favorite_genre", nextGenre)
                                    .apply()
                            },
                            onOpenSettings = { currentScreen = Screen.Settings },
                            onOpenAdmin = { currentScreen = Screen.Admin },
                            onLogout = logout
                        )
                        else -> Unit
                    }
                }

                when (val screen = currentScreen) {
                    Screen.Login -> LoginScreen(
                        authApi = ApiClient.authApi,
                        onLoggedIn = {
                            token = it.token
                            userId = it.userId
                            email = it.email ?: ""
                            role = it.role ?: "USER"
                            preferences.edit()
                                .putString("token", token)
                                .putString("user_id", userId)
                                .putString("email", email)
                                .putString("role", role)
                                .apply()
                            currentScreen = if (it.role == "ADMIN") Screen.Admin else Screen.List
                        }
                    )
                    Screen.Settings -> SettingsScreen(
                        isAdmin = role == "ADMIN",
                        darkTheme = darkTheme,
                        onDarkThemeChange = {
                            darkTheme = it
                            preferences.edit().putBoolean("dark_theme", it).apply()
                        },
                        onBack = { currentScreen = Screen.Profile },
                        onOpenAdmin = { currentScreen = Screen.Admin },
                        onLogout = logout
                    )
                    Screen.List, Screen.Search, Screen.Profile -> Scaffold(
                        bottomBar = {
                            MovieBottomBar(
                                currentScreen = currentScreen,
                                onHome = { currentScreen = Screen.List },
                                onSearch = { currentScreen = Screen.Search },
                                onAdd = { currentScreen = Screen.Edit(null) },
                                onProfile = { currentScreen = Screen.Profile }
                            )
                        }
                    ) { contentPadding ->
                        Box(modifier = androidx.compose.ui.Modifier.padding(contentPadding)) {
                            mainContent()
                        }
                    }
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
                        saveState = saveState,
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
                    Screen.Admin -> AdminScreen(
                        adminApi = ApiClient.adminApi,
                        token = token,
                        currentUserId = userId,
                        onOpenCollection = { currentScreen = Screen.List },
                        onLogout = logout
                    )
                }
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase.withRussianLocale())
    }

    private fun applyRussianLocale() {
        Locale.setDefault(RussianLocale)
        resources.configuration.setLocales(LocaleList(RussianLocale))
    }
}

private val RussianLocale = Locale("ru", "RU")

private fun Context.withRussianLocale(): Context {
    Locale.setDefault(RussianLocale)
    val configuration = Configuration(resources.configuration)
    configuration.setLocales(LocaleList(RussianLocale))
    return createConfigurationContext(configuration)
}

@androidx.compose.runtime.Composable
private fun MovieBottomBar(
    currentScreen: Screen,
    onHome: () -> Unit,
    onSearch: () -> Unit,
    onAdd: () -> Unit,
    onProfile: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    NavigationBar(containerColor = colors.surface, contentColor = colors.onSurface) {
        NavigationBarItem(
            selected = currentScreen == Screen.List,
            onClick = onHome,
            icon = { Text("⌂", fontSize = 28.sp, fontWeight = FontWeight.Bold) },
            label = { Text("Домой") }
        )
        NavigationBarItem(
            selected = currentScreen == Screen.Search,
            onClick = onSearch,
            icon = { Text("⌕", fontSize = 28.sp, fontWeight = FontWeight.Bold) },
            label = { Text("Поиск") }
        )
        NavigationBarItem(
            selected = false,
            onClick = onAdd,
            icon = { Text("+", fontSize = 32.sp, fontWeight = FontWeight.Bold) },
            label = { Text("Добавить") }
        )
        NavigationBarItem(
            selected = currentScreen == Screen.Profile,
            onClick = onProfile,
            icon = { ProfileNavIcon(selected = currentScreen == Screen.Profile) },
            label = { Text("Профиль") }
        )
    }
}

@Composable
private fun ProfileNavIcon(selected: Boolean) {
    val colors = MaterialTheme.colorScheme
    val color = if (selected) colors.primary else colors.onSurface.copy(alpha = 0.76f)
    Canvas(modifier = Modifier.size(30.dp)) {
        drawCircle(
            color = color,
            radius = size.minDimension * 0.16f,
            center = Offset(size.width * 0.5f, size.height * 0.33f)
        )
        drawArc(
            color = color,
            startAngle = 205f,
            sweepAngle = 130f,
            useCenter = false,
            topLeft = Offset(size.width * 0.22f, size.height * 0.52f),
            size = Size(size.width * 0.56f, size.height * 0.35f),
            style = Stroke(width = 2.8.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

private sealed interface Screen {
    data object Login : Screen
    data object List : Screen
    data object Search : Screen
    data object Profile : Screen
    data object Settings : Screen
    data class Details(val movie: MovieDto) : Screen
    data class Edit(val movie: MovieDto?) : Screen
    data object Admin : Screen
}
