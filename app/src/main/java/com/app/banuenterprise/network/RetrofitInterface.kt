package com.app.banuenterprise.network

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
import com.app.banuenterprise.data.model.response.LedgerResponse
import com.app.banuenterprise.data.model.response.LoginResponse
import com.app.banuenterprise.data.model.response.SalesEntryResponse
import com.app.banuenterprise.data.model.response.SalesOrderListResponse
import com.app.banuenterprise.data.model.response.SalesSettingsResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RetrofitInterface {
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @POST("outstanding/day-wise-customers")
    suspend fun getDayWiseCustomers(@Body req : DayWiseRequest):DayWiseResponse
    @POST("outstanding/customer-invoices")
    suspend fun getCustomerWiseBill(@Body req : CustomerWiseRequest):CustomerWiseResponse
    @POST("outstanding/invoices-by-day")
    suspend fun getInvoicesByDay(@Body req : DayWiseRequest): Response<ResponseBody>

    @POST("outstanding/add-collection")
    suspend fun submitReceiptEntry(@Body req : ReceiptEntryRequest):Response<ResponseBody>


    @POST("outstanding/list-collections")
    suspend fun getListCollections(@Body req : CollectionListRequest):CollectionListResponse

    @POST("outstanding/add-online-payment-invoice")
    suspend fun submitInvoiceGivenForOnlinePayment(@Body req : InvoiceGivenForOnlinePaymentRequest):Response<ResponseBody>

    @POST("sales-order/list-settings")
    suspend fun getSalesSetting(@Body req : OnlyApiKeyRequest):SalesSettingsResponse

    @POST("sales-order/add-sales-entry")
    suspend fun addSalesEntry(@Body req : SalesEntryRequest): SalesEntryResponse

    @POST("sales-order/list-sales-entry")
    suspend fun getSalesList(@Body req : CollectionListRequest):SalesOrderListResponse

    @POST("change-password")
    suspend fun changePassword(@Body req : ChangePasswordRequest):ChangePasswordResponse

    @POST("outstanding/customer-outstanding")
    suspend fun getLedgerReport(@Body req : OnlyApiKeyRequest): LedgerResponse
}