package com.app.banuenterprise.ui.outstanding.invoicegivenforonlinepayment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.app.banuenterprise.R
import com.app.banuenterprise.data.model.request.InvoiceGivenForOnlinePaymentRequest
import com.app.banuenterprise.data.model.request.InvoiceItem
import com.app.banuenterprise.databinding.ActivityInvoiceGivenForOnlinePaymentBinding
import com.app.banuenterprise.ui.outstanding.customerwisebills.CustomerWiseBillActivity
import com.app.banuenterprise.ui.outstanding.daywisecustomer.adapter.DayWiseAdapter
import com.app.banuenterprise.utils.SessionUtils
import com.app.banuenterprise.utils.SupportMethods
import com.app.banuenterprise.utils.extentions.AppAlertDialog
import com.app.banuenterprise.utils.extentions.LoadingDialog
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.time.Instant
import java.util.Date

@AndroidEntryPoint
class InvoiceGivenForOnlinePayment : AppCompatActivity() {
    lateinit var binding : ActivityInvoiceGivenForOnlinePaymentBinding
    val viewModel : InvoiceGivenForOnlinePaymentViewModel by viewModels()
    var invoiceNumberMap : HashMap<String, JSONObject> = java.util.HashMap()
    private var selectedInvoiceKey: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvoiceGivenForOnlinePaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Set up the Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Invoice given for online payment"
        binding.toolbar.setTitleTextColor(ContextCompat.getColor(this@InvoiceGivenForOnlinePayment, android.R.color.white))

        LoadingDialog.show(this, "Please wait...")
        // Observe LiveData
        viewModel.billMap.observe(this, Observer { response ->
            invoiceNumberMap = response
            LoadingDialog.hide();
        })
        // Call your API (provide username/password or get from session)
        viewModel.fetchInvoicesByDay(SessionUtils.getApiKey(this))
        binding.tvSelectInvoice.setOnClickListener {
            if (invoiceNumberMap.isNotEmpty()) {
                showInvoiceSelector()
            }else{
                AppAlertDialog.show(this,"No invoices scheduled today")
            }
        }
        binding.btnSubmit.setOnClickListener {
            if (selectedInvoiceKey.isNullOrEmpty() || binding.tvSelectInvoice.text.toString() == "Select Invoice") {
                AppAlertDialog.show(this, "Please select an invoice")
                return@setOnClickListener
            }


            if (!binding.checkboxPromise.isChecked) {
                AppAlertDialog.show(this,"Please confirm the customer's agreement")
                return@setOnClickListener
            }

            val invoiceData = invoiceNumberMap[selectedInvoiceKey]
            if (invoiceData != null) {
                LoadingDialog.show(this, "submitting")
                val billItemId = invoiceData.optString("billItemId", "")
                val date = SupportMethods.getCurrentDateFormatted()
//                val date = Instant.now().toString()
                val InvoiceItem = InvoiceItem(billItemId,date.toString())
                var list:ArrayList<InvoiceItem> = ArrayList()
                list.add(InvoiceItem)
                val req = InvoiceGivenForOnlinePaymentRequest(SessionUtils.getApiKey(applicationContext),list)
                viewModel.submitReceiptEntry(req)
            }
        }
        viewModel.receiptSubmissionResult.observe(this) { result ->
            LoadingDialog.hide()
            val (success, message) = result
            if (success) {
                // Show success Toast/snackbar, clear form, etc.
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                this.finish()
            } else {
                // Show error Toast/snackbar
                AppAlertDialog.show(this,message)
            }
        }
    }
    private fun showInvoiceSelector() {
        val invoiceList = invoiceNumberMap.keys.toList()
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Invoice")

        val input = EditText(this).apply {
            hint = "Search Invoice..."
        }

        val listView = ListView(this)
        var currentList = invoiceList.toMutableList()

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, currentList)
        listView.adapter = adapter

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 16)
            addView(input)
            addView(listView)
        }

        val dialog = builder.setView(container).create()

        input.addTextChangedListener { text ->
            val query = text.toString()
            val filteredList = invoiceList.filter { it.contains(query, ignoreCase = true) }

            currentList.clear()
            currentList.addAll(filteredList)
            adapter.notifyDataSetChanged()
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedKey = adapter.getItem(position) ?: return@setOnItemClickListener
            setSelectedInvoice(selectedKey)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setSelectedInvoice(key: String) {
        selectedInvoiceKey = key
        val data = invoiceNumberMap[key]
        if (data != null) {
            binding.tvSelectInvoice.text = key
            binding.tvCustomerName.text = data.optString("customerName", "-")
            binding.tvBrand.text = data.optString("brand", "-")
            binding.tvAmount.text = data.optString("amount", "-")
        }
    }


}