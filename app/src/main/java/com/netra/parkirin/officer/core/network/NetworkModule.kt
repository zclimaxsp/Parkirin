package com.netra.parkirin.officer.core.network

import com.netra.parkirin.officer.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

    @Provides
    @Singleton
    @Named("master")
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideMasterApiService(@Named("master") retrofit: Retrofit): MasterApiService =
        retrofit.create(MasterApiService::class.java)

    @Provides
    @Singleton
    @Named("transaction")
    fun provideTransactionRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            // Langsung panggil TRANSACTION_BASE_URL yang pasti bener port 8081
            .baseUrl(BuildConfig.TRANSACTION_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideTransactionApiService(
        @Named("transaction") retrofit: Retrofit
    ): TransactionApiService =
        retrofit.create(TransactionApiService::class.java)
}