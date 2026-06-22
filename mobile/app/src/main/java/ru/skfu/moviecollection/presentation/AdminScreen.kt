package ru.skfu.moviecollection.presentation

import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import retrofit2.HttpException
import ru.skfu.moviecollection.api_client.AdminApi
import ru.skfu.moviecollection.api_client.AdminStatsResponse
import ru.skfu.moviecollection.api_client.AdminUserResponse
import ru.skfu.moviecollection.api_client.ComplaintResponse
import ru.skfu.moviecollection.api_client.UpdateComplaintStatusRequest
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
    var complaints by remember { mutableStateOf(emptyList<ComplaintResponse>()) }
    var users by remember { mutableStateOf(emptyList<AdminUserResponse>()) }
    var selectedUser by remember { mutableStateOf<AdminUserResponse?>(null) }
    var selectedMovies by remember { mutableStateOf(emptyList<MovieDto>()) }
    var deleteCandidate by remember { mutableStateOf<AdminUserResponse?>(null) }
    var complaintAction by remember { mutableStateOf<ComplaintAction?>(null) }
    val colors = MaterialTheme.colorScheme

    suspend fun refreshDashboard() {
        stats = adminApi.stats(bearerToken)
        complaints = adminApi.complaints(bearerToken)
        users = adminApi.users(bearerToken)
        selectedUser = selectedUser?.let { selected -> users.firstOrNull { it.id == selected.id } }
    }

    suspend fun loadUserMovies(user: AdminUserResponse) {
        selectedUser = user
        selectedMovies = adminApi.userMovies(bearerToken, user.id)
    }

    suspend fun updateComplaint(action: ComplaintAction, comment: String) {
        adminApi.updateComplaintStatus(
            bearerToken,
            action.complaint.id,
            UpdateComplaintStatusRequest(status = action.status, adminComment = comment.trim().ifBlank { action.defaultComment })
        )
        refreshDashboard()
        message = when (action.status) {
            "IN_PROGRESS" -> "Жалоба взята в работу"
            "RESOLVED" -> "Жалоба закрыта"
            "REJECTED" -> "Жалоба отклонена"
            else -> "Статус жалобы обновлен"
        }
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
            text = { Text("Аккаунт ${user.email}, его коллекция и жалобы будут удалены.") },
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
                ) { Text("Удалить") }
            },
            dismissButton = { TextButton(onClick = { deleteCandidate = null }) { Text("Отмена") } }
        )
    }

    complaintAction?.let { action ->
        ComplaintCommentDialog(
            action = action,
            onDismiss = { complaintAction = null },
            onConfirm = { comment ->
                complaintAction = null
                scope.launch {
                    actionInProgress = true
                    error = null
                    try {
                        updateComplaint(action, comment)
                    } catch (exception: Exception) {
                        error = exception.toAdminMessage()
                    } finally {
                        actionInProgress = false
                    }
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .padding(horizontal = 18.dp, vertical = 22.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
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
        }

        when {
            isLoading -> item {
                CircularProgressIndicator(color = colors.primary, modifier = Modifier.padding(top = 8.dp))
            }
            error != null -> item {
                Text(error.orEmpty(), color = Color(0xFFDC2626))
            }
            else -> {
                message?.let { text ->
                    item { Text(text, color = colors.primary) }
                }
                stats?.let { currentStats ->
                    item { AdminStats(currentStats) }
                    item {
                        AdminReportCard(
                            stats = currentStats,
                            complaints = complaints,
                            users = users
                        )
                    }
                }
                item { SectionTitle("Жалобы пользователей") }
                if (complaints.isEmpty()) {
                    item { EmptyAdminCard("Жалоб пока нет") }
                } else {
                    items(complaints, key = { it.id }) { complaint ->
                        ComplaintCard(
                            complaint = complaint,
                            actionInProgress = actionInProgress,
                            onStart = {
                                complaintAction = ComplaintAction(
                                    complaint = complaint,
                                    status = "IN_PROGRESS",
                                    title = "Взять жалобу в работу",
                                    defaultComment = "Администратор проверяет обращение"
                                )
                            },
                            onResolve = {
                                complaintAction = ComplaintAction(
                                    complaint = complaint,
                                    status = "RESOLVED",
                                    title = "Закрыть жалобу",
                                    defaultComment = "Проверено и закрыто"
                                )
                            },
                            onReject = {
                                complaintAction = ComplaintAction(
                                    complaint = complaint,
                                    status = "REJECTED",
                                    title = "Отклонить жалобу",
                                    defaultComment = "Нарушение не подтверждено"
                                )
                            }
                        )
                    }
                }
                item { SectionTitle("Учетные записи") }
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
                    item { UserMoviesBlock(user, selectedMovies) }
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
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text("Панель администратора", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Жалобы, пользователи, роли и коллекции под рукой.",
                color = colors.onSurface.copy(alpha = 0.66f),
                modifier = Modifier.padding(top = 6.dp, bottom = 16.dp)
            )
            Button(
                onClick = onRefresh,
                enabled = !actionInProgress,
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Обновить") }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 10.dp)) {
                OutlinedButton(onClick = onOpenCollection, enabled = !actionInProgress, shape = RoundedCornerShape(16.dp), modifier = Modifier.weight(1f)) {
                    Text("Коллекция")
                }
                OutlinedButton(onClick = onLogout, enabled = !actionInProgress, shape = RoundedCornerShape(16.dp), modifier = Modifier.weight(1f)) {
                    Text("Выйти")
                }
            }
        }
    }
}

@Composable
private fun AdminStats(stats: AdminStatsResponse) {
    val colors = MaterialTheme.colorScheme
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = colors.surface), modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(16.dp)) {
            StatCell("Открытые жалобы", stats.openComplaintsCount.toString())
            StatCell("Пользователи", stats.usersCount.toString())
            StatCell("Фильмы", stats.moviesCount.toString())
            StatCell("Записи коллекций", stats.collectionItemsCount.toString())
        }
    }
}

