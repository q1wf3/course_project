package ru.skfu.moviecollection.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ru.skfu.moviecollection.control.MovieUiState
import ru.skfu.moviecollection.control.MovieViewModel
import ru.skfu.moviecollection.model.MovieDto

private const val NoCategory = "Без категории"

@Composable
fun MovieListScreen(
    viewModel: MovieViewModel,
    onMovieClick: (MovieDto) -> Unit,
    onAddClick: () -> Unit,
    onSeedDemoClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        when (val currentState = state) {
            MovieUiState.Loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = colors.primary)
            }

            MovieUiState.Empty -> EmptyState(onAddClick, onSeedDemoClick)
            is MovieUiState.Error -> ErrorState(currentState.message, onAddClick, onSeedDemoClick)
            is MovieUiState.Success -> MovieList(
                movies = currentState.movies,
                offline = currentState.offline,
                onMovieClick = onMovieClick
            )
        }
    }
}

@Composable
private fun Header() {
    val colors = MaterialTheme.colorScheme
    Card(
        shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    listOf(colors.background, colors.surface, colors.surfaceVariant)
                )
            )
            .statusBarsPadding()
            .padding(start = 14.dp, end = 14.dp, top = 18.dp, bottom = 8.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text("Movie Collection", color = colors.onSurface, style = MaterialTheme.typography.headlineLarge)
            Text(
                "Личная кинополка: постеры, статусы, оценки и подборки.",
                color = colors.onSurface.copy(alpha = 0.66f),
                modifier = Modifier.padding(top = 8.dp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                HeaderChip("Фильмы", "вся коллекция")
                HeaderChip("Подборки", "по жанрам")
                HeaderChip("Без интернета", "сохранено")
            }
        }
    }
}

@Composable
private fun HeaderChip(title: String, subtitle: String) {
    val colors = MaterialTheme.colorScheme
    Surface(
        color = colors.surfaceVariant,
        shape = RoundedCornerShape(999.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)) {
            Text(title, color = colors.primary, fontSize = 15.sp)
            Text(subtitle, color = colors.onSurface.copy(alpha = 0.56f), fontSize = 12.sp)
        }
    }
}

@Composable
private fun EmptyState(onAddClick: () -> Unit, onSeedDemoClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(22.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
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
                    color = colors.onSurface.copy(alpha = 0.66f),
                    modifier = Modifier.padding(top = 10.dp)
                )
                Button(
                    onClick = onAddClick,
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.padding(top = 18.dp)
                ) {
                    Text("Добавить фильм")
                }
                Button(
                    onClick = onSeedDemoClick,
                    colors = ButtonDefaults.buttonColors(containerColor = colors.surfaceVariant, contentColor = colors.primary),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    Text("Заполнить 15 фильмами")
                }
            }
        }
    }
}

@Composable
private fun ErrorState(message: String, onAddClick: () -> Unit, onSeedDemoClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Column(modifier = Modifier.padding(22.dp)) {
        Surface(
            color = colors.surfaceVariant,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Ошибка: $message",
                color = colors.onSurface,
                modifier = Modifier.padding(16.dp)
            )
        }
        Button(onClick = onAddClick, modifier = Modifier.padding(top = 16.dp)) {
            Text("Добавить вручную")
        }
        Button(onClick = onSeedDemoClick, modifier = Modifier.padding(top = 10.dp)) {
            Text("Заполнить 15 фильмами")
        }
    }
}

@Composable
private fun MovieList(
    movies: List<MovieDto>,
    offline: Boolean,
    onMovieClick: (MovieDto) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val groupedMovies = movies
        .groupBy { normalizeCategory(it.category) }
        .toSortedMap(compareBy<String> { if (it == NoCategory) "яяя" else it.lowercase() })

    Column {
        Header()
        if (offline) {
            Surface(
                color = colors.surfaceVariant,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    "Оффлайн-режим: данные загружены из кэша",
                    color = colors.tertiary,
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
                LibraryOverview(movies)
                CategoryCollections(groupedMovies)
            }
            groupedMovies.forEach { (category, categoryMovies) ->
                item {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.titleLarge,
                        color = colors.onSurface,
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
private fun LibraryOverview(movies: List<MovieDto>) {
    val colors = MaterialTheme.colorScheme
    val watchedCount = movies.count { it.status.name == "WATCHED" }
    val plannedCount = movies.count { it.status.name == "PLANNED" }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            MiniStat("Всего", movies.size.toString(), Modifier.weight(1f))
            MiniStat("Просмотрено", watchedCount.toString(), Modifier.weight(1f))
            MiniStat("План", plannedCount.toString(), Modifier.weight(1f))
        }
    }
}

@Composable
private fun MiniStat(label: String, value: String, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Column(modifier = modifier.heightIn(min = 54.dp)) {
        Text(value, style = MaterialTheme.typography.titleLarge, color = colors.onSurface)
        Text(label, color = colors.onSurface.copy(alpha = 0.62f), fontSize = 13.sp)
    }
}

@Composable
private fun CategoryCollections(groupedMovies: Map<String, List<MovieDto>>) {
    if (groupedMovies.isEmpty()) return
    val colors = MaterialTheme.colorScheme

    Text(
        text = "Подборки",
        style = MaterialTheme.typography.titleLarge,
        color = colors.onSurface,
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
    val colors = MaterialTheme.colorScheme

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
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
                Text("${movies.size} фильмов", color = colors.onSurface.copy(alpha = 0.66f))
                Text(
                    "$watchedCount из ${movies.size} просмотрено",
                    color = colors.primary,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun MovieCard(rank: Int, movie: MovieDto, onMovieClick: (MovieDto) -> Unit) {
    val colors = MaterialTheme.colorScheme
    Card(
        onClick = { onMovieClick(movie) },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(6.dp, RoundedCornerShape(24.dp))
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = rank.toString(),
                color = colors.onSurface.copy(alpha = 0.42f),
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
                    color = colors.onSurface.copy(alpha = 0.66f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = normalizeCategory(movie.category),
                    color = colors.onSurface.copy(alpha = 0.66f),
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
                color = colors.tertiary,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun Cover(coverUrl: String?, width: Int, height: Int) {
    val colors = MaterialTheme.colorScheme
    val normalizedUrl = normalizeImageUrl(coverUrl)
    if (normalizedUrl.isNullOrBlank()) {
        Box(
            modifier = Modifier
                .size(width = width.dp, height = height.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Brush.linearGradient(listOf(colors.surfaceVariant, colors.secondary))),
            contentAlignment = Alignment.Center
        ) {
            Text("🎬", style = MaterialTheme.typography.titleLarge)
        }
    } else {
        val request = ImageRequest.Builder(LocalContext.current)
            .data(normalizedUrl)
            .crossfade(true)
            .allowHardware(false)
            .build()
        AsyncImage(
            model = request,
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
    val colors = MaterialTheme.colorScheme
    Surface(
        color = colors.surfaceVariant,
        shape = RoundedCornerShape(999.dp)
    ) {
        Text(
            text = text,
            color = colors.primary,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
        )
    }
}

private fun normalizeCategory(category: String?): String {
    return category?.trim()?.ifBlank { NoCategory } ?: NoCategory
}
