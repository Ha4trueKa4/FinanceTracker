package com.example.fianancetracker.feature.settings.pin

/** Режим PIN-экрана: установка нового, подтверждение, проверка при входе. */
enum class PinMode { SETUP, CONFIRM, VERIFY }

const val PIN_LENGTH = 4

data class PinUiState(
    val mode: PinMode,
    val entered: String = "",
    val error: Boolean = false,
    val success: Boolean = false,
)
