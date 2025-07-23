package com.app.banuenterprise.ui.outstanding.ledger
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.banuenterprise.R
import com.app.banuenterprise.databinding.ActivityLedgerReportBinding
import com.app.banuenterprise.ui.outstanding.ledger.adapter.LedgerListAdapter
import com.app.banuenterprise.ui.outstanding.todayscollection.CollectionListAdapter
import com.app.banuenterprise.utils.SessionUtils
import com.app.banuenterprise.utils.extentions.LoadingDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LedgerReport : AppCompatActivity() {
    lateinit var binding : ActivityLedgerReportBinding
    val viewModel : LedgerReportViewModel by viewModels()
    lateinit var adapter : LedgerListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLedgerReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Set up the Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Ledger Details"
        binding.toolbar.setTitleTextColor(ContextCompat.getColor(this@LedgerReport, android.R.color.white))
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = LedgerListAdapter(emptyList())
        binding.recyclerView.adapter = adapter

        viewModel.getDetails(SessionUtils.getApiKey(applicationContext))
        viewModel.ledgerReportList.observe(this) { list ->
            LoadingDialog.hide()
            adapter.updateList(list ?: emptyList())
            if (list.isNullOrEmpty()) {
                binding.recyclerView.visibility = View.GONE
                binding.tvNoItemInList.visibility = View.VISIBLE
            } else {
                binding.recyclerView.visibility = View.VISIBLE
                binding.tvNoItemInList.visibility = View.GONE
            }
        }

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
                adapter.filter(newText ?: "")
                if (adapter.getFilteredListSize() == 0) {
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