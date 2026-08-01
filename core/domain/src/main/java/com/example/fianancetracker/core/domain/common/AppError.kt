package com.example.fianancetracker.core.domain.common

sealed class AppError(message: String? = null) : Throwable(message) {

    data object NoInternet : AppError()

    data class Server(val code: Int) : AppError("HTTP $code")

    data class Unknown(val reason: String?) : AppError(reason)
}
