package com.example.fianancetracker.data.network.api

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val token: String
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val request = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
        return chain.proceed(request)
    }
}