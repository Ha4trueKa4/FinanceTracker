package com.example.fianancetracker.presentation.utils

import com.example.fianancetracker.domain.common.AppError

/** Централизованный перевод доменной ошибки в текст для пользователя. */
fun Throwable.toUserMessage(): String = when (this) {
    is AppError.NoInternet -> "Нет подключения к интернету"
    is AppError.Server -> "Ошибка сервера (${code})"
    is AppError.Unknown -> reason ?: "Неизвестная ошибка"
    else -> message ?: "Неизвестная ошибка"
}
