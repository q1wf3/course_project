package ru.skfu.moviecollection.presentation

fun normalizeImageUrl(url: String?): String? {
    val trimmed = url?.trim().orEmpty()
    if (trimmed.isBlank()) {
        return null
    }
    return when {
        trimmed.startsWith("http://", ignoreCase = true) -> trimmed
        trimmed.startsWith("https://", ignoreCase = true) -> trimmed
        trimmed.startsWith("www.", ignoreCase = true) -> "https://$trimmed"
        else -> trimmed
    }
}
