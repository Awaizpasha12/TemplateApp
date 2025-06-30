package com.app.banuenterprise.data.repository


import android.content.Context.MODE_PRIVATE
import com.app.banuenterprise.data.model.request.DayWiseRequest
import com.app.banuenterprise.data.model.request.LoginRequest
import com.app.banuenterprise.data.model.response.CustomerWiseResponse
import com.app.banuenterprise.data.model.response.DayWiseResponse
import com.app.banuenterprise.data.model.response.InvoicesByDayResponse
import com.app.banuenterprise.data.model.response.LoginResponse
import com.app.banuenterprise.network.RetrofitInterface
import javax.inject.Inject

class ApiRepository @Inject constructor(
    private val apiRepository: RetrofitInterface
) {

    suspend fun login(request: LoginRequest): LoginResponse {
        return apiRepository.login(request)
    }
    suspend fun dayWiseCustomer(apikey:String,day: String) : DayWiseResponse {
        return apiRepository.getDayWiseCustomers(apikey,day)
    }
    suspend fun customerWiseBill(apikey:String,day: String) : CustomerWiseResponse {
        return apiRepository.getCustomerWiseBill(apikey,day)
    }

    suspend fun invoiceByDay(apikey:String,day: String) : InvoicesByDayResponse {
        return apiRepository.getInvoicesByDay(apikey,day)
    }
}
