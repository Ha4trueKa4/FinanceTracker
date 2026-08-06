package com.example.fianancetracker.core.domain.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/** Поставщик корутин-диспетчеров: инжектируется в репозитории, чтобы их можно было подменить в тестах. */
interface DispatcherProvider {
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
}

/** Боевая реализация поверх стандартных [Dispatchers]. */
object StandardDispatcherProvider : DispatcherProvider {
    override val io: CoroutineDispatcher = Dispatchers.IO
    override val default: CoroutineDispatcher = Dispatchers.Default
}
