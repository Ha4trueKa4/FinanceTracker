package com.example.fianancetracker.feature.analytics

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fianancetracker.core.ui.components.FilterRow
import com.example.fianancetracker.core.ui.components.FinanceListItem
import com.example.fianancetracker.feature.analytics.components.FinancePieChart

/** Контент экрана аналитики: круговая диаграмма, строки фильтров и список транзакций. */
@Composable
internal fun AnalyticsContent(
    data: AnalyticsState,
    padding: PaddingValues,
    onChartClick: () -> Unit,
    onTypeClick: () -> Unit,
    onPeriodClick: () -> Unit,
    onCategoryClick: () -> Unit,
    onAccountClick: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
    ) {
        item {
            FinancePieChart(
                slices = data.slices,
                totalAmount = data.totalFormatted,
                totalLabel = "Всего за период",
                onClick = onChartClick,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        item { Spacer(Modifier.height(16.dp)) }

        item {
            AnalyticsFilterRow(R.drawable.ic_filter_type, "Тип", data.typeBadge, onTypeClick)
            AnalyticsFilterDivider()
            AnalyticsFilterRow(R.drawable.ic_filter_period, "Период", data.periodBadge, onPeriodClick)
            AnalyticsFilterDivider()
            AnalyticsFilterRow(R.drawable.ic_filter_category, "Статьи", data.categoriesBadge, onCategoryClick)
            AnalyticsFilterDivider()
            AnalyticsFilterRow(R.drawable.ic_filter_account, "Счёт", data.accountBadge, onAccountClick)
        }

        item {
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Транзакции",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1D1B20),
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(Modifier.height(4.dp))
        }

        items(data.items, key = { it.id }) { item ->
            FinanceListItem(item = item)
            HorizontalDivider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
            )
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun AnalyticsFilterRow(iconRes: Int, label: String, badge: String, onClick: () -> Unit) {
    FilterRow(
        icon = painterResource(id = iconRes),
        label = label,
        badgeText = badge,
        onClick = onClick,
    )
}

@Composable
private fun AnalyticsFilterDivider() {
    HorizontalDivider(
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
    )
}
