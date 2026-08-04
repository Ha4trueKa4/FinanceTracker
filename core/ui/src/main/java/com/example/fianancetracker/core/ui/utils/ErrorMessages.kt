package com.example.fianancetracker.core.ui.utils

import android.content.Context
import com.example.fianancetracker.core.domain.common.AppError
import com.example.fianancetracker.core.ui.R

fun Throwable.toUserMessage(context: Context): String = when (this) {
    is AppError.NoInternet -> context.getString(R.string.error_no_internet)
    is AppError.Server -> context.getString(R.string.error_server, code)
    is AppError.Unknown -> reason ?: context.getString(R.string.error_unknown)
    else -> message ?: context.getString(R.string.error_unknown)
}
