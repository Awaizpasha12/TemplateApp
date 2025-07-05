package com.app.banuenterprise.ui.outstanding.invoicenumberentry

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.banuenterprise.R
import com.app.banuenterprise.data.model.request.BillItemRequest
import com.app.banuenterprise.data.model.request.ReceiptEntryRequest
import com.app.banuenterprise.data.model.response.BillItem
import com.app.banuenterprise.databinding.ActivityInvoiceNumberEntryBinding
import com.app.banuenterprise.supabase.SimpleSupabaseUploader
import com.app.banuenterprise.ui.outstanding.invoicenumberentry.adapter.InvoiceEntryGroupAdapter
import com.app.banuenterprise.utils.SessionUtils
import com.app.banuenterprise.utils.SupportMethods
import com.app.banuenterprise.utils.extentions.AppAlertDialog
import com.app.banuenterprise.utils.extentions.LoadingDialog
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.io.File

@AndroidEntryPoint
class InvoiceNumberEntryActivity : AppCompatActivity() {
    lateinit var binding : ActivityInvoiceNumberEntryBinding
    val viewModel : InvoiceNumberEntryViewModel by viewModels()
    private lateinit var adapter: InvoiceEntryGroupAdapter
    private val CAMERA_REQUEST_CODE = 1001
    private val GALLERY_REQUEST_CODE = 1002
    private var proofUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvoiceNumberEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        LoadingDialog.show(this, "Please wait...")
        // Observe LiveData
        viewModel.billMap.observe(this, Observer { response ->
            setupRecyclerView(response)
            LoadingDialog.hide();
        })
        // Call your API (provide username/password or get from session)
        viewModel.fetchInvoicesByDay(SessionUtils.getApiKey(this))
        // Payment method spinner
        val paymentMethods = listOf("cash", "upi", "bank", "cheque")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, paymentMethods)
        binding.spinnerPaymentMethod.adapter = spinnerAdapter

