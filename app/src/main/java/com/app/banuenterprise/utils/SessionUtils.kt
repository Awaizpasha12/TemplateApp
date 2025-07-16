package com.app.banuenterprise.utils

import android.content.Context
import com.app.banuenterprise.data.model.response.Ledger
import com.app.banuenterprise.data.model.response.SalesSettingsResponse
import com.app.banuenterprise.data.model.response.StockItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object SessionUtils {

    private const val PREFERENCE_NAME = "user_session"
    private const val KEY_API_KEY = "apikey"
//    private const val KEY_BASE_URL = "base_url"
//    private const val DEFAULT_BASE_URL = "http://banuenterprise.duckdns.org/api/app/"
//
//    fun getBaseUrl(context: Context): String {
//        val sp = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
//        return sp.getString(KEY_BASE_URL, DEFAULT_BASE_URL) ?: DEFAULT_BASE_URL
//    }
//
//    fun setBaseUrl(context: Context, url: String) {
//        val sp = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
//        sp.edit().putString(KEY_BASE_URL, url).apply()
//    }
    // Get the API key
    fun getApiKey(context: Context): String {
        val sp = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        return sp.getString(KEY_API_KEY, "") ?: ""
    }

    // Get the User Role
    fun getUserRole(context: Context): String {
        val sp = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        return sp.getString("userRole", "FieldUser") ?: ""
    }

    // Save sales settings to cache
    fun saveSalesSettings(context: Context, data: SalesSettingsResponse) {
        val sp = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        val editor = sp.edit()
        val gson = Gson()
        val json = gson.toJson(data)  // Convert object to JSON string
        editor.putString("sales_settings", json)
        editor.apply()
    }

    // Get cached sales settings
    fun getSalesSettings(context: Context): SalesSettingsResponse? {
        val sp = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sp.getString("sales_settings", null)
        return gson.fromJson(json, SalesSettingsResponse::class.java)  // Convert JSON string back to object
    }

    // Clear cached data
    fun clearSalesSettings(context: Context) {
        val sp = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.remove("sales_settings")
        editor.apply()
    }

    // Function to delete all SharedPreferences data
    fun deletePrefData(context: Context) {
        val sp = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.clear()  // Clears all the stored data
        editor.apply()  // Apply changes asynchronously
    }
}
