package com.example.fianancetracker.feature.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.example.fianancetracker.core.ui.model.ListItemModel
import java.time.LocalDate

data class MainUiState(
    val dateLabel: String = "",
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedTab: MainTab = MainTab.EXPENSES,
    val tabs: Map<MainTab, TabContent> = MainTab.entries.associateWith { TabContent.NotLoaded },
) {
    val current: TabContent get() = tabs.getValue(selectedTab)
}

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

enum class MainTab { EXPENSES, INCOMES, ACCOUNTS }

@Composable
internal fun MainTab.title(): String = stringResource(
    when (this) {
        MainTab.EXPENSES -> R.string.tab_expenses
        MainTab.INCOMES -> R.string.tab_incomes
        MainTab.ACCOUNTS -> R.string.tab_accounts
    }
)

@Composable
internal fun MainTab.icon(): ImageVector = when (this) {
    MainTab.EXPENSES -> ImageVector.vectorResource(R.drawable.expenses)
    MainTab.INCOMES -> Icons.AutoMirrored.Filled.TrendingUp
    MainTab.ACCOUNTS -> ImageVector.vectorResource(R.drawable.accounts)
}
