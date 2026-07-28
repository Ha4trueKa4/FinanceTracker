package com.example.fianancetracker.feature.main.add.common

private const val MAX_DIGITS = 12

fun appendKey(current: String, key: String): String {
    if (current.length >= MAX_DIGITS) return current
    return current + key
}
