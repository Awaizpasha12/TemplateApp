package com.app.banuenterprise.utils

import android.content.Context

object SessionUtils {
    fun getApiKey(context: Context): String {
        val sp = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
        return sp.getString("apikey", "") ?: ""
    }
    fun getUserRole(context: Context) : String {
        val sp = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
        return sp.getString("userRole", "") ?: ""
    }
    // Function to delete all SharedPreferences data
    fun deletePrefData(context: Context) {
        val sp = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.clear()  // Clears all the stored data
        editor.apply()  // Apply changes asynchronously
    }
}
