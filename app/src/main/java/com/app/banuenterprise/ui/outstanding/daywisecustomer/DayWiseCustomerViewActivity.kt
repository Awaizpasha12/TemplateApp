package com.app.banuenterprise.ui.outstanding.daywisecustomer

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.banuenterprise.R
import com.app.banuenterprise.databinding.ActivityDayWiseCustomerViewBinding
import com.app.banuenterprise.ui.outstanding.customerwisebills.CustomerWiseBillActivity
import com.app.banuenterprise.ui.outstanding.daywisecustomer.adapter.DayWiseAdapter
import com.app.banuenterprise.utils.SessionUtils
import com.app.banuenterprise.utils.extentions.LoadingDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DayWiseCustomerViewActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDayWiseCustomerViewBinding
    private val viewModel : DayWiseCustomerViewModel by viewModels()
    private var adapter: DayWiseAdapter? = null
    private var daySelected : String = "";
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDayWiseCustomerViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        daySelected = intent.extras?.getString("day") ?: ""
        // Set up the Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = daySelected
        binding.toolbar.setTitleTextColor(ContextCompat.getColor(this@DayWiseCustomerViewActivity, android.R.color.white))
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        LoadingDialog.show(this, "Please wait...")
        // Observe LiveData
        viewModel.dayWiseResult.observe(this, Observer { response ->
            LoadingDialog.hide();
            if (response.isSuccess && response.data != null) {
                if(response.data != null && response.data.size > 0) {
                    adapter = DayWiseAdapter(response.data){ customerSelected ->
                        val intent = Intent(this, CustomerWiseBillActivity::class.java)
                        intent.putExtra("customerSelected",customerSelected.customer)
                        startActivity(intent)
                    }
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
        viewModel.getDetails(SessionUtils.getApiKey(this), daySelected) // TODO: Replace with real credentials
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
