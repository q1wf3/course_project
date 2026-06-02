package ru.skfu.moviecollection.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import retrofit2.HttpException
import ru.skfu.moviecollection.api_client.AuthApi
import ru.skfu.moviecollection.api_client.AuthRequest
import ru.skfu.moviecollection.api_client.AuthResponse

@Composable
fun LoginScreen(
    authApi: AuthApi,
    onLoggedIn: (AuthResponse) -> Unit
) {
    var mode by remember { mutableStateOf(AuthMode.Login) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatedPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(colors.background, colors.surface, colors.surfaceVariant)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Movie Collection",
                color = colors.onBackground,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Твоя фильмотека с постерами, оценками, оффлайн-кэшем и ролями.",
                color = colors.onSurface.copy(alpha = 0.68f),
                modifier = Modifier.padding(top = 10.dp, bottom = 22.dp)
            )

            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    AuthModePicker(
                        mode = mode,
                        onModeChange = {
                            mode = it
                            error = null
                        }
                    )
                    Text(
                        text = if (mode == AuthMode.Login) "Вход" else "Регистрация",
                        style = MaterialTheme.typography.headlineMedium,
                        color = colors.onSurface,
                        modifier = Modifier.padding(top = 20.dp)
                    )
                    Text(
                        text = if (mode == AuthMode.Login) {
                            "Войди в существующую учетку. Для теста: test@yandex.ru или admin@movie.local."
                        } else {
                            "Создай отдельную учетку, чтобы коллекция сохранялась за тобой."
                        },
                        color = colors.onSurface.copy(alpha = 0.66f),
                        modifier = Modifier.padding(top = 6.dp, bottom = 16.dp)
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            error = null
                        },
                        label = { Text("Email") },
                        singleLine = true,
                        colors = movieTextFieldColors(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            error = null
                        },
                        label = { Text("Пароль") },
                        singleLine = true,
                        colors = movieTextFieldColors(),
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                    )
                    if (mode == AuthMode.Register) {
                        OutlinedTextField(
                            value = repeatedPassword,
                            onValueChange = {
                                repeatedPassword = it
                                error = null
                            },
                            label = { Text("Повторите пароль") },
                            singleLine = true,
                            colors = movieTextFieldColors(),
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                        )
                    }
                    Text(
                        text = "Пароль минимум 6 символов. Для тестовых аккаунтов можно использовать 123456.",
                        color = colors.onSurface.copy(alpha = 0.58f),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    error?.let {
                        Text(
                            text = it,
                            color = Color(0xFFEF4444),
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }
                    Button(
                        onClick = {
                            val validationError = validateLogin(
                                email = email,
                                password = password,
                                repeatedPassword = repeatedPassword,
                                mode = mode
                            )
                            if (validationError != null) {
                                error = validationError
                                return@Button
                            }
                            scope.launch {
                                isLoading = true
                                try {
                                    val request = AuthRequest(email.trim(), password)
                                    val response = if (mode == AuthMode.Login) {
                                        authApi.login(request)
                                    } else {
                                        authApi.register(request)
                                    }
                                    onLoggedIn(response)
                                } catch (exception: Exception) {
                                    error = exception.toUserMessage(mode)
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 18.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = colors.onPrimary, strokeWidth = 2.dp)
                        } else {
                            Text(if (mode == AuthMode.Login) "Войти" else "Создать аккаунт")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AuthModePicker(
    mode: AuthMode,
    onModeChange: (AuthMode) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        AuthModeButton(
            title = "Вход",
            selected = mode == AuthMode.Login,
            onClick = { onModeChange(AuthMode.Login) },
            modifier = Modifier.weight(1f)
        )
        AuthModeButton(
            title = "Регистрация",
            selected = mode == AuthMode.Register,
            onClick = { onModeChange(AuthMode.Register) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun AuthModeButton(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    if (selected) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
            shape = RoundedCornerShape(16.dp),
            modifier = modifier
        ) {
            Text(title)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            shape = RoundedCornerShape(16.dp),
            modifier = modifier
        ) {
            Text(title)
        }
    }
}

private enum class AuthMode {
    Login,
    Register
}

private fun validateLogin(
    email: String,
    password: String,
    repeatedPassword: String,
    mode: AuthMode
): String? {
    if (!email.contains("@") || !email.contains(".")) {
        return "Введите корректный email, например test@yandex.ru."
    }
    if (password.length < 6) {
        return "Пароль слишком короткий. Нужно минимум 6 символов."
    }
    if (mode == AuthMode.Register && password != repeatedPassword) {
        return "Пароли не совпадают."
    }
    return null
}

private fun Throwable.toUserMessage(mode: AuthMode): String {
    return when (this) {
        is HttpException -> when (code()) {
            400 -> if (mode == AuthMode.Register) {
                "Не удалось создать аккаунт. Проверь email и пароль."
            } else {
                "Проверь email и пароль."
            }
            401, 403 -> "Неверный email или пароль."
            404 -> "Backend не нашел нужный endpoint. Проверь запуск сервера."
            409 -> "Такой аккаунт уже есть. Переключись на вход."
            500 -> "Ошибка backend. Посмотри логи сервера в Docker Desktop."
            else -> "Ошибка сервера: HTTP ${code()}."
        }
        else -> "Не удалось подключиться к backend. Проверь, что сервер запущен на localhost:8080."
    }
}
