package com.app.banuenterprise.ui.outstanding.receiptEntry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.banuenterprise.data.model.response.CustomerWiseResponse
import com.app.banuenterprise.data.model.response.DayWiseResponse
import com.app.banuenterprise.data.model.response.InvoicesByDayResponse
import com.app.banuenterprise.data.repository.ApiRepository
import com.app.banuenterprise.utils.SupportMethods
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReceiptEntryViewModel @Inject constructor(
    private val repository: ApiRepository
) : ViewModel(){
    private val _allCustomerDetails = MutableLiveData<DayWiseResponse>();
    val allCustomerDetails : LiveData<DayWiseResponse> = _allCustomerDetails
    private val _customerWiseResult = MutableLiveData<CustomerWiseResponse>();
    val customerWiseResult : LiveData<CustomerWiseResponse> = _customerWiseResult

    fun getAllCustomer(apikey : String){
        viewModelScope.launch {
            try {
                val response = repository.dayWiseCustomer(apikey,SupportMethods.getCurrentDay())
                _allCustomerDetails.postValue(response)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun getCustomerWiseDetails(apikey : String,customerId : String){
        viewModelScope.launch {
            try {
                val response = repository.customerWiseBill(apikey,customerId,SupportMethods.getCurrentDay())
                _customerWiseResult.postValue(response)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}