package com.example.fianancetracker.di

import com.example.fianancetracker.BuildConfig
import com.example.fianancetracker.data.network.api.AuthInterceptor
import com.example.fianancetracker.data.network.api.FinanceApi
import com.example.fianancetracker.data.repository.AccountRepositoryImpl
import com.example.fianancetracker.data.repository.AnalyticsRepositoryImpl
import com.example.fianancetracker.data.repository.ExpenseRepositoryImpl
import com.example.fianancetracker.data.repository.IncomeRepositoryImpl
import com.example.fianancetracker.domain.repository.AccountRepository
import com.example.fianancetracker.domain.repository.AnalyticsRepository
import com.example.fianancetracker.domain.repository.ExpenseRepository
import com.example.fianancetracker.domain.repository.IncomeRepository
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
    single<ExpenseRepository> { ExpenseRepositoryImpl(get()) }
    single<IncomeRepository> { IncomeRepositoryImpl(get()) }
    single<AccountRepository> { AccountRepositoryImpl(get()) }
    single<AnalyticsRepository> { AnalyticsRepositoryImpl(get()) }
}