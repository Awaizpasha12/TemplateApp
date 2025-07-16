package com.app.banuenterprise.ui.salesorder.dashboard

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.app.banuenterprise.R
import com.app.banuenterprise.databinding.ActivitySalesDashboardBinding
import com.app.banuenterprise.ui.login.LoginActivity
import com.app.banuenterprise.ui.outstanding.selectday.SelectDays
import com.app.banuenterprise.ui.salesorder.salescollection.TodaysSalesCollection
import com.app.banuenterprise.ui.salesorder.salesentry.SalesEntryActivity
import com.app.banuenterprise.ui.selectmodule.SelectModuleActivity
import com.app.banuenterprise.utils.SessionUtils

class SalesDashboard : AppCompatActivity() {
    lateinit var binding : ActivitySalesDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySalesDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = "Dashboard"
            setDisplayShowTitleEnabled(true)
        }
        // now you can color the title
        binding.toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white))

        loadListFromLeft()
    }
    fun loadListFromLeft(){
        val handler = Handler(Looper.getMainLooper())
        for (i in 0 until binding.listOptions.childCount) {
            val child = binding.listOptions.getChildAt(i)
            handler.postDelayed({
                val anim = TranslateAnimation(
                    -child.width.toFloat(), 0f,
                    0f, 0f
                ).apply {
                    duration = 300
                    fillAfter = true
                }
                child.startAnimation(anim)
            }, i * 150L)
        }
    }
    fun onOptionSelected(view: View) {
        val tag = view.tag?.toString() ?: "Unknown"
        when (tag) {
            getString(R.string.menu_todays_collection) -> {
                openTodaysCollection()
            }
            getString(R.string.new_sales_order) -> {
                openSalesEntry()
            }
            getString(R.string.menu_change_company) -> {
                openSelectCompany()
            }
            getString(R.string.log_out) -> {
                SessionUtils.deletePrefData(applicationContext)
                openLoginScreen()
            }
            else -> {
                Toast.makeText(this, "Unknown Option", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun openLoginScreen(){
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() // Optional: Ensure the current activity is removed from the stack

    }
    fun openSelectCompany(){
        val intent = Intent(this, SelectModuleActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() // Optional: Ensure the current activity is removed from the stack
    }
    fun openSalesEntry(){
        // Launch DashboardActivity
        val intent = Intent(this, SalesEntryActivity::class.java)
        startActivity(intent)
    }
    fun openTodaysCollection(){
        // Launch DashboardActivity
        val intent = Intent(this, TodaysSalesCollection::class.java)
        startActivity(intent)
    }
}