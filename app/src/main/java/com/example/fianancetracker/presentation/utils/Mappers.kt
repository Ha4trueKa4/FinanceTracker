package com.example.fianancetracker.presentation.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

fun toThousand(value: Double): String {
    val symbols = DecimalFormatSymbols.getInstance(Locale.getDefault()).apply {
        groupingSeparator = ' '
    }
    val formatter = DecimalFormat("#,##0", symbols)
    return "${formatter.format(value)} ₽"
}