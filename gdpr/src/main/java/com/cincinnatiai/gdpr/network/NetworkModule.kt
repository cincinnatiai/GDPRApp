package com.cincinnatiai.gdpr.network

import com.cincinnatiai.gdpr.GDPRDataProvider
import com.cincinnatiai.gdpr.network.api.GDPRApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkModule(
    private val isDebug: Boolean = false,
    private val gdprDataProvider: GDPRDataProvider,
) {

    private val logInterceptor: Interceptor by lazy {
        HttpLoggingInterceptor().apply {
            if (isDebug) {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            } else {
                setLevel(HttpLoggingInterceptor.Level.NONE)
            }
        }
    }

    private val okHttp by lazy {
        OkHttpClient.Builder()
            .addInterceptor(logInterceptor)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .client(okHttp)
            .baseUrl(gdprDataProvider.getUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    internal val gdprApi: GDPRApi by lazy {
        retrofit.create(GDPRApi::class.java)
    }

}