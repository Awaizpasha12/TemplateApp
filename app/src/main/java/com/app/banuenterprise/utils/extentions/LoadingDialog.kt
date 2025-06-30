package com.app.banuenterprise.utils.extentions


import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.app.banuenterprise.databinding.DialogLoadingBinding

object LoadingDialog {
    private var dialog: Dialog? = null

    fun show(activity: Activity, message: String? = null) {
        if (dialog != null && dialog?.isShowing == true) return

        dialog = Dialog(activity)
        val binding = DialogLoadingBinding.inflate(LayoutInflater.from(activity))
        dialog?.setContentView(binding.root)
        dialog?.setCancelable(false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        message?.let { binding.tvLoadingMessage.text = it }
        dialog?.show()
    }

    fun hide() {
        dialog?.dismiss()
        dialog = null
    }
}
