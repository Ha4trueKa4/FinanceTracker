package com.example.fianancetracker.core.ui.utils

import com.example.fianancetracker.core.domain.common.AppError

fun Throwable.toUserMessage(): String = when (this) {
    is AppError.NoInternet -> "Нет подключения к интернету"
    is AppError.Server -> "Ошибка сервера (${code})"
    is AppError.Unknown -> reason ?: "Неизвестная ошибка"
    else -> message ?: "Неизвестная ошибка"
}
