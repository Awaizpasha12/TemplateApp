package com.app.banuenterprise.ui.resetpassword

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.app.banuenterprise.data.model.request.ChangePasswordRequest
import com.app.banuenterprise.data.model.response.ChangePasswordResponse
import com.app.banuenterprise.databinding.ActivityResetPasswordBinding
import com.app.banuenterprise.utils.SessionUtils
import com.app.banuenterprise.utils.extentions.LoadingDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding
    val viewModel : ResetPasswordViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.changePasswordRequest.observe(this) { result ->
            LoadingDialog.hide()
            if (result.isSuccess) {
                Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnSubmitChange.setOnClickListener {
            val newPassword = binding.etNewPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()
            // Validation
            when {
                newPassword.isBlank() -> binding.tilNewPassword.error = "Enter new password"
                confirmPassword.isBlank() -> binding.tilConfirmPassword.error = "Confirm new password"
                newPassword != confirmPassword -> binding.tilConfirmPassword.error = "Passwords do not match"
                else -> {
                    clearAllErrors()
                    val request = ChangePasswordRequest(
                        apiKey = SessionUtils.getApiKey(applicationContext),
                        newPassword = newPassword
                    )
                    changePassword(request)
                }
            }
        }
    }

    private fun clearAllErrors() {
        binding.tilNewPassword.error = null
        binding.tilConfirmPassword.error = null
    }

    private fun changePassword(req : ChangePasswordRequest) {
        LoadingDialog.show(this, "Changing password...")
        viewModel.changePassword(req)
    }
}
