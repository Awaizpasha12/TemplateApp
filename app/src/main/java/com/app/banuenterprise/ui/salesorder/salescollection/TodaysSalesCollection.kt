package com.app.banuenterprise.ui.salesorder.salescollection

import com.app.banuenterprise.ui.salesorder.salescollection.adapter.SalesOrderAdapter
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.banuenterprise.R
import com.app.banuenterprise.databinding.ActivityTodaysSalesCollectionBinding
import com.app.banuenterprise.utils.SessionUtils
import com.app.banuenterprise.utils.SupportMethods
import com.app.banuenterprise.utils.extentions.LoadingDialog
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class TodaysSalesCollection : AppCompatActivity() {
    lateinit var binding: ActivityTodaysSalesCollectionBinding
    var dateSelected: String = ""
    val viewModel: TodaysSalesCollectionViewModel by viewModels()
    private lateinit var adapter: SalesOrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodaysSalesCollectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Bills Collected"
        binding.toolbar.setTitleTextColor(ContextCompat.getColor(this@TodaysSalesCollection, android.R.color.white))

        // Set current date as default
        dateSelected = SupportMethods.getCurrentDateFormatted()
        binding.tvSelectDate.text = dateSelected

        // Set up RecyclerView & Adapter
        adapter = SalesOrderAdapter(emptyList())
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // Set up date selector
        binding.tvSelectDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    adapter.updateList(emptyList())
                    val selected = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                    dateSelected = selected
                    binding.tvSelectDate.text = dateSelected
                    loadDataBasedOnDate()
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Observe ViewModel data
        viewModel.billItemList.observe(this) { salesOrders ->
            LoadingDialog.hide()
            if (salesOrders.isNullOrEmpty()) {
                binding.recyclerView.visibility = android.view.View.GONE
                binding.tvNoItemInList.visibility = android.view.View.VISIBLE
            } else {
                adapter.updateList(salesOrders)
                binding.recyclerView.visibility = android.view.View.VISIBLE
                binding.tvNoItemInList.visibility = android.view.View.GONE
            }
        }

        loadDataBasedOnDate()
    }

    fun loadDataBasedOnDate() {
        LoadingDialog.show(this, "Getting details")
        viewModel.getDetails(SessionUtils.getApiKey(applicationContext), dateSelected)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? androidx.appcompat.widget.SearchView
        searchView?.queryHint = "Search details..."

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
