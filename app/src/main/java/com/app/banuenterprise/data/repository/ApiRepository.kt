package com.app.banuenterprise.data.repository


import android.content.Context.MODE_PRIVATE
import com.app.banuenterprise.data.model.request.ChangePasswordRequest
import com.app.banuenterprise.data.model.request.CollectionListRequest
import com.app.banuenterprise.data.model.request.CustomerWiseRequest
import com.app.banuenterprise.data.model.request.DayWiseRequest
import com.app.banuenterprise.data.model.request.InvoiceGivenForOnlinePaymentRequest
import com.app.banuenterprise.data.model.request.LoginRequest
import com.app.banuenterprise.data.model.request.OnlyApiKeyRequest
import com.app.banuenterprise.data.model.request.ReceiptEntryRequest
import com.app.banuenterprise.data.model.request.SalesEntryRequest
import com.app.banuenterprise.data.model.response.ChangePasswordResponse
import com.app.banuenterprise.data.model.response.CollectionListResponse
import com.app.banuenterprise.data.model.response.CustomerWiseResponse
import com.app.banuenterprise.data.model.response.DayWiseResponse
import com.app.banuenterprise.data.model.response.InvoicesByDayResponse
import com.app.banuenterprise.data.model.response.LoginResponse
import com.app.banuenterprise.data.model.response.SalesEntryResponse
import com.app.banuenterprise.data.model.response.SalesOrderListResponse
import com.app.banuenterprise.data.model.response.SalesSettingsResponse
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
    suspend fun dayWiseCustomerNew(reqObj : DayWiseRequest) : DayWiseResponse {
        return apiRepository.getDayWiseCustomers(reqObj)
    }
    suspend fun customerWiseBill(apikey:String,customerId: String,day : Int) : CustomerWiseResponse {
        return apiRepository.getCustomerWiseBill(CustomerWiseRequest(apikey,customerId,day))
    }
    suspend fun customerWiseBillNew(reqObj :CustomerWiseRequest) : CustomerWiseResponse {
        return apiRepository.getCustomerWiseBill(reqObj)
    }

    suspend fun invoiceByDay(apikey:String,day: Int) : Response<ResponseBody> {
        return apiRepository.getInvoicesByDay(DayWiseRequest(apikey,day,true))
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
    suspend fun getSalesSetting(req : OnlyApiKeyRequest) : SalesSettingsResponse {
        return apiRepository.getSalesSetting(req)
    }
    suspend fun addSalesEntry(req : SalesEntryRequest) : SalesEntryResponse {
        return apiRepository.addSalesEntry(req)
    }

    suspend fun getSalesList(reqObj:CollectionListRequest) : SalesOrderListResponse {
        return apiRepository.getSalesList(reqObj)
    }
    suspend fun changePassword(reqObj:ChangePasswordRequest) : ChangePasswordResponse {
        return apiRepository.changePassword(reqObj)
    }
}
