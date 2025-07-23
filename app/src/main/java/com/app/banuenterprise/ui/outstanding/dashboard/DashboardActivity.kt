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
import com.app.banuenterprise.ui.login.LoginActivity
import com.app.banuenterprise.ui.outstanding.invoicegivenforonlinepayment.InvoiceGivenForOnlinePayment
import com.app.banuenterprise.ui.outstanding.invoicegivenforonlinepayment.InvoiceGivenForOnlinePaymentViewModel
import com.app.banuenterprise.ui.outstanding.invoicenumberentry.InvoiceNumberEntryActivity
import com.app.banuenterprise.ui.outstanding.ledger.LedgerReport
import com.app.banuenterprise.ui.outstanding.receiptEntry.ReceiptEntry
import com.app.banuenterprise.ui.outstanding.selectday.SelectDays
import com.app.banuenterprise.ui.outstanding.todayscollection.TodaysCollection
import com.app.banuenterprise.ui.resetpassword.ResetPasswordActivity
import com.app.banuenterprise.ui.selectmodule.SelectModuleActivity
import com.app.banuenterprise.utils.SessionUtils

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
        var userRole = SessionUtils.getUserRole(applicationContext)
        if(userRole.equals("FieldUser"))
            binding.llchangeCompany.visibility = View.GONE
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
                openTodaysCollection()
            }
            getString(R.string.menu_receipt_entry) -> {
                openReceiptEntry()
            }
            getString(R.string.menu_invoice_number_entry) -> {
               openInvoiceEntry()
            }
            getString(R.string.menu_invoice_online) -> {
                openInvoiceGivenForOnlinePayment();
            }
            getString(R.string.menu_change_company) -> {
                openSelectCompany()
            }
            getString(R.string.log_out) -> {
                SessionUtils.deletePrefData(applicationContext)
                openLoginScreen()
            }
            getString(R.string.reset_password) -> {
                openResetPassword()
            }
            getString(R.string.menu_ledger) -> {
                openLedger()
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
    fun openResetPassword(){
        val intent = Intent(this, ResetPasswordActivity::class.java)
        startActivity(intent)
    }
    fun openLedger(){
        val intent = Intent(this, LedgerReport::class.java)
        startActivity(intent)
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
    fun openTodaysCollection(){
        val intent = Intent(this, TodaysCollection::class.java)
        startActivity(intent)
    }
    fun openInvoiceGivenForOnlinePayment(){
        val intent = Intent(this, InvoiceGivenForOnlinePayment::class.java)
        startActivity(intent)
    }
    fun openInvoiceEntry(){
        val intent = Intent(this, InvoiceNumberEntryActivity::class.java)
        startActivity(intent)

    }
}