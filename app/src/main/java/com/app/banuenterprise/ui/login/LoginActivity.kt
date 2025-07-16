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
import com.app.banuenterprise.ui.resetpassword.ResetPasswordActivity
import com.app.banuenterprise.ui.salesorder.dashboard.SalesDashboard
import com.app.banuenterprise.ui.selectmodule.SelectModuleActivity
import com.app.banuenterprise.utils.SessionUtils
import com.app.banuenterprise.utils.extentions.LoadingDialog

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
            var userRole = SessionUtils.getUserRole(applicationContext)
            if(userRole.equals("FieldUser"))
                openDashboard()
            else
                openSelectModule()
            return
        }
        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
//            val enteredUrl = binding.etServerUrl.text.toString().trim()
//            if (enteredUrl.isNotEmpty() && (enteredUrl.startsWith("http://") || enteredUrl.startsWith("https://"))) {
//                SessionUtils.setBaseUrl(this, enteredUrl)
//            }
            LoadingDialog.show(this,"Logging in please wait")
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()
            viewModel.login(username, password)
        }
//        binding.tvResetPassword.setOnClickListener {
//            val intent = Intent(this, ResetPasswordActivity::class.java)
//            startActivity(intent)
//        }
    }

    private fun observeViewModel() {
        viewModel.loginResult.observe(this) { result ->
            LoadingDialog.hide()
            if (result.isSuccess == true) {
                sharedPref.edit { putString("apikey", result.token) }
                var userRole = result.user?.roles?.get(0) ?: "FieldUser";
//                var userRole = "Admin"
                sharedPref.edit(){putString("userRole", userRole)}
                if(userRole.equals("FieldUser"))
                    openDashboard()
                else
                    openSelectModule()
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
    fun openSalesDashboard(){
        // Launch DashboardActivity
        val intent = Intent(this, SalesDashboard::class.java)
        startActivity(intent)
        this.finish()
    }
    fun openSelectModule(){
        // Launch DashboardActivity
        val intent = Intent(this, SelectModuleActivity::class.java)
        startActivity(intent)
        this.finish()
    }
}
