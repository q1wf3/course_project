package ru.skfu.moviecollection.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import retrofit2.HttpException
import ru.skfu.moviecollection.api_client.AuthApi
import ru.skfu.moviecollection.api_client.AuthRequest

@Composable
fun LoginScreen(
    authApi: AuthApi,
    onLoggedIn: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1E1B4B), Color(0xFF6D28D9), Color(0xFFF8FAFC))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Movie Collection", color = Color.White, style = MaterialTheme.typography.headlineLarge)
            Text(
                text = "Личная фильмотека: статусы, оценки, заметки и быстрый поиск.",
                color = Color(0xFFEDE9FE),
                modifier = Modifier.padding(top = 10.dp, bottom = 28.dp)
            )
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.94f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(20.dp, RoundedCornerShape(28.dp))
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
                    Text("Вход в коллекцию", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "Если аккаунта нет, приложение создаст его автоматически.",
                        color = Color(0xFF6B7280),
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
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                    )
                    Text(
                        text = "Минимум 6 символов. Для проверки можно ввести 123456.",
                        color = Color(0xFF6B7280),
                        modifier = Modifier.padding(top = 6.dp)
                    )
                    error?.let {
                        Text(
                            text = it,
                            color = Color(0xFFDC2626),
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }
                    Button(
                        onClick = {
                            val validationError = validateLogin(email, password)
                            if (validationError != null) {
                                error = validationError
                                return@Button
                            }
                            scope.launch {
                                isLoading = true
                                try {
                                    val request = AuthRequest(email.trim(), password)
                                    val response = try {
                                        authApi.login(request)
                                    } catch (_: Exception) {
                                        authApi.register(request)
                                    }
                                    onLoggedIn(response.token)
                                } catch (exception: Exception) {
                                    error = exception.toUserMessage()
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D28D9)),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 18.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("Продолжить")
                        }
                    }
                }
            }
            Text(
                text = "Фишки: оффлайн-кэш • обложки • категории • рейтинги • Material UI",
                color = Color(0xFF4B5563),
                modifier = Modifier.padding(top = 18.dp)
            )
        }
    }
}

private fun validateLogin(email: String, password: String): String? {
    if (!email.contains("@") || !email.contains(".")) {
        return "Введите корректный email, например test@yandex.ru."
    }
    if (password.length < 6) {
        return "Пароль слишком короткий. Нужно минимум 6 символов."
    }
    return null
}

private fun Throwable.toUserMessage(): String {
    return when (this) {
        is HttpException -> when (code()) {
            400 -> "Проверьте email и пароль. Пароль должен быть минимум 6 символов."
            401, 403 -> "Неверный email или пароль."
            404 -> "Backend не нашел нужный endpoint. Проверьте запуск сервера."
            500 -> "Ошибка backend. Посмотрите лог сервера в PowerShell."
            else -> "Ошибка сервера: HTTP ${code()}."
        }
        else -> "Не удалось подключиться к backend. Проверьте, что сервер запущен на localhost:8080."
    }
}

