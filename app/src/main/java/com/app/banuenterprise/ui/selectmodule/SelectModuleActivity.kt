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

        userRole = SessionUtils.getUserRole(applicationContext)
        if(userRole == "1"){
            openDashboard()
        }

    }
    fun openDashboard(){
        // Launch DashboardActivity
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        this.finish()
    }
}