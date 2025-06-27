package com.app.banuenterprise.network

import com.app.banuenterprise.data.model.request.LoginRequest
import com.app.banuenterprise.data.model.response.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApi {
    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse
}