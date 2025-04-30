package com.tutorial.kneecast.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitFactory {
    fun createRetrofitInstance(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(createOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    /**
     * Goバックエンドサーバー用のRetrofitインスタンスを作成
     * @return Retrofit Goサーバー用に設定されたRetrofitインスタンス
     */
    fun createGoServerRetrofitInstance(): Retrofit {
        // エミュレータからlocalhostにアクセスするための特別なIP
        // 実機の場合は実際のサーバーIPアドレスに変更する必要があります
        val goServerBaseUrl = "http://10.0.2.2:8080/"
        
        return createRetrofitInstance(goServerBaseUrl)
    }
}