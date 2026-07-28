package com.example.fianancetracker.feature.main.add.transaction

internal fun typeDigit(value: Int, partial: Int, digit: Int, max: Int): Triple<Int, Int, Boolean> {
    val tensMax = max / 10
    return when (partial) {
        0 -> {
            val complete = digit > tensMax
            Triple(digit, if (complete) 2 else 1, complete)
        }
        1 -> {
            val candidate = value * 10 + digit
            if (candidate <= max) {
                Triple(candidate, 2, true)
            } else {
                Triple(digit, if (digit > tensMax) 2 else 1, digit > tensMax)
            }
        }
        else -> {
            val complete = digit > tensMax
            Triple(digit, if (complete) 2 else 1, complete)
        }
    }
}
