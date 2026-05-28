package ru.skfu.moviecollection.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    var offlineCache by remember { mutableStateOf(true) }
    var smartSorting by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(18.dp)
    ) {
        Text("Настройки", style = MaterialTheme.typography.headlineLarge)
        Text(
            "Параметры клиента и демонстрационные фишки приложения.",
            color = Color(0xFF6B7280),
            modifier = Modifier.padding(top = 6.dp, bottom = 18.dp)
        )
        SettingCard("Оффлайн-кэш", "Показывать фильмы из Room без интернета", offlineCache) {
            offlineCache = it
        }
        SettingCard("Умная сортировка", "Поднимать избранное и высокие оценки выше", smartSorting) {
            smartSorting = it
        }
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("API endpoint", style = MaterialTheme.typography.titleLarge)
                Text("http://10.0.2.2:8080/api", color = Color(0xFF6B7280))
            }
        }
        Row(modifier = Modifier.padding(top = 18.dp)) {
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D28D9)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Назад")
            }
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(start = 10.dp)
            ) {
                Text("Выйти")
            }
        }
    }
}

@Composable
private fun SettingCard(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        Row(modifier = Modifier.padding(18.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleLarge)
                Text(description, color = Color(0xFF6B7280), modifier = Modifier.padding(top = 4.dp))
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

