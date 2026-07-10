package com.example.fianancetracker.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.fianancetracker.model.MockData
import com.example.fianancetracker.ui.components.FinanceFloatingActionButton
import com.example.fianancetracker.ui.components.FinanceListContent
import com.example.fianancetracker.ui.components.FinanceTopBar
import com.example.fianancetracker.ui.components.FinanceTotalCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(modifier: Modifier = Modifier) {
    val items = MockData.getAccountListItems()
    val totalText = String.format("%,.0f ₽", MockData.totalBalance)

    Scaffold(
        modifier = modifier,
        topBar = {
            FinanceTopBar()
        },
        floatingActionButton = {
            FinanceFloatingActionButton(onClick = {})
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            FinanceTotalCard(
                label = "баланс, всего",
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
}