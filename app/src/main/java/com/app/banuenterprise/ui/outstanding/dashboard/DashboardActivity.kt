package com.app.banuenterprise.ui.outstanding.dashboard

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.app.banuenterprise.R
import com.app.banuenterprise.databinding.ActivityDashboardBinding
import com.app.banuenterprise.ui.outstanding.receiptEntry.ReceiptEntry
import com.app.banuenterprise.ui.outstanding.selectday.SelectDays

class DashboardActivity : AppCompatActivity() {
    lateinit var binding : ActivityDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // after setContentView(...)
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
    // This method is linked via `android:onClick="onOptionSelected"` in XML
    fun onOptionSelected(view: View) {
        val tag = view.tag?.toString() ?: "Unknown"
        when (tag) {
            getString(R.string.menu_my_report) -> {
                openMyReport()
            }
            getString(R.string.menu_todays_collection) -> {
                Toast.makeText(this, "Today's Collection selected", Toast.LENGTH_SHORT).show()
                // startActivity(Intent(this, TodaysCollectionActivity::class.java))
            }
            getString(R.string.menu_receipt_entry) -> {
                openReceiptEntry()
            }
            getString(R.string.menu_invoice_number_entry) -> {
                Toast.makeText(this, "Invoice Number Entry selected", Toast.LENGTH_SHORT).show()
                // startActivity(Intent(this, InvoiceNumberEntryActivity::class.java))
            }
            getString(R.string.menu_invoice_online) -> {
                Toast.makeText(this, "Invoice Online selected", Toast.LENGTH_SHORT).show()
                // startActivity(Intent(this, InvoiceOnlineActivity::class.java))
            }
            getString(R.string.menu_change_company) -> {
                Toast.makeText(this, "Change Company selected", Toast.LENGTH_SHORT).show()
                // startActivity(Intent(this, ChangeCompanyActivity::class.java))
            }
            else -> {
                Toast.makeText(this, "Unknown Option", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun openMyReport(){
        // Launch DashboardActivity
        val intent = Intent(this, SelectDays::class.java)
        startActivity(intent)
    }
    fun openReceiptEntry(){
        // Launch DashboardActivity
        val intent = Intent(this, ReceiptEntry::class.java)
        startActivity(intent)
    }
}