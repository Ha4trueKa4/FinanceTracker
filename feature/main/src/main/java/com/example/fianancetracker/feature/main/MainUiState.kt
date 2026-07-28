package com.example.fianancetracker.feature.main

data class MainUiState(
    val dateLabel: String = "",
    val selectedTab: MainTab = MainTab.EXPENSES,
    val tabs: Map<MainTab, TabContent> = MainTab.entries.associateWith { TabContent.NotLoaded },
) {
    val current: TabContent get() = tabs.getValue(selectedTab)
}
