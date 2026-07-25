package com.example.fianancetracker.core.data.common

import com.example.fianancetracker.core.domain.common.AppError
import retrofit2.Response
import java.io.IOException

/**
 * Оборачивает операцию репозитория в [Result], классифицируя исключения в [AppError].
 * Любой [AppError], бросённый внутри (например из [bodyOrThrow]), пробрасывается как есть.
 */
suspend inline fun <T> apiCall(crossinline block: suspend () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: AppError) {
        Result.failure(e)
    } catch (e: IOException) {
        Result.failure(AppError.NoInternet)
    } catch (e: Exception) {
        Result.failure(AppError.Unknown(e.message))
    }

/** Тело успешного ответа либо [AppError.Server] при неуспехе/пустом теле. */
fun <T> Response<T>.bodyOrThrow(): T {
    if (!isSuccessful) throw AppError.Server(code())
    return body() ?: throw AppError.Server(code())
}

/** Тело ответа или null — для «мягких» запросов, где неуспех можно пропустить. */
fun <T> Response<T>.bodyOrNull(): T? = if (isSuccessful) body() else null
