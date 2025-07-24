package com.app.banuenterprise.ui.salesorder.salesentry

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.banuenterprise.data.model.request.OnlyApiKeyRequest
import com.app.banuenterprise.data.model.request.SalesEntryRequest
import com.app.banuenterprise.data.model.response.LoginResponse
import com.app.banuenterprise.data.model.response.SalesEntryResponse
import com.app.banuenterprise.data.model.response.SalesSettingsResponse
import com.app.banuenterprise.data.repository.ApiRepository
import com.app.banuenterprise.utils.SessionUtils
import com.app.banuenterprise.utils.extentions.LoadingDialog
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class SalesEntryViewModel @Inject constructor(
    private val apiRepository: ApiRepository
) : ViewModel() {

    private val _salesSettings = MutableLiveData<SalesSettingsResponse>()
    val salesSettings: LiveData<SalesSettingsResponse> get() = _salesSettings
    private val _salesEntryResult = MutableLiveData<SalesEntryResponse>()
    val salesEntryResult: LiveData<SalesEntryResponse> get() = _salesEntryResult

    fun submitSalesEntry(request: SalesEntryRequest, activity: Activity) {
        viewModelScope.launch {
            LoadingDialog.show(activity, "Saving sales entry, please wait...")
            try {
                val response = apiRepository.addSalesEntry(request)
                _salesEntryResult.postValue(response)
            }
            catch (e: HttpException) {
                // Get error body
                val errorBody = e.response()?.errorBody()?.string()
                // Parse it into LoginResponse (assuming Gson)
                val gson = Gson()
                val loginResponse = try {
                    gson.fromJson(errorBody, LoginResponse::class.java)
                } catch (ex: Exception) {
                    null
                }
                _salesEntryResult.postValue(
                    SalesEntryResponse(
                        isSuccess = false,
                        data = null,
                        message = loginResponse?.message ?: "Something went wrong"
                    )
                )
            }
            catch (e: Exception) {
                _salesEntryResult.postValue(
                    SalesEntryResponse(
                        isSuccess = false,
                        data = null,
                        message = e.message ?: "Something went wrong"
                    )
                )
            } finally {
                LoadingDialog.hide()
            }
        }
    }

    // Function to fetch sales settings (ledgers and stock items)
    fun fetchSalesSettings(
        apikey: String,
        context: Context,       // for cache, use appContext
        activity: Activity,     // for dialog
        forceRefresh: Boolean = false
    ) {
        if (forceRefresh || _salesSettings.value == null) {
            // Use applicationContext for cache
            val cachedData = SessionUtils.getSalesSettings(context.applicationContext)
            if (cachedData != null && !forceRefresh) {
                _salesSettings.postValue(cachedData!!)
            } else {
                LoadingDialog.show(activity, "Fetching sales settings...")
                viewModelScope.launch {
                    try {
                        val response = apiRepository.getSalesSetting(OnlyApiKeyRequest(apikey))
                        _salesSettings.postValue(response)
                        SessionUtils.saveSalesSettings(context.applicationContext, response)
                    }
                    catch (e: HttpException) {
                        // Get error body
                        val errorBody = e.response()?.errorBody()?.string()
                        // Parse it into LoginResponse (assuming Gson)
                        val gson = Gson()
                        val loginResponse = try {
                            gson.fromJson(errorBody, LoginResponse::class.java)
                        } catch (ex: Exception) {
                            null
                        }
                        LoadingDialog.hide()
//                        _loginResult.value = loginResponse ?: LoginResponse(
//                            isSuccess = false,
//                            token = "",
//                            message = loginResponse?.message ?: "Something went wrong",
//                            user = null
//                        )
                    }

                    catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        LoadingDialog.hide()
                    }
                }
            }
        } else {
            _salesSettings.postValue(_salesSettings.value)
        }
    }
}
