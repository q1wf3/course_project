package ru.skfu.moviecollection.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ru.skfu.moviecollection.control.SaveMovieState
import ru.skfu.moviecollection.model.MovieDto
import ru.skfu.moviecollection.model.WatchStatus

private val CategorySuggestions = listOf(
    "Фантастика",
    "Драма",
    "Комедия",
    "Боевик",
    "Триллер",
    "Ужасы",
    "Мультфильм",
    "Документальные",
    "Сериалы",
    "Избранное"
)

@Composable
fun MovieEditScreen(
    movie: MovieDto?,
    saveState: SaveMovieState,
    onBack: () -> Unit,
    onSaved: (String, Int, String?, String?, String?, WatchStatus, Int?) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    var title by remember { mutableStateOf(movie?.title.orEmpty()) }
    var releaseYear by remember { mutableStateOf(movie?.releaseYear?.toString().orEmpty()) }
    var director by remember { mutableStateOf(movie?.director.orEmpty()) }
    var coverUrl by remember { mutableStateOf(movie?.coverUrl.orEmpty()) }
    var category by remember { mutableStateOf(movie?.category ?: "Без категории") }
    var status by remember { mutableStateOf(movie?.status ?: WatchStatus.PLANNED) }
    var rating by remember { mutableIntStateOf(movie?.rating ?: 0) }
    val isSaving = saveState is SaveMovieState.Saving

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(start = 18.dp, end = 18.dp, top = 36.dp, bottom = 24.dp)
    ) {
        Surface(
            color = colors.surfaceVariant,
            contentColor = colors.onSurface,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .size(48.dp)
                .clickable(enabled = !isSaving, onClick = onBack)
        ) {
            Box(contentAlignment = Alignment.Center) {
                BackArrowIcon()
            }
        }
        Text(
            if (movie == null) "Новый фильм" else "Редактирование",
            style = MaterialTheme.typography.headlineLarge,
            color = colors.onBackground,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            "Можно указать русское название, обложку, категорию, статус и оценку.",
            color = colors.onSurface.copy(alpha = 0.66f),
            modifier = Modifier.padding(top = 6.dp, bottom = 18.dp)
        )

        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PosterPreview(coverUrl)
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title.ifBlank { "Название фильма" },
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = category.trim().ifBlank { "Без категории" },
                        color = colors.onSurface.copy(alpha = 0.66f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = "★ ${rating.takeIf { it > 0 } ?: "—"}",
                        color = Color(0xFFF59E0B),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Название фильма") },
                    singleLine = true,
                    colors = movieTextFieldColors(),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        keyboardType = KeyboardType.Text
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = releaseYear,
                    onValueChange = { releaseYear = it.filter(Char::isDigit).take(4) },
                    label = { Text("Год выпуска") },
                    singleLine = true,
                    colors = movieTextFieldColors(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                )
                OutlinedTextField(
                    value = director,
                    onValueChange = { director = it },
                    label = { Text("Режиссёр") },
                    singleLine = true,
                    colors = movieTextFieldColors(),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        keyboardType = KeyboardType.Text
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                )
                OutlinedTextField(
                    value = coverUrl,
                    onValueChange = { coverUrl = it },
                    label = { Text("URL обложки") },
                    singleLine = true,
                    colors = movieTextFieldColors(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Категория") },
                    singleLine = true,
                    colors = movieTextFieldColors(),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        keyboardType = KeyboardType.Text
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                )

                Text(
                    "Быстрые категории",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 18.dp)
                )
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CategorySuggestions.forEach { option ->
                        SelectableChip(
                            text = option,
                            selected = category.trim().equals(option, ignoreCase = true),
                            onClick = { category = option }
                        )
                    }
                }

                Text(
                    "Статус просмотра",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 18.dp)
                )
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WatchStatus.entries.forEach { option ->
                        SelectableChip(
                            text = option.label,
                            selected = status == option,
                            onClick = { status = option }
                        )
                    }
                }

                Text("Оценка", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 18.dp))
                RatingStars(
                    rating = rating,
                    onRatingChange = { if (!isSaving) rating = it }
                )

                if (saveState is SaveMovieState.Error) {
                    Surface(
                        color = Color(0xFFFEE2E2),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Text(
                            text = saveState.message,
                            color = Color(0xFF991B1B),
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }

                Row(modifier = Modifier.padding(top = 20.dp)) {
                    Button(
                        onClick = onBack,
                        enabled = !isSaving,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE5E7EB),
                            contentColor = Color(0xFF111827)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Отмена")
                    }
                    Button(
                        onClick = {
                            onSaved(
                                title.ifBlank { "Без названия" },
                                releaseYear.toIntOrNull() ?: 2000,
                                director.ifBlank { null },
                                coverUrl.ifBlank { null },
                                category.trim().ifBlank { "Без категории" },
                                status,
                                rating.takeIf { it > 0 }
                            )
                        },
                        enabled = !isSaving,
                        colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(start = 10.dp)
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(22.dp)
                            )
                        } else {
                            Text("Сохранить")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BackArrowIcon() {
    val color = MaterialTheme.colorScheme.onSurface
    Canvas(modifier = Modifier.size(24.dp)) {
        val strokeWidth = 2.6.dp.toPx()
        drawLine(
            color = color,
            start = Offset(size.width * 0.76f, size.height * 0.5f),
            end = Offset(size.width * 0.24f, size.height * 0.5f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.24f, size.height * 0.5f),
            end = Offset(size.width * 0.44f, size.height * 0.3f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.24f, size.height * 0.5f),
            end = Offset(size.width * 0.44f, size.height * 0.7f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun PosterPreview(coverUrl: String) {
    val colors = MaterialTheme.colorScheme
    val normalizedUrl = normalizeImageUrl(coverUrl)
    if (normalizedUrl.isNullOrBlank()) {
        Box(
            modifier = Modifier
                .size(width = 82.dp, height = 116.dp)
                .clip(RoundedCornerShape(18.dp))
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
                .size(width = 82.dp, height = 116.dp)
                .clip(RoundedCornerShape(18.dp))
        )
    }
}

@Composable
private fun SelectableChip(text: String, selected: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Surface(
        color = if (selected) colors.primary else colors.surfaceVariant,
        shape = RoundedCornerShape(999.dp),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            color = if (selected) colors.onPrimary else colors.primary,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
        )
    }
}

@Composable
private fun RatingStars(rating: Int, onRatingChange: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(top = 8.dp)
    ) {
        (1..10).forEach { value ->
            Text(
                text = if (value <= rating) "★" else "☆",
                color = if (value <= rating) Color(0xFFF59E0B) else Color(0xFF9CA3AF),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(end = 4.dp)
                    .clickable { onRatingChange(value) }
            )
        }
    }
}
