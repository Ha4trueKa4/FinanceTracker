package com.example.fianancetracker.feature.main

import com.example.fianancetracker.core.ui.model.ListItemModel

/** Табы главного экрана. Переключение таба меняет состояние списка, а не навигацию. */
enum class MainTab { EXPENSES, INCOMES, ACCOUNTS }

/**
 * Состояние отдельного таба. Всё уже готово к рендеру — списки смаплены в [ListItemModel],
 * суммы отформатированы. `NotLoaded` — триггер ленивой загрузки при первом открытии.
 */
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

/** Единое состояние всего главного экрана под одну [MainViewModel]. */
data class MainUiState(
    val dateLabel: String = "",
    val selectedTab: MainTab = MainTab.EXPENSES,
    val tabs: Map<MainTab, TabContent> = MainTab.entries.associateWith { TabContent.NotLoaded },
) {
    val current: TabContent get() = tabs.getValue(selectedTab)
}
