package com.example.fianancetracker.core.data.di

import com.example.fianancetracker.core.data.BuildConfig
import com.example.fianancetracker.core.data.datasource.remote.AccountRemoteDataSource
import com.example.fianancetracker.core.data.datasource.remote.AccountRemoteDataSourceImpl
import com.example.fianancetracker.core.data.datasource.remote.CategoryRemoteDataSource
import com.example.fianancetracker.core.data.datasource.remote.CategoryRemoteDataSourceImpl
import com.example.fianancetracker.core.data.datasource.remote.TransactionRemoteDataSource
import com.example.fianancetracker.core.data.datasource.remote.TransactionRemoteDataSourceImpl
import com.example.fianancetracker.core.data.network.api.AuthInterceptor
import com.example.fianancetracker.core.data.network.api.FinanceApi
import com.example.fianancetracker.core.data.network.api.RetryInterceptor
import com.example.fianancetracker.core.data.repository.AccountRepositoryImpl
import com.example.fianancetracker.core.data.repository.AnalyticsRepositoryImpl
import com.example.fianancetracker.core.data.repository.CategoryRepositoryImpl
import com.example.fianancetracker.core.data.repository.TransactionRepositoryImpl
import com.example.fianancetracker.core.domain.common.DispatcherProvider
import com.example.fianancetracker.core.domain.common.StandardDispatcherProvider
import com.example.fianancetracker.core.domain.repository.AccountRepository
import com.example.fianancetracker.core.domain.repository.AnalyticsRepository
import com.example.fianancetracker.core.domain.repository.CategoryRepository
import com.example.fianancetracker.core.domain.repository.TransactionRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://shmr-finance.ru/api/v1/"

val networkModule = module {
    single { AuthInterceptor(BuildConfig.TOKEN) }

    single<Gson> { GsonBuilder().serializeNulls().create() }

    single {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(RetryInterceptor())
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
            .addConverterFactory(GsonConverterFactory.create(get<Gson>()))
            .build()
            .create(FinanceApi::class.java)
    }

    single<AccountRemoteDataSource> { AccountRemoteDataSourceImpl(get()) }
    single<TransactionRemoteDataSource> { TransactionRemoteDataSourceImpl(get()) }
    single<CategoryRemoteDataSource> { CategoryRemoteDataSourceImpl(get()) }
}

val repositoryModule = module {
    single<DispatcherProvider> { StandardDispatcherProvider }

    single<TransactionRepository> {
        TransactionRepositoryImpl(get(), get(), get(), get(), get(), get(), get(), get())
    }
    single<AccountRepository> { AccountRepositoryImpl(get(), get(), get(), get(), get()) }
    single<AnalyticsRepository> { AnalyticsRepositoryImpl(get(), get(), get(), get(), get()) }
    single<CategoryRepository> { CategoryRepositoryImpl(get(), get(), get()) }
}
