package ru.skfu.moviecollection.control

import ru.skfu.moviecollection.model.MovieDto

sealed interface MovieUiState {
    data object Loading : MovieUiState
    data object Empty : MovieUiState
    data class Success(val movies: List<MovieDto>, val offline: Boolean = false) : MovieUiState
    data class Error(val message: String) : MovieUiState
}

