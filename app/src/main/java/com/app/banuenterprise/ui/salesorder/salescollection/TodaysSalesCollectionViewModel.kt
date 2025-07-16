package com.app.banuenterprise.ui.salesorder.salescollection

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.banuenterprise.data.model.request.CollectionListRequest
import com.app.banuenterprise.data.model.response.CollectionItem
import com.app.banuenterprise.data.model.response.SalesOrder
import com.app.banuenterprise.data.repository.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodaysSalesCollectionViewModel @Inject constructor(
    val apiRepository: ApiRepository
) : ViewModel(){
    private val _billItemList = MutableLiveData<List<SalesOrder>>();
    val billItemList : LiveData<List<SalesOrder>> = _billItemList

    fun getDetails(apikey : String,date : String){
        viewModelScope.launch {
            try {
                val response = apiRepository.getSalesList(CollectionListRequest(date,apikey))
                _billItemList.postValue(response.data)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}