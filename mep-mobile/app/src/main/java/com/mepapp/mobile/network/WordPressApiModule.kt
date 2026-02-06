package com.mepapp.mobile.network

import android.util.Base64
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object WordPressApiModule {
    private const val BASE_URL = "https://maduraielectriciansandplumbers.com/"
    private const val USERNAME = "mep_admin"
    private const val PASSWORD = "flsb8aYzar1ZzgiRxbrB4n9O"

    private val basicAuthInterceptor = Interceptor { chain ->
        val credentials = "$USERNAME:$PASSWORD"
        val basicAuth = "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
        val request = chain.request().newBuilder()
            .addHeader("Authorization", basicAuth)
            .build()
        chain.proceed(request)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(basicAuthInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    @PublishedApi
    internal val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    inline fun <reified T> createService(): T = retrofit.create(T::class.java)
}
