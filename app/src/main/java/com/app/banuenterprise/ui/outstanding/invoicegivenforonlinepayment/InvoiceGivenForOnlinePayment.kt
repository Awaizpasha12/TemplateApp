package com.app.banuenterprise.ui.outstanding.invoicegivenforonlinepayment

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.app.banuenterprise.R
import com.app.banuenterprise.databinding.ActivityInvoiceGivenForOnlinePaymentBinding

class InvoiceGivenForOnlinePayment : AppCompatActivity() {
    lateinit var binding : ActivityInvoiceGivenForOnlinePaymentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvoiceGivenForOnlinePaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}