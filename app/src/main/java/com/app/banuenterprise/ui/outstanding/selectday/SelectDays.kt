package com.app.banuenterprise.ui.outstanding.selectday

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.TranslateAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.app.banuenterprise.databinding.ActivitySelectDaysBinding
import com.app.banuenterprise.ui.outstanding.daywisecustomer.DayWiseCustomerViewActivity

class SelectDays : AppCompatActivity() {
    lateinit var binding : ActivitySelectDaysBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectDaysBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Set up the Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "My Report"
        binding.toolbar.setTitleTextColor(ContextCompat.getColor(this@SelectDays, android.R.color.white))
        loadUiFromLeft()
    }

    private fun loadUiFromLeft(){
        val handler = Handler(Looper.getMainLooper())
        for (i in 0 until binding.daysLayout.childCount) {
            val child = binding.daysLayout.getChildAt(i)
            handler.postDelayed({
                val anim = TranslateAnimation(
                    -child.width.toFloat(), 0f,
                    0f, 0f
                ).apply {
                    duration = 300
                    fillAfter = true
                }
                child.startAnimation(anim)
            }, i * 150L)
        }
    }

    fun onDayCardClicked(view: View) {
        // Get the day from the tag set in the XML
        val dayTag = (view.parent as View).tag?.toString() ?: "Unknown"

        // Map day name to integer value
        val dayInt = when (dayTag) {
            "Sunday" -> 0
            "Monday" -> 1
            "Tuesday" -> 2
            "Wednesday" -> 3
            "Thursday" -> 4
            "Friday" -> 5
            "Saturday" -> 6
            else -> -1 // Default case if unknown
        }


        // Create intent to pass data to the next activity
        val intent = Intent(this, DayWiseCustomerViewActivity::class.java)
        intent.putExtra("day", dayInt)  // Pass the integer value instead of the string day
        startActivity(intent)
    }


}