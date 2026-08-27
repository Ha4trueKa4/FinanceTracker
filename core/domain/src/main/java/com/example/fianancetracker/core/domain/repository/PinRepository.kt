package com.example.fianancetracker.core.domain.repository

/** Хранилище PIN-кода: установка, проверка и сброс. Реализация шифрует значение на устройстве. */
interface PinRepository {
    fun isPinSet(): Boolean
    fun setPin(pin: String)
    fun verifyPin(pin: String): Boolean
    fun clearPin()
}
