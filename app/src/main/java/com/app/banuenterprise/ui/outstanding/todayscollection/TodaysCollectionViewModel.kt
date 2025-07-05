package com.app.banuenterprise.ui.outstanding.todayscollection

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.banuenterprise.data.model.request.CollectionListRequest
import com.app.banuenterprise.data.model.response.CollectionItem
import com.app.banuenterprise.data.model.response.CollectionListResponse
import com.app.banuenterprise.data.model.response.DayWiseResponse
import com.app.banuenterprise.data.repository.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodaysCollectionViewModel @Inject constructor(
    val repository: ApiRepository
)  : ViewModel(){

    private val _billItemList = MutableLiveData<List<CollectionItem>>();
    val billItemList : LiveData<List<CollectionItem>> = _billItemList

    fun getDetails(apikey : String,date : String){
        viewModelScope.launch {
            try {
                val response = repository.getListCollections(CollectionListRequest(date,apikey))
                _billItemList.postValue(response.data)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}