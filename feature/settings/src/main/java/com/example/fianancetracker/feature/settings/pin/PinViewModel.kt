package com.example.fianancetracker.feature.settings.pin

import androidx.lifecycle.ViewModel
import com.example.fianancetracker.core.domain.repository.PinRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Логика PIN-экрана: набор цифр и переходы между установкой/подтверждением/проверкой. */
class PinViewModel(
    private val pinRepository: PinRepository,
    initialMode: PinMode,
) : ViewModel() {

    private var firstEntry: String? = null

    private val _state = MutableStateFlow(PinUiState(initialMode))
    val state: StateFlow<PinUiState> = _state.asStateFlow()

    fun onDigit(digit: String) {
        val current = _state.value
        if (current.success || current.entered.length >= PIN_LENGTH) return
        val entered = current.entered + digit
        _state.value = current.copy(entered = entered, error = false)
        if (entered.length == PIN_LENGTH) process(entered)
    }

    fun onBackspace() {
        val current = _state.value
        if (current.entered.isEmpty()) return
        _state.value = current.copy(entered = current.entered.dropLast(1), error = false)
    }

    private fun process(pin: String) {
        when (_state.value.mode) {
            PinMode.SETUP -> {
                firstEntry = pin
                _state.value = PinUiState(PinMode.CONFIRM)
            }

            PinMode.CONFIRM ->
                if (pin == firstEntry) {
                    pinRepository.setPin(pin)
                    _state.value = _state.value.copy(success = true)
                } else {
                    firstEntry = null
                    _state.value = PinUiState(PinMode.SETUP, error = true)
                }

            PinMode.VERIFY ->
                if (pinRepository.verifyPin(pin)) {
                    _state.value = _state.value.copy(success = true)
                } else {
                    _state.value = PinUiState(PinMode.VERIFY, error = true)
                }
        }
    }
}
