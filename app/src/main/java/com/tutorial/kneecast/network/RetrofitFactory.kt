package com.tutorial.kneecast.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun createRetrofitInstance(baseUrl: String): Retrofit {
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(createOkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}