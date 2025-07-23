package com.app.banuenterprise.ui.outstanding.invoicegivenforonlinepayment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.banuenterprise.data.model.request.InvoiceGivenForOnlinePaymentRequest
import com.app.banuenterprise.data.model.request.ReceiptEntryRequest
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
class InvoiceGivenForOnlinePaymentViewModel @Inject constructor(
    val apiRepository: ApiRepository
) : ViewModel(){
    private val _billMap = MutableLiveData<HashMap<String, JSONObject>>()
    val billMap: LiveData<HashMap<String, JSONObject>> get() = _billMap
    private val _receiptSubmissionResult = MutableLiveData<Pair<Boolean, String>>()
    val receiptSubmissionResult: LiveData<Pair<Boolean, String>> get() = _receiptSubmissionResult
    fun fetchInvoicesByDay(apiKey: String) {
        viewModelScope.launch {
            try {
                val response = apiRepository.invoiceByDay(apiKey, SupportMethods.getCurrentDay())
                if (response.isSuccessful && response.body() != null) {
                    val jsonObject = JSONObject(response.body()!!.string())
                    val resultMap = SupportMethods.convertToBillMap(jsonObject)
                    _billMap.postValue(resultMap)
                } else {

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun submitReceiptEntry(req: InvoiceGivenForOnlinePaymentRequest) {
        viewModelScope.launch {
            try {
                val response = apiRepository.submitInvoiceGivenForOnlinePayment(req)
                if (response.isSuccessful && response.body() != null) {
                    _receiptSubmissionResult.postValue(Pair(true, "Receipt submitted successfully!"))
                } else {
                    _receiptSubmissionResult.postValue(Pair(false, "Duplicate entry not allowed"))
                }
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
                val errorMsg = loginResponse?.message ?: "Something went wrong"
                _receiptSubmissionResult.postValue(Pair(false, errorMsg))
            } catch (e: Exception) {
                _receiptSubmissionResult.postValue(Pair(false, "Submission error: ${e.localizedMessage}"))
            }
        }
    }
}