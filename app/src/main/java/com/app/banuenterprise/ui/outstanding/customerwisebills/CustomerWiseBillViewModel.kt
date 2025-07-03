package com.app.banuenterprise.ui.outstanding.customerwisebills

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.banuenterprise.data.model.response.CustomerWiseResponse
import com.app.banuenterprise.data.model.response.DayWiseResponse
import com.app.banuenterprise.data.repository.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerWiseBillViewModel @Inject constructor(
    private val repository: ApiRepository
) : ViewModel() {
    private val _customerWiseResult = MutableLiveData<CustomerWiseResponse>();
    val customerWiseResult : LiveData<CustomerWiseResponse> = _customerWiseResult

    fun getDetails(apikey : String,customerId : String,day : Int){
        viewModelScope.launch {
            try {
                val response = repository.customerWiseBill(apikey,customerId,day)
                _customerWiseResult.postValue(response)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}