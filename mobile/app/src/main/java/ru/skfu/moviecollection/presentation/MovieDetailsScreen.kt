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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ru.skfu.moviecollection.model.MovieDto

@Composable
fun MovieDetailsScreen(
    movie: MovieDto,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onComplaintSubmit: (String, String, (String) -> Unit) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    var complaintDialogVisible by remember { mutableStateOf(false) }
    var complaintReason by remember { mutableStateOf("Ошибка в карточке фильма") }
    var complaintDescription by remember { mutableStateOf("") }
    var complaintMessage by remember { mutableStateOf<String?>(null) }
    if (complaintDialogVisible) {
        AlertDialog(
            onDismissRequest = { complaintDialogVisible = false },
            title = { Text("Жалоба на фильм") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Администратор увидит фильм, твою учетную запись и описание проблемы.",
                        color = colors.onSurface.copy(alpha = 0.68f)
                    )
                    OutlinedTextField(
                        value = complaintReason,
                        onValueChange = { complaintReason = it },
                        label = { Text("Причина") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = complaintDescription,
                        onValueChange = { complaintDescription = it },
                        label = { Text("Что не так") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onComplaintSubmit(complaintReason, complaintDescription) { result ->
                            complaintMessage = result
                            complaintDialogVisible = false
                            complaintDescription = ""
                        }
                    },
                    enabled = complaintReason.isNotBlank() && complaintDescription.isNotBlank()
                ) {
                    Text("Отправить")
                }
            },
            dismissButton = {
                TextButton(onClick = { complaintDialogVisible = false }) {
                    Text("Отмена")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(colors.background, colors.surface, colors.surfaceVariant)))
                .statusBarsPadding()
                .padding(start = 22.dp, end = 22.dp, top = 42.dp, bottom = 24.dp)
        ) {
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.surfaceVariant,
                    contentColor = colors.onSurface
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
                        color = colors.onSurface,
                        style = MaterialTheme.typography.headlineLarge,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${movie.releaseYear} • ${movie.director ?: "режиссёр не указан"}",
                        color = colors.onSurface.copy(alpha = 0.66f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = movie.category?.trim()?.ifBlank { "Без категории" } ?: "Без категории",
                        color = colors.tertiary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
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
                        colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Редактировать")
                    }
                    Button(
                        onClick = onDelete,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Удалить")
                    }
                }
                OutlinedButton(
                    onClick = { complaintDialogVisible = true },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    Text("Пожаловаться")
                }
                complaintMessage?.let { message ->
                    Text(
                        text = message,
                        color = colors.primary,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun Poster(coverUrl: String?, width: Int, height: Int) {
    val colors = MaterialTheme.colorScheme
    val normalizedUrl = normalizeImageUrl(coverUrl)
    if (normalizedUrl.isNullOrBlank()) {
        Box(
            modifier = Modifier
                .size(width = width.dp, height = height.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Brush.linearGradient(listOf(colors.surfaceVariant, colors.secondary))),
            contentAlignment = Alignment.Center
        ) {
            Text("🎬", style = MaterialTheme.typography.headlineLarge)
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
                .clip(RoundedCornerShape(22.dp))
        )
    }
}

@Composable
private fun InfoChip(label: String, value: String) {
    val colors = MaterialTheme.colorScheme
    Surface(color = colors.surfaceVariant, shape = RoundedCornerShape(18.dp)) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            Text(label, color = colors.onSurface.copy(alpha = 0.66f))
            Text(value, color = colors.primary, style = MaterialTheme.typography.titleLarge)
        }
    }
}
