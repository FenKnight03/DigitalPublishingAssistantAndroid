package com.ljdit.digitalpublishing.data.api

import LoginRequest
import LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("api/accounts/mobile/")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

}