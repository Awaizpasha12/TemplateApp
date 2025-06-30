package com.app.banuenterprise.ui.outstanding.receiptEntry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.banuenterprise.data.model.response.DayWiseResponse
import com.app.banuenterprise.data.model.response.InvoicesByDayResponse
import com.app.banuenterprise.data.repository.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReceiptEntryViewModel @Inject constructor(
    private val repository: ApiRepository
) : ViewModel(){

    private val _invoicesByDay = MutableLiveData<InvoicesByDayResponse>();
    val invoicesByDay : LiveData<InvoicesByDayResponse> = _invoicesByDay

    fun getDetails(apikey : String,day : String){
        viewModelScope.launch {
            try {
                val response = repository.invoiceByDay(apikey,day)
                _invoicesByDay.postValue(response)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun getCurrentDayName(): String {
        return "Friday"
//        val sdf = java.text.SimpleDateFormat("EEEE", java.util.Locale.getDefault())
//        return sdf.format(java.util.Date())
    }

}