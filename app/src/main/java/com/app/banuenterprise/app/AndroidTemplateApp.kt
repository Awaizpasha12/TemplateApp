package com.app.banuenterprise.app

import android.app.Application
import android.app.Dialog
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AndroidTemplateApp : Application() {


    override fun onCreate() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        instance = this
        super.onCreate()
    }


    companion object {
        private lateinit var instance: AndroidTemplateApp
        var noDialog: Dialog? = null
        fun getInstance(): AndroidTemplateApp {
            return instance
        }
    }

}