package com.example.fianancetracker.feature.main

import com.example.fianancetracker.core.ui.model.ListItemModel

sealed interface TabContent {
    data object NotLoaded : TabContent
    data object Loading : TabContent
    data class Empty(val message: String) : TabContent
    data class Error(val message: String) : TabContent
    data class Content(
        val totalLabel: String,
        val totalAmount: String,
        val items: List<ListItemModel>,
    ) : TabContent
}
