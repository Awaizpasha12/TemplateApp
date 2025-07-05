package com.app.banuenterprise.ui.outstanding.receiptEntry

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.banuenterprise.data.model.request.BillItemRequest
import com.app.banuenterprise.data.model.request.ReceiptEntryRequest
import com.app.banuenterprise.data.model.response.BillItem
import com.app.banuenterprise.data.model.response.InvoiceDetail
import com.app.banuenterprise.databinding.ActivityReceiptEntryBinding
import com.app.banuenterprise.supabase.SimpleSupabaseUploader
import com.app.banuenterprise.ui.outstanding.receiptEntry.adapter.ReceiptEntryGroupAdapter
import com.app.banuenterprise.utils.SessionUtils
import com.app.banuenterprise.utils.SupportMethods
import com.app.banuenterprise.utils.extentions.AppAlertDialog
import com.app.banuenterprise.utils.extentions.LoadingDialog
import com.app.banuenterprise.utils.simpleadapters.SimpleStringListAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class ReceiptEntry : AppCompatActivity() {
    private lateinit var binding: ActivityReceiptEntryBinding
    private val viewModel: ReceiptEntryViewModel by viewModels()
    private var customerNameCustomerId: MutableMap<String, String> = mutableMapOf()
    private var selectedCustomer: String? = null
    private lateinit var adapter: ReceiptEntryGroupAdapter
    private val CAMERA_REQUEST_CODE = 1001
    private val GALLERY_REQUEST_CODE = 1002
    private var proofUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiptEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        LoadingDialog.show(this, "Please wait...")

        viewModel.getAllCustomer(SessionUtils.getApiKey(this))
        viewModel.allCustomerDetails.observe(this) { response ->
            if (response != null && response.isSuccess) {

//                customerNameInvoiceMap = response.data ?: emptyMap()
//                customerNamesList = customerNameInvoiceMap.keys.toList()

                // Check if customers list is not null and contains at least one customer
                if (response.customers != null && response.customers.isNotEmpty()) {
                    for (cus in response.customers) {
                        // Check if customerId and customerName are not null
                        if (cus.customerId != null) {
                            customerNameCustomerId[cus.customerId] = cus.customerName
                        }
                    }
                }

                setupRecyclerView()
            } else {
                Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
            LoadingDialog.hide()
        }

        binding.tvCustomerName.setOnClickListener {
            // Create a list of customer names for display in the dialog
            val customerNamesList = customerNameCustomerId.values.toList()

            // Show the dialog
            showSearchableDialog(
                title = "Select Customer",
                data = customerNamesList
            ) { selectedCustomerName ->
                // Retrieve the customerId based on the selected customer name
                val selectedCustomerId = customerNameCustomerId.filterValues { it == selectedCustomerName }
                    .keys
                    .firstOrNull()

                if (selectedCustomerId != null) {
                    selectedCustomer = selectedCustomerName
                    binding.tvCustomerName.text = selectedCustomerName
                    LoadingDialog.show(this,"Loading customer details please wait")
                    viewModel.getCustomerWiseDetails(SessionUtils.getApiKey(applicationContext),selectedCustomerId)
                }
            }
        }



        viewModel.customerWiseResult.observe(this) { response ->
            LoadingDialog.hide()
            if (response != null && response.isSuccess) {
                var allInvoiceList :  List<BillItem> = ArrayList()
                if(response.invoices != null && response.invoices.size > 0){
                    allInvoiceList = response.invoices
                }
                // Reset groups when customer changes!
                adapter.clearAll()
                adapter.setAvailableInvoices(allInvoiceList)
                binding.btnAddGroup.isEnabled = true // Now allow adding groups
                // Optionally, auto-add first group
                adapter.addGroup()
            } else {
                Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
            LoadingDialog.hide()
        }

        // 3. Disable add-group button until customer selected
        binding.btnAddGroup.isEnabled = false

        binding.btnAddGroup.setOnClickListener {
            if (selectedCustomer.isNullOrBlank()) {
                Toast.makeText(this, "Please select a customer first", Toast.LENGTH_SHORT).show()
            } else {
                adapter.addGroup()
            }
        }

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

        binding.btnSubmit.setOnClickListener {
            // Validate customer
            if (selectedCustomer.isNullOrBlank()) {
                AppAlertDialog.show(this, "Please select a customer")
                return@setOnClickListener
            }
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
            if(proofUri == null){
                if (paymentMethod.isBlank()) {
                    AppAlertDialog.show(this, "Please attach proof")
                    return@setOnClickListener
                }
            }
            LoadingDialog.show(this,"submitting")
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
    // inside an Activity or Fragment…


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

    private fun showSearchableDialog(
        title: String,
        data: List<String>,
        onItemSelected: (String) -> Unit
    ) {
        val ctx = this
        val dialogView = LayoutInflater.from(ctx).inflate(com.app.banuenterprise.R.layout.layout_dialog_search_list, null)
        val etSearch = dialogView.findViewById<EditText>(com.app.banuenterprise.R.id.etSearch)
        val rvList = dialogView.findViewById<RecyclerView>(com.app.banuenterprise.R.id.rvList)

        lateinit var alertDialog: androidx.appcompat.app.AlertDialog

        val adapter = SimpleStringListAdapter(data) { selected ->
            onItemSelected(selected)
            alertDialog.dismiss()
        }
        rvList.layoutManager = LinearLayoutManager(ctx)
        rvList.adapter = adapter

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val filtered = data.filter { it.contains(s.toString(), ignoreCase = true) }
                adapter.updateData(filtered)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        alertDialog = MaterialAlertDialogBuilder(ctx)
            .setTitle(title)
            .setView(dialogView)
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setupRecyclerView() {
        adapter = ReceiptEntryGroupAdapter(
            availableInvoices = emptyList(),
            onListChanged = {
                // Update total
                binding.tvTotalAmount.text = "Total: ${adapter.items.sumOf { it.amount }}"
            }
        )
        binding.rvGroups.layoutManager = LinearLayoutManager(this)
        binding.rvGroups.adapter = adapter
    }
}
