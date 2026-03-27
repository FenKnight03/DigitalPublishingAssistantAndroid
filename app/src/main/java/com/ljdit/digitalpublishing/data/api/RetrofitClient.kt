package com.ljdit.digitalpublishing.data.api

import com.ljdit.digitalpublishing.core.network.AuthInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://ljdit.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val photoApi: PhotoApi =
        retrofit.create(PhotoApi::class.java)

    val authApi: AuthApi =
        retrofit.create(AuthApi::class.java)

}