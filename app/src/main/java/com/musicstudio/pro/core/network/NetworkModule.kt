package com.musicstudio.pro.core.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object NetworkModule {
    fun createRetrofit(baseUrl: String): Retrofit {
        val httpClient = OkHttpClient.Builder().build()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }
}
