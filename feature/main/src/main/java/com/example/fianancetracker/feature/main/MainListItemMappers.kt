package com.example.fianancetracker.feature.main

import com.example.fianancetracker.core.domain.account.Account
import com.example.fianancetracker.core.domain.transaction.Transaction
import com.example.fianancetracker.core.ui.model.ListItemModel
import com.example.fianancetracker.core.ui.utils.accountEmoji
import com.example.fianancetracker.core.ui.utils.toThousand

/** Мапперы доменных моделей в [ListItemModel] для списков главного экрана. */

internal fun Transaction.toListItem(isPositive: Boolean) = ListItemModel(
    id = id,
    lead = ListItemModel.Lead(emoji = category.emoji),
    content = ListItemModel.Content(title = title, subtitle = "${category.name} • $date"),
    trail = ListItemModel.Trail(amount = toThousand(amount), isPositive = isPositive),
)

internal fun Account.toListItem() = ListItemModel(
    id = id,
    lead = ListItemModel.Lead(emoji = emoji.ifEmpty { accountEmoji(type) }),
    content = ListItemModel.Content(
        title = name,
        subtitle = cardNumber.ifEmpty { type.displayName },
    ),
    trail = ListItemModel.Trail(amount = toThousand(balance), isPositive = balance >= 0),
)
