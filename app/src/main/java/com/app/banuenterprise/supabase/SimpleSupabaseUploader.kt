package com.app.banuenterprise.supabase

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.MultipartBody.Part
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

object SimpleSupabaseUploader {
    private const val SUPABASE_URL = "https://qgokuymyzmsghtwmcmid.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFnb2t1eW15em1zZ2h0d21jbWlkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTE1Njg0MzEsImV4cCI6MjA2NzE0NDQzMX0.TxZgr9thWL0VP0TaOzDodGZRTT3kWzrbMjGNxq3KZnE"

    private val client = OkHttpClient()

    /**
     * Uploads the image at [uri] into your "photos" bucket
     * and returns a public URL via [onResult].
     */
    fun uploadImage(
        context: Context,
        uri: Uri,
        onResult: (success: Boolean, publicUrl: String?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val input = context.contentResolver.openInputStream(uri)
                if (input == null) {
                    withContext(Dispatchers.Main) { onResult(false, null) }
                    return@launch
                }
                val bytes = input.readBytes()
                input.close()

                val bucket = "photos"
                val filename = "${System.currentTimeMillis()}_upload.jpg"
                val url = "$SUPABASE_URL/storage/v1/object/$bucket/$filename"
                val requestBody = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())

                val request = Request.Builder()
                    .url(url)
                    .addHeader("apikey", SUPABASE_KEY)
                    .addHeader("Authorization", "Bearer $SUPABASE_KEY")
                    .addHeader("Content-Type", "image/jpeg")
                    .post(requestBody)
                    .build()

                client.newCall(request).execute().use { resp ->
                    withContext(Dispatchers.Main) {
                        if (resp.isSuccessful) {
                            val publicUrl = "$SUPABASE_URL/storage/v1/object/public/$bucket/$filename"
                            onResult(true, publicUrl)
                        } else {
                            onResult(false, null)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    onResult(false, null)
                }
            }
        }
    }

}
