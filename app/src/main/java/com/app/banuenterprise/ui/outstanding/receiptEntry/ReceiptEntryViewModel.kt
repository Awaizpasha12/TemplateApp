package com.app.banuenterprise.ui.outstanding.receiptEntry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.banuenterprise.data.model.request.CustomerWiseRequest
import com.app.banuenterprise.data.model.request.DayWiseRequest
import com.app.banuenterprise.data.model.request.ReceiptEntryRequest
import com.app.banuenterprise.data.model.response.CustomerWiseResponse
import com.app.banuenterprise.data.model.response.DayWiseResponse
import com.app.banuenterprise.data.model.response.InvoicesByDayResponse
import com.app.banuenterprise.data.repository.ApiRepository
import com.app.banuenterprise.utils.SupportMethods
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class ReceiptEntryViewModel @Inject constructor(
    private val repository: ApiRepository
) : ViewModel(){
    private val _allCustomerDetails = MutableLiveData<DayWiseResponse>();
    val allCustomerDetails : LiveData<DayWiseResponse> = _allCustomerDetails
    private val _customerWiseResult = MutableLiveData<CustomerWiseResponse>();
    val customerWiseResult : LiveData<CustomerWiseResponse> = _customerWiseResult
    private val _receiptSubmissionResult = MutableLiveData<Pair<Boolean, String>>()
    val receiptSubmissionResult: LiveData<Pair<Boolean, String>> get() = _receiptSubmissionResult

    fun getAllCustomer(apikey : String){
        viewModelScope.launch {
            try {
                val response = repository.dayWiseCustomerNew(DayWiseRequest(apikey,SupportMethods.getCurrentDay(),true))
                _allCustomerDetails.postValue(response)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun getCustomerWiseDetails(apikey : String,customerId : String){
        viewModelScope.launch {
            try {
                val response = repository.customerWiseBillNew(CustomerWiseRequest(apikey,customerId,SupportMethods.getCurrentDay(),true))
                _customerWiseResult.postValue(response)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun submitReceiptEntry(req: ReceiptEntryRequest) {
        viewModelScope.launch {
            try {
                val response = repository.submitReceiptEntry(req)
                if (response.isSuccessful && response.body() != null) {
                    _receiptSubmissionResult.postValue(Pair(true, "Receipt submitted successfully!"))
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                    _receiptSubmissionResult.postValue(Pair(false, "Submission failed: $errorMsg"))
                }
            } catch (e: Exception) {
                _receiptSubmissionResult.postValue(Pair(false, "Submission error: ${e.localizedMessage}"))
            }
        }
    }


}