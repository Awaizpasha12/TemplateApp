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
import com.app.banuenterprise.data.model.response.LoginResponse
import com.app.banuenterprise.data.repository.ApiRepository
import com.app.banuenterprise.utils.SupportMethods
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
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

                val gson = Gson()
                val bodyString = response.body()?.string()

                if (response.isSuccessful && !bodyString.isNullOrEmpty()) {
                    val receiptResponse = try {
                        gson.fromJson(bodyString, LoginResponse::class.java)
                    } catch (e: Exception) {
                        null
                    }

                    if (receiptResponse?.isSuccess == true) {
                        _receiptSubmissionResult.postValue(Pair(true, receiptResponse.message ?: "Receipt Submitted successfully"))
                    } else {
                        _receiptSubmissionResult.postValue(Pair(false, receiptResponse?.message ?: "Submission failed"))
                    }

                } else {
                    val errorString = response.errorBody()?.string()
                    val errorResponse = try {
                        gson.fromJson(errorString, LoginResponse::class.java)
                    } catch (e: Exception) {
                        null
                    }
                    val msg = errorResponse?.message ?: "Unknown error occurred"
                    _receiptSubmissionResult.postValue(Pair(false, msg))
                }

            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = try {
                    val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
                    errorResponse?.message ?: "Something went wrong"
                } catch (ex: Exception) {
                    "Something went wrong"
                }
                _receiptSubmissionResult.postValue(Pair(false, errorMessage))
            } catch (e: Exception) {
                _receiptSubmissionResult.postValue(Pair(false, "Submission error: ${e.localizedMessage}"))
            }
        }
    }



}