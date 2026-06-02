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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ru.skfu.moviecollection.control.MovieUiState
import ru.skfu.moviecollection.control.MovieViewModel
import ru.skfu.moviecollection.model.MovieDto

@Composable
fun MovieSearchScreen(
    viewModel: MovieViewModel,
    onMovieClick: (MovieDto) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val state by viewModel.state.collectAsState()
    var query by remember { mutableStateOf("") }
    val movies = (state as? MovieUiState.Success)?.movies.orEmpty()
    val filteredMovies = movies.filter { movie ->
        val normalizedQuery = query.trim()
        normalizedQuery.isBlank() ||
            movie.title.contains(normalizedQuery, ignoreCase = true) ||
            movie.director.orEmpty().contains(normalizedQuery, ignoreCase = true) ||
            movie.category.orEmpty().contains(normalizedQuery, ignoreCase = true) ||
            movie.status.label.contains(normalizedQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .padding(horizontal = 18.dp, vertical = 20.dp)
    ) {
        Text("Поиск", style = MaterialTheme.typography.headlineLarge)
        Text(
            "Ищи по названию, режиссеру, категории или статусу.",
            color = colors.onSurface.copy(alpha = 0.66f),
            modifier = Modifier.padding(top = 6.dp, bottom = 18.dp)
        )
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Найти фильм") },
            leadingIcon = { Text("⌕") },
            singleLine = true,
            colors = movieTextFieldColors(),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                keyboardType = KeyboardType.Text
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Surface(
            color = colors.surfaceVariant,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp)
        ) {
            Text(
                text = "${filteredMovies.size} из ${movies.size} фильмов",
                color = colors.onSurface.copy(alpha = 0.72f),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }

        if (movies.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Коллекция пока пуста", color = colors.onSurface.copy(alpha = 0.66f))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredMovies, key = { it.id }) { movie ->
                    SearchMovieCard(movie = movie, onMovieClick = onMovieClick)
                }
                item { Spacer(modifier = Modifier.height(12.dp)) }
            }
        }
    }
}

@Composable
private fun SearchMovieCard(movie: MovieDto, onMovieClick: (MovieDto) -> Unit) {
    val colors = MaterialTheme.colorScheme
    Card(
        onClick = { onMovieClick(movie) },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            SearchPoster(movie.coverUrl)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    movie.title,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "${movie.releaseYear} · ${movie.director ?: "режиссер не указан"}",
                    color = colors.onSurface.copy(alpha = 0.66f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "${movie.status.label} · ${movie.category ?: "Без категории"}",
                    color = colors.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun SearchPoster(coverUrl: String?) {
    val colors = MaterialTheme.colorScheme
    val normalizedUrl = normalizeImageUrl(coverUrl)
    if (normalizedUrl.isNullOrBlank()) {
        Box(
            modifier = Modifier
                .size(width = 58.dp, height = 82.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.linearGradient(listOf(colors.surfaceVariant, colors.secondary))),
            contentAlignment = Alignment.Center
        ) {
            Text("Film")
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
                .size(width = 58.dp, height = 82.dp)
                .clip(RoundedCornerShape(14.dp))
        )
    }
}
