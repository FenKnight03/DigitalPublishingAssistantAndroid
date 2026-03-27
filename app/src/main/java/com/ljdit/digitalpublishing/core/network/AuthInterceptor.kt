package com.ljdit.digitalpublishing.core.network

import com.ljdit.digitalpublishing.core.session.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()

        val token = SessionManager.token

        val newRequest = if (token != null) {

            request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()

        } else {
            request
        }

        return chain.proceed(newRequest)
    }
}