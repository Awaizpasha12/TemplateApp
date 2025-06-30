package com.app.banuenterprise.ui.outstanding.daywisecustomer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.banuenterprise.data.model.request.DayWiseRequest
import com.app.banuenterprise.data.model.response.DayWiseResponse
import com.app.banuenterprise.data.repository.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DayWiseCustomerViewModel @Inject constructor(
    private val repository: ApiRepository
) : ViewModel(){

    private val _dayWiseResult = MutableLiveData<DayWiseResponse>();
    val dayWiseResult : LiveData<DayWiseResponse> = _dayWiseResult

    fun getDetails(apikey : String,day : String){
        viewModelScope.launch {
            try {
                val response = repository.dayWiseCustomer(apikey,day)
                _dayWiseResult.postValue(response)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}