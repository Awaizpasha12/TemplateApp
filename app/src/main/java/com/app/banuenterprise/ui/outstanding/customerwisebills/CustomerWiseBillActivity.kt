package com.app.banuenterprise.ui.outstanding.customerwisebills

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.banuenterprise.R
import com.app.banuenterprise.databinding.ActivityCustomerWiseBillBinding
import com.app.banuenterprise.ui.outstanding.customerwisebills.adapter.CustomerWiseBillAdapter
import com.app.banuenterprise.utils.SessionUtils
import com.app.banuenterprise.utils.extentions.LoadingDialog
import androidx.lifecycle.Observer
import com.app.banuenterprise.ui.outstanding.dashboard.DashboardActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CustomerWiseBillActivity : AppCompatActivity() {
    lateinit var binding : ActivityCustomerWiseBillBinding
    private val viewModel : CustomerWiseBillViewModel by viewModels()
    private var adapter: CustomerWiseBillAdapter? = null
    private var customerSelected : String = "";
    private var customerId : String = "";
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerWiseBillBinding.inflate(layoutInflater)
        setContentView(binding.root)
        customerSelected = intent.extras?.getString("customerSelected") ?: ""
        customerId = intent.extras?.getString("customerId") ?: ""

        // Set up the Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = customerSelected
        binding.toolbar.setTitleTextColor(ContextCompat.getColor(this@CustomerWiseBillActivity, android.R.color.white))
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        LoadingDialog.show(this, "Please wait...")
        // Observe LiveData
        viewModel.customerWiseResult.observe(this, Observer { response ->
            LoadingDialog.hide();
            if (response.isSuccess && response.invoices != null) {
                if(response.invoices != null && response.invoices.size > 0) {
                    adapter = CustomerWiseBillAdapter(response.invoices)
                    binding.recyclerView.adapter = adapter
                }
                else{
                    binding.tvNoItemInList.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                }
            }else{
                binding.tvNoItemInList.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            }
            // Optionally handle empty/error states here
        })

        // Call your API (provide username/password or get from session)
        viewModel.getDetails(SessionUtils.getApiKey(this), customerId,intent.extras?.getInt("daySelected") ?: 0) // TODO: Replace with real credentials

        binding.btnReturn.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish() // Optional: Ensure the current activity is removed from the stack
        }

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? androidx.appcompat.widget.SearchView
        searchView?.queryHint = "Search customer..."

        // Change text and hint color to white
        val searchEditText = searchView?.findViewById<android.widget.EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText?.setTextColor(Color.WHITE)
        searchEditText?.setHintTextColor(Color.WHITE) // translucent white for hint

        searchView?.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter?.filter(newText ?: "")
                if (adapter?.getFilteredListSize() == 0) {
                    binding.tvNoItemInList.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                } else {
                    binding.tvNoItemInList.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                }
                return true
            }
        })


        return true
    }

}