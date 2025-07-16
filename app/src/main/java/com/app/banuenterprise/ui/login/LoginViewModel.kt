package com.app.banuenterprise.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.banuenterprise.data.model.request.LoginRequest
import com.app.banuenterprise.data.model.response.LoginResponse
import com.app.banuenterprise.data.repository.ApiRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: ApiRepository
) : ViewModel() {
    private val _loginResult = MutableLiveData<LoginResponse>();
    val loginResult : LiveData<LoginResponse> = _loginResult

    fun login(userName: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.login(LoginRequest(userName, password))
                _loginResult.postValue(response)
            } catch (e: HttpException) {
                // Get error body
                val errorBody = e.response()?.errorBody()?.string()
                // Parse it into LoginResponse (assuming Gson)
                val gson = Gson()
                val loginResponse = try {
                    gson.fromJson(errorBody, LoginResponse::class.java)
                } catch (ex: Exception) {
                    null
                }
                _loginResult.value = loginResponse ?: LoginResponse(
                    isSuccess = false,
                    token = "",
                    message = loginResponse?.message ?: "Something went wrong",
                    user = null
                )
            } catch (e: Exception) {
                _loginResult.value = LoginResponse(
                    isSuccess = false,
                    token = "",
                    message = e.localizedMessage ?: "Something went wrong",
                    user = null
                )
            }
        }
    }

}