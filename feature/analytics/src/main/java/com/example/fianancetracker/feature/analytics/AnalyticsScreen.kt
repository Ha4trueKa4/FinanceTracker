package com.example.fianancetracker.feature.analytics

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.fianancetracker.core.ui.components.FinanceEmptyState
import com.example.fianancetracker.core.ui.components.FinanceErrorState
import com.example.fianancetracker.core.ui.components.FinanceLoadingState

/** Флаги видимости bottom-sheet'ов аналитики: триггерятся из контента, читаются хостом шитов. */
@Stable
class AnalyticsSheets {
    var type by mutableStateOf(false)
    var period by mutableStateOf(false)
    var customDate by mutableStateOf(false)
    var account by mutableStateOf(false)
    var category by mutableStateOf(false)
    var detail by mutableStateOf(false)
}

private const val EMPTY_PERIOD_MESSAGE = "Нет операций за выбранный период"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    val sheets = remember { AnalyticsSheets() }

    Scaffold(
        topBar = { AnalyticsTopBar(onBackClick) },
        modifier = modifier,
    ) { padding ->
        when (val s = state) {
            is AnalyticsUiState.Loading -> FinanceLoadingState(Modifier.padding(padding))

            is AnalyticsUiState.Error -> FinanceErrorState(
                message = s.message,
                onRetry = viewModel::loadData,
                modifier = Modifier.padding(padding),
            )

            is AnalyticsUiState.Empty ->
                FinanceEmptyState(EMPTY_PERIOD_MESSAGE, Modifier.padding(padding))

            is AnalyticsUiState.Content -> {
                val data = s.data
                if (data.items.isEmpty()) {
                    FinanceEmptyState(EMPTY_PERIOD_MESSAGE, Modifier.padding(padding))
                } else {
                    AnalyticsContent(
                        data = data,
                        padding = padding,
                        onChartClick = { sheets.detail = true },
                        onTypeClick = { sheets.type = true },
                        onPeriodClick = { sheets.period = true },
                        onCategoryClick = { sheets.category = true },
                        onAccountClick = { sheets.account = true },
                    )
                }
                AnalyticsFilterSheets(data, sheets, viewModel::updateFilter)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnalyticsTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "Аналитика",
                fontSize = 28.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF1D1B20),
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    )
}
