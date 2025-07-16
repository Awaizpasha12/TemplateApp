package com.app.banuenterprise.ui.selectmodule

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.app.banuenterprise.R
import com.app.banuenterprise.databinding.ActivitySelectModuleBinding
import com.app.banuenterprise.ui.outstanding.dashboard.DashboardActivity
import com.app.banuenterprise.ui.salesorder.dashboard.SalesDashboard
import com.app.banuenterprise.utils.SessionUtils

class SelectModuleActivity : AppCompatActivity() {
    lateinit var binding : ActivitySelectModuleBinding
    var userRole = "";
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectModuleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Set up the Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Select Company"
        binding.toolbar.setTitleTextColor(ContextCompat.getColor(this@SelectModuleActivity, android.R.color.white))
        binding.cardCompany1.setOnClickListener {
            openDashboard()
        }
        binding.cardCompany2.setOnClickListener {
            openSalesDashboard()
        }
    }
    fun openDashboard(){
        // Launch DashboardActivity
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        this.finish()
    }
    fun openSalesDashboard(){
        // Launch DashboardActivity
        val intent = Intent(this, SalesDashboard::class.java)
        startActivity(intent)
        this.finish()
    }
}