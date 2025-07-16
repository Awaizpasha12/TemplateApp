package com.app.banuenterprise.ui.resetpassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.banuenterprise.data.model.request.ChangePasswordRequest
import com.app.banuenterprise.data.model.request.LoginRequest
import com.app.banuenterprise.data.model.response.ChangePasswordResponse
import com.app.banuenterprise.data.model.response.LoginResponse
import com.app.banuenterprise.data.repository.ApiRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    val apiRepository: ApiRepository
) : ViewModel(){
    private val _changePasswordRequest = MutableLiveData<ChangePasswordResponse>();
    val changePasswordRequest : LiveData<ChangePasswordResponse> = _changePasswordRequest

    fun changePassword(req : ChangePasswordRequest){
        viewModelScope.launch {
            try {
                val response = apiRepository.changePassword(req)
                _changePasswordRequest.postValue(response)
            }
            catch (e: HttpException) {
                // Get error body
                val errorBody = e.response()?.errorBody()?.string()
                // Parse it into LoginResponse (assuming Gson)
                val gson = Gson()
                val response = try {
                    gson.fromJson(errorBody, LoginResponse::class.java)
                } catch (ex: Exception) {
                    null
                }
                _changePasswordRequest.postValue(ChangePasswordResponse(false,response?.message ?: "Something went wrong"))

            }
            catch (e: Exception) {
                _changePasswordRequest.postValue(ChangePasswordResponse(false,"Something went wrong"))
            }
        }
    }
}