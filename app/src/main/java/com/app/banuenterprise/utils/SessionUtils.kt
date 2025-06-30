package com.app.banuenterprise.utils

import android.content.Context

object SessionUtils {
    fun getApiKey(context: Context): String {
        val sp = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
        return sp.getString("apikey", "") ?: ""
    }
}
