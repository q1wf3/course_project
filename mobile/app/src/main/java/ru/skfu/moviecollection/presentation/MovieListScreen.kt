package ru.skfu.moviecollection.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.skfu.moviecollection.control.MovieUiState
import ru.skfu.moviecollection.control.MovieViewModel
import ru.skfu.moviecollection.model.MovieDto

private const val NoCategory = "Без категории"

@Composable
fun MovieListScreen(
    viewModel: MovieViewModel,
    onMovieClick: (MovieDto) -> Unit,
    onAddClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        Header(onAddClick, onSettingsClick)

        when (val currentState = state) {
            MovieUiState.Loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF6D28D9))
            }

            MovieUiState.Empty -> EmptyState(onAddClick)
            is MovieUiState.Error -> ErrorState(currentState.message, onAddClick)
            is MovieUiState.Success -> MovieList(
                movies = currentState.movies,
                offline = currentState.offline,
                onMovieClick = onMovieClick
            )
        }
    }
}

@Composable
private fun Header(
    onAddClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF111827), Color(0xFF6D28D9), Color(0xFFDB2777))
                )
            )
            .statusBarsPadding()
            .padding(start = 22.dp, end = 22.dp, top = 46.dp, bottom = 24.dp)
    ) {
        Text("Моя фильмотека", color = Color.White, style = MaterialTheme.typography.headlineLarge)
        Text(
            "Фильмы, обложки, категории, статусы просмотра и оценки.",
            color = Color(0xFFEDE9FE),
            modifier = Modifier.padding(top = 8.dp)
        )
        Row(
            modifier = Modifier.padding(top = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onAddClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF6D28D9)
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text("+ Добавить")
            }
            Button(
                onClick = onSettingsClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0x33FFFFFF),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text("Настройки")
            }
        }
    }
}

@Composable
private fun EmptyState(onAddClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(22.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(16.dp, RoundedCornerShape(30.dp))
        ) {
            Column(
                modifier = Modifier.padding(26.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Коллекция пока пуста", style = MaterialTheme.typography.titleLarge)
                Text(
                    "Добавь первый фильм: русское название, обложку, категорию, статус и оценку.",
                    color = Color(0xFF6B7280),
                    modifier = Modifier.padding(top = 10.dp)
                )
                Button(
                    onClick = onAddClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D28D9)),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.padding(top = 18.dp)
                ) {
                    Text("Добавить фильм")
                }
            }
        }
    }
}

@Composable
private fun ErrorState(message: String, onAddClick: () -> Unit) {
    Column(modifier = Modifier.padding(22.dp)) {
        Surface(
            color = Color(0xFFFEE2E2),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Ошибка: $message",
                color = Color(0xFF991B1B),
                modifier = Modifier.padding(16.dp)
            )
        }
        Button(onClick = onAddClick, modifier = Modifier.padding(top = 16.dp)) {
            Text("Добавить вручную")
        }
    }
}

@Composable
private fun MovieList(
    movies: List<MovieDto>,
    offline: Boolean,
    onMovieClick: (MovieDto) -> Unit
) {
    val groupedMovies = movies
        .groupBy { normalizeCategory(it.category) }
        .toSortedMap(compareBy<String> { if (it == NoCategory) "яяя" else it.lowercase() })

    Column {
        if (offline) {
            Surface(
                color = Color(0xFFFEF3C7),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    "Оффлайн-режим: данные загружены из кэша",
                    color = Color(0xFF92400E),
                    modifier = Modifier.padding(14.dp)
                )
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            item {
                CategoryCollections(groupedMovies)
            }
            groupedMovies.forEach { (category, categoryMovies) ->
                item {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF111827),
                        modifier = Modifier.padding(top = 18.dp, bottom = 8.dp)
                    )
                }
                itemsIndexed(categoryMovies) { index, movie ->
                    MovieCard(rank = index + 1, movie = movie, onMovieClick = onMovieClick)
                }
            }
            item {
                Spacer(modifier = Modifier.height(26.dp))
            }
        }
    }
}

@Composable
private fun CategoryCollections(groupedMovies: Map<String, List<MovieDto>>) {
    if (groupedMovies.isEmpty()) return

    Text(
        text = "Подборки",
        style = MaterialTheme.typography.titleLarge,
        color = Color(0xFF111827),
        modifier = Modifier.padding(top = 18.dp, bottom = 10.dp)
    )
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        itemsIndexed(groupedMovies.entries.toList()) { _, entry ->
            CollectionCard(category = entry.key, movies = entry.value)
        }
    }
}

@Composable
private fun CollectionCard(category: String, movies: List<MovieDto>) {
    val watchedCount = movies.count { it.status.name == "WATCHED" }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .width(246.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            val firstCover = movies.firstOrNull { !it.coverUrl.isNullOrBlank() }?.coverUrl
            Cover(firstCover, width = 58, height = 80)
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = category,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text("${movies.size} фильмов", color = Color(0xFF6B7280))
                Text(
                    "$watchedCount из ${movies.size} просмотрено",
                    color = Color(0xFF6D28D9),
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun MovieCard(rank: Int, movie: MovieDto, onMovieClick: (MovieDto) -> Unit) {
    Card(
        onClick = { onMovieClick(movie) },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(6.dp, RoundedCornerShape(24.dp))
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = rank.toString(),
                color = Color(0xFF111827),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.width(26.dp)
            )
            Cover(movie.coverUrl, width = 72, height = 104)
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${movie.releaseYear} • ${movie.director ?: "режиссёр не указан"}",
                    color = Color(0xFF6B7280),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = normalizeCategory(movie.category),
                    color = Color(0xFF6B7280),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    Chip(movie.status.label)
                    Chip("★ ${movie.rating ?: "—"}")
                }
            }
            Text(
                text = movie.rating?.let { "★ $it" } ?: "☆",
                color = Color(0xFFF59E0B),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun Cover(coverUrl: String?, width: Int, height: Int) {
    if (coverUrl.isNullOrBlank()) {
        Box(
            modifier = Modifier
                .size(width = width.dp, height = height.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Brush.linearGradient(listOf(Color(0xFF6D28D9), Color(0xFFDB2777)))),
            contentAlignment = Alignment.Center
        ) {
            Text("🎬", style = MaterialTheme.typography.titleLarge)
        }
    } else {
        AsyncImage(
            model = coverUrl,
            contentDescription = "Обложка фильма",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(width = width.dp, height = height.dp)
                .clip(RoundedCornerShape(16.dp))
        )
    }
}

@Composable
private fun Chip(text: String) {
    Surface(
        color = Color(0xFFEDE9FE),
        shape = RoundedCornerShape(999.dp)
    ) {
        Text(
            text = text,
            color = Color(0xFF5B21B6),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
        )
    }
}

private fun normalizeCategory(category: String?): String {
    return category?.trim()?.ifBlank { NoCategory } ?: NoCategory
}