@Composable
private fun AdminReportCard(
    stats: AdminStatsResponse,
    complaints: List<ComplaintResponse>,
    users: List<AdminUserResponse>
) {
    val colors = MaterialTheme.colorScheme
    val context = LocalContext.current
    val reportText = remember(stats, complaints, users) { buildReportText(stats, complaints, users) }
    Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = colors.surface), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Отчет администратора", style = MaterialTheme.typography.titleLarge)
            Text(
                "Сводка по пользователям, коллекциям и жалобам.",
                color = colors.onSurface.copy(alpha = 0.66f),
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )
            AdminBarChart(stats = stats, complaints = complaints)
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "Отчет Movie Collection")
                        putExtra(Intent.EXTRA_TEXT, reportText)
                    }
                    context.startActivity(Intent.createChooser(intent, "Выгрузить отчет"))
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp)
            ) { Text("Выгрузить отчет") }
        }
    }
}

@Composable
private fun AdminBarChart(stats: AdminStatsResponse, complaints: List<ComplaintResponse>) {
    val colors = MaterialTheme.colorScheme
    val chartItems = listOf(
        ChartItem("Откр.", stats.openComplaintsCount.toFloat(), colors.primary),
        ChartItem("Новые", complaints.count { it.status == "NEW" }.toFloat(), Color(0xFFF59E0B)),
        ChartItem("Раб.", complaints.count { it.status == "IN_PROGRESS" }.toFloat(), Color(0xFF3B82F6)),
        ChartItem("Закр.", complaints.count { it.status == "RESOLVED" }.toFloat(), Color(0xFF22C55E)),
        ChartItem("Откл.", complaints.count { it.status == "REJECTED" }.toFloat(), Color(0xFFEF4444))
    )
    val maxValue = chartItems.maxOf { it.value }.coerceAtLeast(1f)
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .background(colors.surfaceVariant, RoundedCornerShape(18.dp))
            .padding(12.dp)
    ) {
        val gap = size.width * 0.045f
        val barWidth = (size.width - gap * (chartItems.size + 1)) / chartItems.size
        val chartHeight = size.height * 0.72f
        chartItems.forEachIndexed { index, item ->
            val left = gap + index * (barWidth + gap)
            val barHeight = (item.value / maxValue) * chartHeight
            val top = size.height - barHeight - 26f
            drawRoundRect(
                color = item.color.copy(alpha = 0.9f),
                topLeft = Offset(left, top),
                size = Size(barWidth, barHeight.coerceAtLeast(5f))
            )
        }
    }
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(top = 6.dp)) {
        chartItems.forEach { item ->
            Text(item.label, color = colors.onSurface.copy(alpha = 0.68f), style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 6.dp))
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
private fun ComplaintCard(
    complaint: ComplaintResponse,
    actionInProgress: Boolean,
    onStart: () -> Unit,
    onResolve: () -> Unit,
    onReject: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = colors.surface), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(complaint.movieTitle, style = MaterialTheme.typography.titleMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text("${complaint.reporterEmail} · ${complaint.status.label}", color = colors.onSurface.copy(alpha = 0.66f), modifier = Modifier.padding(top = 4.dp))
            Text(complaint.reason, color = colors.primary, modifier = Modifier.padding(top = 10.dp))
            Text(complaint.description, color = colors.onSurface.copy(alpha = 0.82f), modifier = Modifier.padding(top = 6.dp))
            complaint.adminComment?.takeIf { it.isNotBlank() }?.let { comment ->
                Text("Сообщение пользователю: $comment", color = colors.onSurface.copy(alpha = 0.66f), modifier = Modifier.padding(top = 8.dp))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 12.dp)) {
                OutlinedButton(onClick = onStart, enabled = !actionInProgress && complaint.status == "NEW", shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1f)) {
                    Text("В работу")
                }
                OutlinedButton(onClick = onResolve, enabled = !actionInProgress && complaint.status != "RESOLVED", shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1f)) {
                    Text("Закрыть")
                }
                OutlinedButton(onClick = onReject, enabled = !actionInProgress && complaint.status != "REJECTED", shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1f)) {
                    Text("Отклонить")
                }
            }
        }
    }
}

