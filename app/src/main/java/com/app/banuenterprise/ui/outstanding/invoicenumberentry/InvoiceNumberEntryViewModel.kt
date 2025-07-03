package com.app.banuenterprise.ui.outstanding.invoicenumberentry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.banuenterprise.data.model.response.BillItem
import com.app.banuenterprise.data.repository.ApiRepository
import com.app.banuenterprise.utils.SupportMethods
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class InvoiceNumberEntryViewModel @Inject constructor(
    val apiRepository: ApiRepository
) :  ViewModel(){
    private val _billMap = MutableLiveData<List<BillItem>>()
    val billMap: LiveData<List<BillItem>> get() = _billMap

    fun fetchInvoicesByDay(apiKey: String) {
        viewModelScope.launch {
            try {
                val response = apiRepository.invoiceByDay(apiKey, SupportMethods.getCurrentDay())
                if (response.isSuccessful && response.body() != null) {
                    val jsonObject = JSONObject(response.body()!!.string())
                    val resultMap = SupportMethods.extractBillItems(jsonObject)
                    _billMap.postValue(resultMap)
                } else {

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}