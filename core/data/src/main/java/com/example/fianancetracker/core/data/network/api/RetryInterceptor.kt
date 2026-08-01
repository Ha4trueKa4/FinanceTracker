package com.example.fianancetracker.core.data.network.api

import okhttp3.Interceptor
import okhttp3.Response

class RetryInterceptor(
    private val maxRetries: Int = 3,
    private val retryDelayMillis: Long = 2_000,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var attempt = 0
        var response = chain.proceed(chain.request())

        while (response.code == 500 && attempt < maxRetries) {
            response.close()
            attempt++
            try {
                Thread.sleep(retryDelayMillis)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                break
            }
            response = chain.proceed(chain.request())
        }

        return response
    }
}
