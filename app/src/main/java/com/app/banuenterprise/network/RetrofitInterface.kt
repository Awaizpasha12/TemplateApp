package com.app.banuenterprise.network

import com.app.banuenterprise.data.model.request.CustomerWiseRequest
import com.app.banuenterprise.data.model.request.DayWiseRequest
import com.app.banuenterprise.data.model.request.LoginRequest
import com.app.banuenterprise.data.model.response.CustomerWiseResponse
import com.app.banuenterprise.data.model.response.DayWiseResponse
import com.app.banuenterprise.data.model.response.InvoicesByDayResponse
import com.app.banuenterprise.data.model.response.LoginResponse
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
}