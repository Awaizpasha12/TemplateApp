package com.app.banuenterprise.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.banuenterprise.data.model.request.LoginRequest
import com.app.banuenterprise.data.model.response.LoginResponse
import com.app.banuenterprise.data.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LoginRepository
) : ViewModel() {
    private val _loginResult = MutableLiveData<LoginResponse>();
    val loginResult : LiveData<LoginResponse> = _loginResult

    fun login(userName : String,password : String){
        viewModelScope.launch {
            try {
                val response = repository.login(LoginRequest(userName,password))
                _loginResult.postValue(response)
            } catch (e: Exception) {
                _loginResult.value = LoginResponse(
                    statusCode = 0,
                    isSuccess = false,
                    apiKey = "",
                    message = e.localizedMessage ?: "Something went wrong"
                )
            }
        }
    }
}