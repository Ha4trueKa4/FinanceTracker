package com.example.fianancetracker.core.data.di

import com.example.fianancetracker.core.data.BuildConfig
import com.example.fianancetracker.core.data.network.api.AuthInterceptor
import com.example.fianancetracker.core.data.network.api.FinanceApi
import com.example.fianancetracker.core.data.repository.AccountRepositoryImpl
import com.example.fianancetracker.core.data.repository.AnalyticsRepositoryImpl
import com.example.fianancetracker.core.data.repository.CategoryRepositoryImpl
import com.example.fianancetracker.core.data.repository.TransactionRepositoryImpl
import com.example.fianancetracker.core.domain.repository.AccountRepository
import com.example.fianancetracker.core.domain.repository.AnalyticsRepository
import com.example.fianancetracker.core.domain.repository.CategoryRepository
import com.example.fianancetracker.core.domain.repository.TransactionRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://shmr-finance.ru/api/v1/"

val networkModule = module {
    single { AuthInterceptor(BuildConfig.TOKEN) }

    single {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(get<AuthInterceptor>())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FinanceApi::class.java)
    }
}

val repositoryModule = module {
    single<TransactionRepository> { TransactionRepositoryImpl(get()) }
    single<AccountRepository> { AccountRepositoryImpl(get()) }
    single<AnalyticsRepository> { AnalyticsRepositoryImpl(get()) }
    single<CategoryRepository> { CategoryRepositoryImpl(get()) }
}