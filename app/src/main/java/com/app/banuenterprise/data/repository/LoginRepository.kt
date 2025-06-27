package com.app.banuenterprise.data.repository


import com.app.banuenterprise.data.model.request.LoginRequest
import com.app.banuenterprise.data.model.response.LoginResponse
import com.app.banuenterprise.network.LoginApi
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val loginApi: LoginApi
) {
    suspend fun login(request: LoginRequest): LoginResponse {
        return loginApi.login(request)
    }
}
