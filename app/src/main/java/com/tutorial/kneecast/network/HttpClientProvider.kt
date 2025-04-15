package com.tutorial.kneecast.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

fun createOkHttpClient(): OkHttpClient {
    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    return OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()
}