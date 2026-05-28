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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.skfu.moviecollection.model.MovieDto

@Composable
fun MovieDetailsScreen(
    movie: MovieDto,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(Color(0xFF111827), Color(0xFF6D28D9))))
                .statusBarsPadding()
                .padding(start = 22.dp, end = 22.dp, top = 42.dp, bottom = 24.dp)
        ) {
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0x33FFFFFF),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("← Назад")
            }
            Row(
                modifier = Modifier.padding(top = 22.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Poster(movie.coverUrl, width = 104, height = 150)
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = movie.title,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineLarge,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${movie.releaseYear} • ${movie.director ?: "режиссёр не указан"}",
                        color = Color(0xFFEDE9FE),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = movie.category?.trim()?.ifBlank { "Без категории" } ?: "Без категории",
                        color = Color(0xFFFDE68A),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Column(modifier = Modifier.padding(22.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    InfoChip("Статус", movie.status.label)
                    InfoChip("Оценка", movie.rating?.toString() ?: "—")
                }
                Spacer(modifier = Modifier.height(18.dp))
                Text("Рейтинг", style = MaterialTheme.typography.titleLarge)
                Text(
                    text = (1..10).joinToString("") { if (it <= (movie.rating ?: 0)) "★" else "☆" },
                    color = Color(0xFFF59E0B),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(top = 22.dp)
                ) {
                    Button(
                        onClick = onEdit,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D28D9)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Редактировать")
                    }
                    Button(
                        onClick = onDelete,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Удалить")
                    }
                }
            }
        }
    }
}

@Composable
private fun Poster(coverUrl: String?, width: Int, height: Int) {
    if (coverUrl.isNullOrBlank()) {
        Box(
            modifier = Modifier
                .size(width = width.dp, height = height.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Brush.linearGradient(listOf(Color(0xFF6D28D9), Color(0xFFDB2777)))),
            contentAlignment = Alignment.Center
        ) {
            Text("🎬", style = MaterialTheme.typography.headlineLarge)
        }
    } else {
        AsyncImage(
            model = coverUrl,
            contentDescription = "Обложка фильма",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(width = width.dp, height = height.dp)
                .clip(RoundedCornerShape(22.dp))
        )
    }
}

@Composable
private fun InfoChip(label: String, value: String) {
    Surface(color = Color(0xFFEDE9FE), shape = RoundedCornerShape(18.dp)) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            Text(label, color = Color(0xFF6B7280))
            Text(value, color = Color(0xFF5B21B6), style = MaterialTheme.typography.titleLarge)
        }
    }
}
