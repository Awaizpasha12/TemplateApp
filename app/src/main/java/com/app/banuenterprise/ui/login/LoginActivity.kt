package com.app.banuenterprise.ui.login


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.app.banuenterprise.databinding.ActivityLoginBinding
import com.app.banuenterprise.ui.outstanding.dashboard.DashboardActivity
import com.app.banuenterprise.utils.Constants
import com.app.banuenterprise.utils.DataStorePrefUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import androidx.core.content.edit

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()      // keeps the splash alive
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
        val savedApiKey = sharedPref.getString("apikey", null)
        if (!savedApiKey.isNullOrEmpty()) {
            openDashboard()
            return // skip login screen
        }
        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()
            viewModel.login(username, password)
        }
    }

    private fun observeViewModel() {
        viewModel.loginResult.observe(this) { result ->
            if (result.isSuccess) {
                sharedPref.edit { putString("apikey", result.apikey) }
                // Navigate to DashboardActivity or next screen
                openDashboard()
            } else {
                Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun openDashboard(){
        // Launch DashboardActivity
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        this.finish()
    }
}
