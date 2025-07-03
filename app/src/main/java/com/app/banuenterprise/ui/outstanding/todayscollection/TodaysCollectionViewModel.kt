package com.app.banuenterprise.ui.outstanding.todayscollection

import androidx.lifecycle.ViewModel
import com.app.banuenterprise.data.repository.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TodaysCollectionViewModel @Inject constructor(
    val repository: ApiRepository
)  : ViewModel(){
}