@Composable
private fun ComplaintCommentDialog(
    action: ComplaintAction,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var comment by remember(action) { mutableStateOf(action.defaultComment) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(action.title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(action.complaint.movieTitle, style = MaterialTheme.typography.titleMedium)
                Text(action.complaint.description, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f))
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Сообщение пользователю") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = { TextButton(onClick = { onConfirm(comment) }) { Text("Сохранить") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}

@Composable
private fun EmptyAdminCard(text: String) {
    val colors = MaterialTheme.colorScheme
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = colors.surface), modifier = Modifier.fillMaxWidth()) {
        Text(text, color = colors.onSurface.copy(alpha = 0.66f), modifier = Modifier.padding(16.dp))
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
    val titleColor = if (isSelected) colors.primary else colors.onSurface
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) colors.surfaceVariant else colors.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !actionInProgress, onClick = onSelect)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(user.email, style = MaterialTheme.typography.titleMedium, color = titleColor)
            Text("Роль: ${user.role} · фильмов: ${user.moviesCount}", color = colors.onSurface.copy(alpha = 0.66f), modifier = Modifier.padding(top = 4.dp, bottom = 12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onSelect, enabled = !actionInProgress, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) { Text("Фильмы") }
                OutlinedButton(onClick = onChangeRole, enabled = !actionInProgress && !isCurrentUser, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Text(if (user.role == "ADMIN") "Сделать обычным" else "Сделать админом")
                }
                OutlinedButton(onClick = onDelete, enabled = !actionInProgress && !isCurrentUser, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) { Text("Удалить аккаунт") }
            }
        }
    }
}

@Composable
private fun UserMoviesBlock(user: AdminUserResponse, movies: List<MovieDto>) {
    val colors = MaterialTheme.colorScheme
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = colors.surface), modifier = Modifier.fillMaxWidth().padding(bottom = 18.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Коллекция ${user.email}", style = MaterialTheme.typography.titleLarge)
            if (movies.isEmpty()) {
                Text("Фильмов пока нет", color = colors.onSurface.copy(alpha = 0.66f), modifier = Modifier.padding(top = 8.dp))
            } else {
                movies.forEach { movie ->
                    Text("${movie.title} (${movie.releaseYear}) · ${movie.status.label}", color = colors.onSurface.copy(alpha = 0.8f), modifier = Modifier.padding(top = 8.dp))
                }
            }
        }
    }
}

private fun buildReportText(
    stats: AdminStatsResponse,
    complaints: List<ComplaintResponse>,
    users: List<AdminUserResponse>
): String {
    val byStatus = complaints.groupingBy { it.status }.eachCount()
    return buildString {
        appendLine("Отчет Movie Collection")
        appendLine()
        appendLine("Пользователей: ${stats.usersCount}")
        appendLine("Фильмов: ${stats.moviesCount}")
        appendLine("Записей коллекций: ${stats.collectionItemsCount}")
        appendLine("Открытых жалоб: ${stats.openComplaintsCount}")
        appendLine()
        appendLine("Жалобы:")
        appendLine("Новые: ${byStatus["NEW"] ?: 0}")
        appendLine("В работе: ${byStatus["IN_PROGRESS"] ?: 0}")
        appendLine("Закрытые: ${byStatus["RESOLVED"] ?: 0}")
        appendLine("Отклоненные: ${byStatus["REJECTED"] ?: 0}")
        appendLine()
        appendLine("Пользователи:")
        users.forEach { user -> appendLine("- ${user.email}: ${user.role}, фильмов ${user.moviesCount}") }
        if (complaints.isNotEmpty()) {
            appendLine()
            appendLine("Последние жалобы:")
            complaints.take(10).forEach { complaint ->
                appendLine("- ${complaint.movieTitle}: ${complaint.reason} (${complaint.status.label})")
            }
        }
    }
}

private val String.label: String
    get() = when (this) {
        "NEW" -> "Новая"
        "IN_PROGRESS" -> "В работе"
        "RESOLVED" -> "Закрыта"
        "REJECTED" -> "Отклонена"
        else -> this
    }

private fun Throwable.toAdminMessage(): String {
    return when (this) {
        is HttpException -> when (code()) {
            400 -> "Действие отклонено backend. Проверь выбранные данные."
            401 -> "Нужно заново войти в аккаунт администратора."
            403 -> "Этот аккаунт не имеет роли ADMIN."
            404 -> "Пользователь, жалоба или endpoint не найден."
            else -> "Ошибка backend: HTTP ${code()}."
        }
        else -> message ?: "Не удалось загрузить админку"
    }
}

private data class ComplaintAction(
    val complaint: ComplaintResponse,
    val status: String,
    val title: String,
    val defaultComment: String
)

private data class ChartItem(
    val label: String,
    val value: Float,
    val color: Color
)
