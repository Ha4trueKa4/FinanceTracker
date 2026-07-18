package com.example.fianancetracker.presentation.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class ListItemModel(
    val id: String,
    val lead: Lead,
    val content: Content,
    val trail: Trail
) {
    data class Lead(
        val icon: ImageVector? = null,
        val emoji: String? = null,
        val backgroundColor: Color = Color.Transparent
    )

    data class Content(
        val title: String,
        val subtitle: String = ""
    )

    data class Trail(
        val amount: String,
        val isPositive: Boolean = true
    )
}