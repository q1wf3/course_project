package ru.skfu.moviecollection.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable

@Composable
fun movieTextFieldColors(): TextFieldColors {
    val colors = MaterialTheme.colorScheme
    return OutlinedTextFieldDefaults.colors(
        focusedTextColor = colors.onSurface,
        unfocusedTextColor = colors.onSurface,
        focusedLabelColor = colors.primary,
        unfocusedLabelColor = colors.onSurface.copy(alpha = 0.68f),
        focusedPlaceholderColor = colors.onSurface.copy(alpha = 0.55f),
        unfocusedPlaceholderColor = colors.onSurface.copy(alpha = 0.55f),
        focusedBorderColor = colors.primary,
        unfocusedBorderColor = colors.outline,
        cursorColor = colors.primary,
        focusedContainerColor = colors.surface,
        unfocusedContainerColor = colors.surface
    )
}
