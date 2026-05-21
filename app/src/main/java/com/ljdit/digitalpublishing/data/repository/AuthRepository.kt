package com.ljdit.digitalpublishing.data.repository

import LoginData
import LoginRequest
import com.ljdit.digitalpublishing.data.api.RetrofitClient

class AuthRepository {

    suspend fun login(
        username: String,
        password: String
    ): LoginData {

        val response = RetrofitClient.authApi.login(
            LoginRequest(username, password)
        )

        if (!response.isSuccessful) {
            throw Exception("HTTP ERROR ${response.code()}")
        }

        val body = response.body()
            ?: throw Exception("Empty body")

        if (!body.ok) {
            throw Exception("Login failed")
        }

        return body.data
    }
}