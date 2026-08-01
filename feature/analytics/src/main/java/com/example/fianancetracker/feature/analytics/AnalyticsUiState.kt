package com.example.fianancetracker.feature.analytics

sealed interface AnalyticsUiState {
    data object Loading : AnalyticsUiState
    data object Empty : AnalyticsUiState
    data class Error(val message: String) : AnalyticsUiState
    data class Content(val data: AnalyticsState) : AnalyticsUiState
}
