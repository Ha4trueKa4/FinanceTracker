package com.example.fianancetracker.core.domain.common

/**
 * Доменная ошибка. Наследует Throwable, чтобы укладываться в [Result.failure].
 * Семантику ошибки (что показать пользователю) решает presentation-слой,
 * репозитории лишь классифицируют причину.
 */
sealed class AppError(message: String? = null) : Throwable(message) {

    /** Нет сети / таймаут. */
    data object NoInternet : AppError()

    /** Сервер вернул неуспешный код. */
    data class Server(val code: Int) : AppError("HTTP $code")

    /** Всё остальное. */
    data class Unknown(val reason: String?) : AppError(reason)
}