// Set the listener for the Spinner
        binding.spinnerPaymentMethod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // No action needed
            }

            override fun onItemSelected(parentView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Get selected payment method
                val selectedMethod = parentView?.getItemAtPosition(position)?.toString() ?: ""

                showProofImage(null)

                // Show correct dialog based on the selected payment method
                if (selectedMethod == "cash" || selectedMethod == "cheque") {
                    // Enable camera option
                    binding.btnProof.text = "Add Proof (Camera)"
                } else {
                    // Enable gallery option
                    binding.btnProof.text = "Add Proof (Camera or Gallery)"
                }
            }
        }
        binding.btnProof.setOnClickListener {
            val method = binding.spinnerPaymentMethod.selectedItem?.toString() ?: ""

            // Camera permission check
            if (method == "cash" || method == "cheque" ||
                (method == "upi" || method == "bank")) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 10)
                    return@setOnClickListener
                }
            }

            // Gallery/image permission check
            if (method == "upi" || method == "bank") {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), 11)
                        return@setOnClickListener
                    }
                } else {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 11)
                        return@setOnClickListener
                    }
                }
            }

            // Proof logic
            if (method == "cash" || method == "cheque") {
                openCamera()
            } else if (method == "upi" || method == "bank") {
                showProofPickerDialog()
            } else {
                AppAlertDialog.show(this, "Please select a payment method first.")
            }
        }
        binding.btnSubmit.setOnClickListener {
            // Validate at least one group
            if (adapter.items.isEmpty()) {
                AppAlertDialog.show(this, "Please add at least one invoice")
                return@setOnClickListener
            }
            // Validate each group
            for ((idx, entry) in adapter.items.withIndex()) {
                if (entry.invoiceNumber.isBlank()) {
                    AppAlertDialog.show(this, "Invoice not selected in group ${idx + 1}")
                    return@setOnClickListener
                }
                if (entry.amount <= 0.0) {
                    AppAlertDialog.show(this, "Amount must be greater than 0 for bill number ${entry.invoiceNumber}")
                    return@setOnClickListener
                }
                if (entry.amount > entry.defaultAmount) {
                    AppAlertDialog.show(
                        this,
                        "Entered amount (${entry.amount}) for bill number ${entry.invoiceNumber} cannot exceed original invoice amount (${entry.defaultAmount})"
                    )
                    return@setOnClickListener
                }
            }
            if(proofUri == null || proofUri.toString().equals("")){
                AppAlertDialog.show(this, "Attaching proof is mandatory")
                return@setOnClickListener
            }
            // Optionally: Validate remarks, payment method, proof
            val remarks = binding.etRemarks.text?.toString() ?: ""
            val paymentMethod = binding.spinnerPaymentMethod.selectedItem?.toString() ?: ""
            if (paymentMethod.isBlank()) {
                AppAlertDialog.show(this, "Please select a payment method")
                return@setOnClickListener
            }
            // TODO: Validate proof if required
            if (proofUri == null) {
                AppAlertDialog.show(this, "Please attach proof")
                return@setOnClickListener
            }
            LoadingDialog.show(this, "submitting")
            SimpleSupabaseUploader.uploadImage(
                context = this,       // your Activity or Fragment’s context
                uri     = proofUri!!  // the Uri you want to upload
            ) { success, publicUrl ->
                if (success && publicUrl != null) {
                    val billItems = adapter.items.map {
                        BillItemRequest(
                            billItemId = it.billItemId,
                            amount = it.amount
                        )
                    }

                    val request = ReceiptEntryRequest(
                        billItems = billItems,
                        mode = paymentMethod,
                        collectedDate = SupportMethods.getCurrentDateFormatted(),
                        proofUrl = publicUrl,
                        remarks = remarks,
                        token = SessionUtils.getApiKey(this)
                    )
                    viewModel.submitReceiptEntry(request)

                } else {
                    LoadingDialog.hide();
                    // Upload failed
                    Toast.makeText(this, "image syncing failed please retry adding proof", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.receiptSubmissionResult.observe(this) { result ->
            LoadingDialog.hide()
            val (success, message) = result
            if (success) {
                // Show success Toast/snackbar, clear form, etc.
                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                this.finish()
            } else {
                // Show error Toast/snackbar
                AppAlertDialog.show(applicationContext,message)
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 10 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Camera permission granted, proceed
            openCamera()
        }
        if (requestCode == 11 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Gallery/image permission granted, open gallery if needed
            openGallery()
        }
    }

    private fun showProofPickerDialog() {
        val options = arrayOf("Camera", "Gallery")
        AlertDialog.Builder(this)
            .setTitle("Select Proof")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }
            .show()
    }
    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val imageUri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            createImageFile()
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        proofUri = imageUri
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }
    private fun createImageFile(): File {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "proof_${System.currentTimeMillis()}_", ".jpg", storageDir
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    // proofUri contains the captured image URI
                    showProofImage(proofUri)
                }
                GALLERY_REQUEST_CODE -> {
                    proofUri = data?.data
                    showProofImage(proofUri)
                }
            }
        }
    }
    private fun showProofImage(uri: Uri?) {
        if (uri != null) {
            binding.llProofImage.visibility = View.VISIBLE
            binding.ivProofPreview.setImageURI(uri)
            // Or just keep proofUri for API upload
        } else {
            proofUri = null;
//            AppAlertDialog.show(this, "No image selected!")
            binding.llProofImage.visibility = View.GONE
        }
    }


    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun setupRecyclerView(availableInvoices: List<BillItem>) {
        adapter = InvoiceEntryGroupAdapter(availableInvoices) {
            binding.tvTotalAmount.text = "Total: ${adapter.items.sumOf { it.amount }}"
        }
        binding.rvInvoiceGroups.layoutManager = LinearLayoutManager(this)
        binding.rvInvoiceGroups.adapter = adapter

        binding.btnAddInvoiceGroup.setOnClickListener {
            adapter.addGroup()
        }
    }

}