package com.app.banuenterprise.data.repository


import android.content.Context.MODE_PRIVATE
import com.app.banuenterprise.data.model.request.CollectionListRequest
import com.app.banuenterprise.data.model.request.CustomerWiseRequest
import com.app.banuenterprise.data.model.request.DayWiseRequest
import com.app.banuenterprise.data.model.request.InvoiceGivenForOnlinePaymentRequest
import com.app.banuenterprise.data.model.request.LoginRequest
import com.app.banuenterprise.data.model.request.ReceiptEntryRequest
import com.app.banuenterprise.data.model.response.CollectionListResponse
import com.app.banuenterprise.data.model.response.CustomerWiseResponse
import com.app.banuenterprise.data.model.response.DayWiseResponse
import com.app.banuenterprise.data.model.response.InvoicesByDayResponse
import com.app.banuenterprise.data.model.response.LoginResponse
import com.app.banuenterprise.network.RetrofitInterface
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class ApiRepository @Inject constructor(
    private val apiRepository: RetrofitInterface
) {

    suspend fun login(request: LoginRequest): LoginResponse {
        return apiRepository.login(request)
    }
    suspend fun dayWiseCustomer(apikey:String,day: Int) : DayWiseResponse {
        return apiRepository.getDayWiseCustomers(DayWiseRequest(apikey,day))
    }
    suspend fun customerWiseBill(apikey:String,customerId: String,day : Int) : CustomerWiseResponse {
        return apiRepository.getCustomerWiseBill(CustomerWiseRequest(apikey,customerId,day))
    }

    suspend fun invoiceByDay(apikey:String,day: Int) : Response<ResponseBody> {
        return apiRepository.getInvoicesByDay(DayWiseRequest(apikey,day))
    }
    suspend fun submitReceiptEntry(reqObj:ReceiptEntryRequest) : Response<ResponseBody> {
        return apiRepository.submitReceiptEntry(reqObj)
    }
    suspend fun getListCollections(reqObj:CollectionListRequest) : CollectionListResponse {
        return apiRepository.getListCollections(reqObj)
    }

    suspend fun submitInvoiceGivenForOnlinePayment(reqObj:InvoiceGivenForOnlinePaymentRequest) : Response<ResponseBody> {
        return apiRepository.submitInvoiceGivenForOnlinePayment(reqObj)
    }

}
