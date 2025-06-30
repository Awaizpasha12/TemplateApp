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
        val dayTag = (view.parent as View).tag?.toString() ?: "Unknown"
        val intent = Intent(this, DayWiseCustomerViewActivity::class.java)
        intent.putExtra("day",dayTag)
        startActivity(intent)
    }

}