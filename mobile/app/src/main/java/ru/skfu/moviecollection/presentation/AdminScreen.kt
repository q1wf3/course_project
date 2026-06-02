package ru.skfu.moviecollection.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import retrofit2.HttpException
import ru.skfu.moviecollection.api_client.AdminApi
import ru.skfu.moviecollection.api_client.AdminStatsResponse
import ru.skfu.moviecollection.api_client.AdminUserResponse
import ru.skfu.moviecollection.model.MovieDto

@Composable
fun AdminScreen(
    adminApi: AdminApi,
    token: String,
    currentUserId: String,
    onOpenCollection: () -> Unit,
    onLogout: () -> Unit
) {
    val bearerToken = "Bearer $token"
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }
    var actionInProgress by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    var stats by remember { mutableStateOf<AdminStatsResponse?>(null) }
    var users by remember { mutableStateOf(emptyList<AdminUserResponse>()) }
    var selectedUser by remember { mutableStateOf<AdminUserResponse?>(null) }
    var selectedMovies by remember { mutableStateOf(emptyList<MovieDto>()) }
    var deleteCandidate by remember { mutableStateOf<AdminUserResponse?>(null) }
    val colors = MaterialTheme.colorScheme

    suspend fun refreshDashboard() {
        stats = adminApi.stats(bearerToken)
        users = adminApi.users(bearerToken)
        selectedUser = selectedUser?.let { selected ->
            users.firstOrNull { it.id == selected.id }
        }
    }

    suspend fun loadUserMovies(user: AdminUserResponse) {
        selectedUser = user
        selectedMovies = adminApi.userMovies(bearerToken, user.id)
    }

    LaunchedEffect(token) {
        isLoading = true
        error = null
        try {
            refreshDashboard()
        } catch (exception: Exception) {
            error = exception.toAdminMessage()
        } finally {
            isLoading = false
        }
    }

    deleteCandidate?.let { user ->
        AlertDialog(
            onDismissRequest = { deleteCandidate = null },
            title = { Text("Удалить пользователя") },
            text = { Text("Аккаунт ${user.email} и его коллекция будут удалены.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        deleteCandidate = null
                        scope.launch {
                            actionInProgress = true
                            error = null
                            try {
                                adminApi.deleteUser(bearerToken, user.id)
                                if (selectedUser?.id == user.id) {
                                    selectedUser = null
                                    selectedMovies = emptyList()
                                }
                                refreshDashboard()
                                message = "Пользователь удален"
                            } catch (exception: Exception) {
                                error = exception.toAdminMessage()
                            } finally {
                                actionInProgress = false
                            }
                        }
                    }
                ) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteCandidate = null }) {
                    Text("Отмена")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .padding(horizontal = 18.dp, vertical = 22.dp)
    ) {
        AdminHeader(
            actionInProgress = actionInProgress,
            onOpenCollection = onOpenCollection,
            onRefresh = {
                scope.launch {
                    actionInProgress = true
                    error = null
                    try {
                        refreshDashboard()
                        message = "Данные обновлены"
                    } catch (exception: Exception) {
                        error = exception.toAdminMessage()
                    } finally {
                        actionInProgress = false
                    }
                }
            },
            onLogout = onLogout
        )

        when {
            isLoading -> CircularProgressIndicator(
                color = Color(0xFF6D28D9),
                modifier = Modifier.padding(top = 18.dp)
            )
            error != null -> Text(
                error.orEmpty(),
                color = Color(0xFFDC2626),
                modifier = Modifier.padding(top = 12.dp)
            )
            else -> {
                message?.let {
                    Text(it, color = Color(0xFF0F766E), modifier = Modifier.padding(top = 8.dp))
                }
                stats?.let { AdminStats(it) }
                LazyColumn(modifier = Modifier.weight(1f)) {
                    item {
                        Text(
                            "Учетные записи",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(top = 6.dp, bottom = 10.dp)
                        )
                    }
                    items(users, key = { it.id }) { user ->
                        UserCard(
                            user = user,
                            isCurrentUser = user.id == currentUserId,
                            actionInProgress = actionInProgress,
                            isSelected = selectedUser?.id == user.id,
                            onSelect = {
                                scope.launch {
                                    actionInProgress = true
                                    error = null
                                    try {
                                        loadUserMovies(user)
                                    } catch (exception: Exception) {
                                        error = exception.toAdminMessage()
                                    } finally {
                                        actionInProgress = false
                                    }
                                }
                            },
                            onChangeRole = {
                                scope.launch {
                                    actionInProgress = true
                                    error = null
                                    try {
                                        val nextRole = if (user.role == "ADMIN") "USER" else "ADMIN"
                                        adminApi.changeRole(bearerToken, user.id, nextRole)
                                        refreshDashboard()
                                        message = "Роль пользователя обновлена"
                                    } catch (exception: Exception) {
                                        error = exception.toAdminMessage()
                                    } finally {
                                        actionInProgress = false
                                    }
                                }
                            },
                            onDelete = { deleteCandidate = user }
                        )
                    }
                    selectedUser?.let { user ->
                        item {
                            UserMoviesBlock(user, selectedMovies)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminHeader(
    actionInProgress: Boolean,
    onOpenCollection: () -> Unit,
    onRefresh: () -> Unit,
    onLogout: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text("Панель администратора", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Пользователи, роли и коллекции под рукой.",
                color = colors.onSurface.copy(alpha = 0.66f),
                modifier = Modifier.padding(top = 6.dp, bottom = 16.dp)
            )
            Button(
                onClick = onRefresh,
                enabled = !actionInProgress,
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Обновить")
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(top = 10.dp)
            ) {
                OutlinedButton(
                    onClick = onOpenCollection,
                    enabled = !actionInProgress,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Коллекция")
                }
                OutlinedButton(
                    onClick = onLogout,
                    enabled = !actionInProgress,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Выйти")
                }
            }
        }
    }
}

@Composable
private fun AdminStats(stats: AdminStatsResponse) {
    val colors = MaterialTheme.colorScheme
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp, bottom = 12.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            StatCell("Пользователи", stats.usersCount.toString())
            StatCell("Фильмы", stats.moviesCount.toString())
            StatCell("Записи коллекций", stats.collectionItemsCount.toString())
        }
    }
}

@Composable
private fun StatCell(label: String, value: String, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Column(modifier = modifier) {
        Text(value, style = MaterialTheme.typography.titleLarge)
        Text(label, color = colors.onSurface.copy(alpha = 0.66f), modifier = Modifier.padding(top = 2.dp))
    }
}

@Composable
private fun UserCard(
    user: AdminUserResponse,
    isCurrentUser: Boolean,
    actionInProgress: Boolean,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onChangeRole: () -> Unit,
    onDelete: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val borderColor = if (isSelected) colors.primary else Color.Transparent
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) colors.surfaceVariant else colors.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .clickable(enabled = !actionInProgress, onClick = onSelect)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(user.email, style = MaterialTheme.typography.titleMedium, color = borderColor.takeIf { isSelected } ?: Color.Unspecified)
            Text(
                "Роль: ${user.role} · фильмов: ${user.moviesCount}",
                color = colors.onSurface.copy(alpha = 0.66f),
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onSelect,
                    enabled = !actionInProgress,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Фильмы")
                }
                OutlinedButton(
                    onClick = onChangeRole,
                    enabled = !actionInProgress && !isCurrentUser,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (user.role == "ADMIN") "Сделать USER" else "Сделать ADMIN")
                }
                OutlinedButton(
                    onClick = onDelete,
                    enabled = !actionInProgress && !isCurrentUser,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Удалить")
                }
            }
        }
    }
}

@Composable
private fun UserMoviesBlock(user: AdminUserResponse, movies: List<MovieDto>) {
    val colors = MaterialTheme.colorScheme
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 18.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Коллекция ${user.email}", style = MaterialTheme.typography.titleLarge)
            if (movies.isEmpty()) {
                Text("Фильмов пока нет", color = colors.onSurface.copy(alpha = 0.66f), modifier = Modifier.padding(top = 8.dp))
            } else {
                movies.forEach { movie ->
                    Text(
                        "${movie.title} (${movie.releaseYear}) · ${movie.status.label}",
                        color = colors.onSurface.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

private fun Throwable.toAdminMessage(): String {
    return when (this) {
        is HttpException -> when (code()) {
            400 -> "Действие отклонено backend. Проверь выбранного пользователя или роль."
            401 -> "Нужно заново войти в аккаунт администратора."
            403 -> "Этот аккаунт не имеет роли ADMIN."
            404 -> "Пользователь или endpoint не найден."
            else -> "Ошибка backend: HTTP ${code()}."
        }
        else -> message ?: "Не удалось загрузить админку"
    }
}
