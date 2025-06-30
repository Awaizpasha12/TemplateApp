package com.app.banuenterprise.network

import com.app.banuenterprise.data.model.request.DayWiseRequest
import com.app.banuenterprise.data.model.request.LoginRequest
import com.app.banuenterprise.data.model.response.CustomerWiseResponse
import com.app.banuenterprise.data.model.response.DayWiseResponse
import com.app.banuenterprise.data.model.response.InvoicesByDayResponse
import com.app.banuenterprise.data.model.response.LoginResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RetrofitInterface {
    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @GET("day-wise-customers")
    suspend fun getDayWiseCustomers(@Query("apikey") apikey: String,
                                    @Query("day") day: String):DayWiseResponse
    @GET("customer-invoices")
    suspend fun getCustomerWiseBill(@Query("apikey") apikey: String,
                                    @Query("customer") customername: String):CustomerWiseResponse
    @GET("invoices-by-day")
    suspend fun getInvoicesByDay(@Query("apikey") apikey: String,
                                    @Query("day") day: String):InvoicesByDayResponse
}