package com.app.banuenterprise.ui.outstanding.ledger
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.app.banuenterprise.databinding.ActivityLedgerReportBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LedgerReport : AppCompatActivity() {
    lateinit var binding : ActivityLedgerReportBinding
    val viewModel : LedgerReportViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLedgerReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}