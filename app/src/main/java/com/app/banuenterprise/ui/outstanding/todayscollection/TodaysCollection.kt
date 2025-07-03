package com.app.banuenterprise.ui.outstanding.todayscollection

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.banuenterprise.R
import com.app.banuenterprise.databinding.ActivityTodaysCollectionBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TodaysCollection : AppCompatActivity() {
    lateinit var binding : ActivityTodaysCollectionBinding
    val viewModel : TodaysCollectionViewModel by viewModels()
    var dateSelected: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodaysCollectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Set up the Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Bills Collected"
        binding.toolbar.setTitleTextColor(ContextCompat.getColor(this@TodaysCollection, android.R.color.white))
        // Set current date as default
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        dateSelected = dateFormat.format(calendar.time)
        binding.tvSelectDate.text = dateSelected
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.tvSelectDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val selected = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
                    dateSelected = selected
                    binding.tvSelectDate.text = dateSelected
                    loadDataBasedOnDate()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        loadDataBasedOnDate()
    }
    fun loadDataBasedOnDate(){

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
//                adapter?.filter(newText ?: "")
//                if (adapter?.getFilteredListSize() == 0) {
//                    binding.tvNoItemInList.visibility = View.VISIBLE
//                    binding.recyclerView.visibility = View.GONE
//                } else {
//                    binding.tvNoItemInList.visibility = View.GONE
//                    binding.recyclerView.visibility = View.VISIBLE
//                }
                return true
            }
        })


        return true
    }
}