package com.app.banuenterprise.ui.login


import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.app.banuenterprise.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()      // keeps the splash alive
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                // Navigate to DashboardActivity or next screen
            } else {
                Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
