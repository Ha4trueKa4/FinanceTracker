package com.example.fianancetracker.feature.main.add.account

data class AddAccountUiState(
    val visible: Boolean = false,
    val submitting: Boolean = false,
    val editingId: String? = null,
    val name: String = "",
    val amountDigits: String = "",
    val emoji: String = ACCOUNT_EMOJIS.first(),
    val currency: String = "RUB",
    val showCurrencyPicker: Boolean = false,
    val showErrors: Boolean = false,
) {
    val isValid: Boolean get() = name.isNotBlank()

    val form: AddAccountFormState
        get() = AddAccountFormState(
            amountText = "${amountDigits.ifEmpty { "0" }} ₽",
            name = name,
            emoji = emoji,
            currency = currency,
            nameError = showErrors && name.isBlank(),
            amountError = false,
        )
}

data class AddAccountFormState(
    val amountText: String = "0 ₽",
    val name: String = "",
    val emoji: String = "",
    val currency: String = "",
    val nameError: Boolean = false,
    val amountError: Boolean = false,
)
