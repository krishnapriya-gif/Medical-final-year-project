package com.example.pharmacare.response

import android.util.Base64
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {

    private const val BASE_URL = "https://wizzie.online/CraftLeather/"
    const val TYPE = "PharmaCare1"

    private val AUTH_HEADER by lazy {
        "Basic ${Base64.encodeToString("sss".toByteArray(), Base64.NO_WRAP)}"
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", AUTH_HEADER)
                .build()
            chain.proceed(request)
        }
        .build()

    val instance: API by lazy {
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(API::class.java)
    }

}