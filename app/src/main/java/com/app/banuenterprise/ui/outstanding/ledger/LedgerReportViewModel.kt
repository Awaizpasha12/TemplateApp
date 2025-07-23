package com.app.banuenterprise.ui.outstanding.ledger

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.banuenterprise.data.model.request.OnlyApiKeyRequest
import com.app.banuenterprise.data.model.response.LedgerItems
import com.app.banuenterprise.data.repository.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LedgerReportViewModel @Inject constructor(
    val apiRepository: ApiRepository
) : ViewModel(){
    private val _ledgerReportList = MutableLiveData<List<LedgerItems>>();
    val ledgerReportList : LiveData<List<LedgerItems>> = _ledgerReportList

    fun getDetails(apikey : String){
        viewModelScope.launch {
            try {
                val response = apiRepository.getLedgerReport(OnlyApiKeyRequest(apikey))
                _ledgerReportList.postValue(response.data)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}