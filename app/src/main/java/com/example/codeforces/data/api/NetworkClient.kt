package com.example.codeforces.data.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object NetworkClient {
    private const val CODEFORCES_BASE_URL = "https://codeforces.com/api/"
    private const val DEFAULT_BACKEND_URL = "https://your-backend.example.com/api/"
    private const val TIMEOUT_SECONDS = 30L

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    private val loggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
    }

    private fun baseClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()

    fun codeforcesService(): CodeforcesApiService =
        Retrofit.Builder()
            .baseUrl(CODEFORCES_BASE_URL)
            .client(baseClient())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(CodeforcesApiService::class.java)

    fun backendService(baseUrl: String = DEFAULT_BACKEND_URL): BackendApiService =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(baseClient())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(BackendApiService::class.java)
}

