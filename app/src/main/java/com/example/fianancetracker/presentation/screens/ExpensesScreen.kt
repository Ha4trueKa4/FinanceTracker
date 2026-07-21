package com.example.fianancetracker.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.fianancetracker.data.MockData
import com.example.fianancetracker.presentation.components.FinanceListContent
import com.example.fianancetracker.presentation.components.FinanceTotalCard
import com.example.fianancetracker.presentation.utils.toThousand

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(modifier: Modifier = Modifier) {
    val items = MockData.getExpenseListItems()
    val totalText = toThousand(MockData.totalExpenses)
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        FinanceTotalCard(
            label = "расходы всего",
            amount = totalText,
        )
        FinanceListContent(
            items = items,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        )
    }
}