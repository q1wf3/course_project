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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ru.skfu.moviecollection.api_client.ComplaintResponse
import ru.skfu.moviecollection.model.MovieDto

@Composable
fun ProfileScreen(
    email: String,
    isAdmin: Boolean,
    movies: List<MovieDto>,
    profileName: String,
    avatarUrl: String,
    bio: String,
    favoriteGenre: String,
    notifications: List<ComplaintResponse>,
    readNotificationIds: Set<String>,
    deletedNotificationIds: Set<String>,
    onRefreshNotifications: () -> Unit,
    onNotificationsViewed: (Set<String>) -> Unit,
    onDeleteNotification: (String) -> Unit,
    onDeleteAllNotifications: (Set<String>) -> Unit,
    onProfileChange: (String, String, String, String) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAdmin: () -> Unit,
    onLogout: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val watchedCount = movies.count { it.status.name == "WATCHED" }
    val plannedCount = movies.count { it.status.name == "PLANNED" }
    val averageRating = movies.mapNotNull { it.rating }.average().takeIf { !it.isNaN() }
    var isEditing by remember { mutableStateOf(false) }
    var notificationsVisible by remember { mutableStateOf(false) }
    val visibleNotifications = notifications.filterNot { it.id in deletedNotificationIds }
    val answeredNotificationIds = visibleNotifications
        .filter { it.hasUserVisibleAnswer }
        .map { it.id }
        .toSet()
    val unreadNotifications = answeredNotificationIds.count { it !in readNotificationIds }

    if (notificationsVisible) {
        NotificationsDialog(
            notifications = visibleNotifications,
            onDeleteNotification = onDeleteNotification,
            onDeleteAllNotifications = {
                onDeleteAllNotifications(visibleNotifications.map { it.id }.toSet())
            },
            onDismiss = { notificationsVisible = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 20.dp)
    ) {
        Text("Профиль", style = MaterialTheme.typography.headlineLarge)
        Text(
            "Личные данные и быстрые действия.",
            color = colors.onSurface.copy(alpha = 0.66f),
            modifier = Modifier.padding(top = 6.dp, bottom = 18.dp)
        )

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(20.dp)
            ) {
                Avatar(avatarUrl = avatarUrl, profileName = profileName)
                Text(
                    profileName.ifBlank { "Киноман" },
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 12.dp)
                )
                Text(email, color = colors.onSurface.copy(alpha = 0.66f))
                Text(
                    bio.ifBlank { "Любимые фильмы, планы и оценки в одном месте." },
                    color = colors.onSurface.copy(alpha = 0.74f),
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(top = 12.dp)
        ) {
            ProfileStat("Всего", movies.size.toString(), Modifier.weight(1f))
            ProfileStat("Просмотрено", watchedCount.toString(), Modifier.weight(1f))
            ProfileStat("План", plannedCount.toString(), Modifier.weight(1f))
        }
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Средняя оценка", color = colors.onSurface.copy(alpha = 0.66f))
                Text(
                    averageRating?.let { "%.1f / 10".format(it) } ?: "Пока нет оценок",
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.tertiary,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    "Любимый жанр: ${favoriteGenre.ifBlank { "не указан" }}",
                    color = colors.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }


        OutlinedButton(
            onClick = {
                onRefreshNotifications()
                if (answeredNotificationIds.isNotEmpty()) {
                    onNotificationsViewed(answeredNotificationIds)
                }
                notificationsVisible = true
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            Text("🔔 Уведомления" + if (unreadNotifications > 0) " ($unreadNotifications)" else "")
        }

        OutlinedButton(
            onClick = { isEditing = !isEditing },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            Text(if (isEditing) "✕ Скрыть редактирование" else "✎ Редактировать профиль")
        }

        OutlinedButton(
            onClick = onOpenSettings,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            Text("⚙ Настройки")
        }

        if (isEditing) {
            ProfileEditor(
                profileName = profileName,
                avatarUrl = avatarUrl,
                bio = bio,
                favoriteGenre = favoriteGenre,
                onProfileChange = onProfileChange
            )
        }

        if (isAdmin) {
            OutlinedButton(
                onClick = onOpenAdmin,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                Text("Открыть админку")
            }
        }
        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color(0xFFEF4444)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            Text("Выйти")
        }
        Spacer(modifier = Modifier.height(18.dp))
    }
}

