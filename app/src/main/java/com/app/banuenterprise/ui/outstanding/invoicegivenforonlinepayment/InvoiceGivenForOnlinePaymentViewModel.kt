package com.app.banuenterprise.ui.outstanding.invoicegivenforonlinepayment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.banuenterprise.data.repository.ApiRepository
import com.app.banuenterprise.utils.SupportMethods
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class InvoiceGivenForOnlinePaymentViewModel @Inject constructor(
    val apiRepository: ApiRepository
) : ViewModel(){
    private val _billMap = MutableLiveData<HashMap<String, JSONObject>>()
    val billMap: LiveData<HashMap<String, JSONObject>> get() = _billMap

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
}