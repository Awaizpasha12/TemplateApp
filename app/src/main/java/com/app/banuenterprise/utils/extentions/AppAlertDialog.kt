package com.app.banuenterprise.utils.extentions

import android.app.Activity
import android.app.AlertDialog
import android.content.Context

object AppAlertDialog {
    fun show(context: Context, message: String, onOk: (() -> Unit)? = null) {
        if ((context as? Activity)?.isFinishing == true) return // Don't show if activity is finishing

        val builder = AlertDialog.Builder(context)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                onOk?.invoke()
            }
        builder.show()
    }
}