@Composable
private fun Avatar(avatarUrl: String, profileName: String) {
    val colors = MaterialTheme.colorScheme
    val normalizedUrl = normalizeImageUrl(avatarUrl)
    if (normalizedUrl.isNullOrBlank()) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(colors.primary, colors.secondary))),
            contentAlignment = Alignment.Center
        ) {
            Text(
                profileName.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "M",
                style = MaterialTheme.typography.headlineLarge,
                color = colors.onPrimary
            )
        }
    } else {
        val request = ImageRequest.Builder(LocalContext.current)
            .data(normalizedUrl)
            .crossfade(true)
            .allowHardware(false)
            .build()
        AsyncImage(
            model = request,
            contentDescription = "Аватар",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
        )
    }
}

@Composable
private fun ProfileStat(label: String, value: String, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(value, style = MaterialTheme.typography.titleLarge)
            Text(label, color = colors.onSurface.copy(alpha = 0.66f))
        }
    }
}

@Composable
private fun ProfileEditor(
    profileName: String,
    avatarUrl: String,
    bio: String,
    favoriteGenre: String,
    onProfileChange: (String, String, String, String) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Редактирование", style = MaterialTheme.typography.titleLarge)
            OutlinedTextField(
                value = profileName,
                onValueChange = { onProfileChange(it, avatarUrl, bio, favoriteGenre) },
                label = { Text("Имя") },
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
                value = avatarUrl,
                onValueChange = { onProfileChange(profileName, it, bio, favoriteGenre) },
                label = { Text("URL аватарки") },
                singleLine = true,
                colors = movieTextFieldColors(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            )
            OutlinedTextField(
                value = favoriteGenre,
                onValueChange = { onProfileChange(profileName, avatarUrl, bio, it) },
                label = { Text("Любимый жанр") },
                singleLine = true,
                colors = movieTextFieldColors(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    keyboardType = KeyboardType.Text
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            )
            OutlinedTextField(
                value = bio,
                onValueChange = { onProfileChange(profileName, avatarUrl, it, favoriteGenre) },
                label = { Text("О себе") },
                minLines = 2,
                colors = movieTextFieldColors(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    keyboardType = KeyboardType.Text
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            )
        }
    }
}

@Composable
private fun NotificationsDialog(
    notifications: List<ComplaintResponse>,
    onDeleteNotification: (String) -> Unit,
    onDeleteAllNotifications: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Уведомления") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .height(360.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (notifications.isEmpty()) {
                    Text(
                        "Пока нет уведомлений. Когда администратор ответит на жалобу, ответ появится здесь.",
                        color = colors.onSurface.copy(alpha = 0.72f)
                    )
                } else {
                    OutlinedButton(
                        onClick = onDeleteAllNotifications,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Удалить все уведомления")
                    }
                    notifications.forEach { notification ->
                        NotificationCard(
                            notification = notification,
                            onDelete = { onDeleteNotification(notification.id) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Закрыть")
            }
        }
    )
}

@Composable
private fun NotificationCard(
    notification: ComplaintResponse,
    onDelete: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(notification.movieTitle, style = MaterialTheme.typography.titleMedium)
            Text(
                "Жалоба: ${notification.reason}",
                color = colors.onSurface.copy(alpha = 0.72f),
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                "Статус: ${notification.status.notificationLabel}",
                color = colors.primary,
                modifier = Modifier.padding(top = 6.dp)
            )
            val adminMessage = notification.adminComment?.takeIf { it.isNotBlank() }
            Text(
                adminMessage ?: "Администратор пока не написал ответ.",
                color = colors.onSurface.copy(alpha = if (adminMessage == null) 0.58f else 0.86f),
                modifier = Modifier.padding(top = 8.dp)
            )
            TextButton(
                onClick = onDelete,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 6.dp)
            ) {
                Text("Удалить")
            }
        }
    }
}

private val ComplaintResponse.hasUserVisibleAnswer: Boolean
    get() = adminComment?.isNotBlank() == true || status != "NEW"

private val String.notificationLabel: String
    get() = when (this) {
        "NEW" -> "ожидает рассмотрения"
        "IN_PROGRESS" -> "администратор проверяет"
        "RESOLVED" -> "закрыта"
        "REJECTED" -> "отклонена"
        else -> this
    }
