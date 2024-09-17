package com.example.diapertracker

import android.content.Context
import android.icu.util.Calendar
import android.os.Bundle
import android.text.format.DateFormat
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.diapertracker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var diaperCounter : Int = 0
    private lateinit var imm : InputMethodManager
    private val toastMessageTime = "Enter the correct time format (12:00)"

    var diaperCount : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Data binding inflation
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Data binding (layout variable myActivity is bound to this activity)
        binding.diaperMain = this

        // Button click listeners
        binding.buttonAddDiaper.setOnClickListener { addDiaper() }
        binding.buttonClear.setOnClickListener { clear() }

        initialize()
    }

    // Initialize variables
    private fun initialize() {
        binding.textViewDiaperList.visibility = TextView.INVISIBLE
        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        defaultTimeToggle()
    }

    // Add diaper to list
    private fun addDiaper() {
        val time = getTime()

        if (time == "") return
        else {
            diaperCounter++
            val newDiaper = getNewDiaper(time)
            updateDiaperList(newDiaper)
            updateDiaperCountSummary()
            clearTime()
            hideKeyboard()
        }
    }

    // Get the new diaper entry
    private fun getNewDiaper(time : String) : String {
        val diaperType = getDiaperType()
        val timeUnit = getTimeUnit()
        return getString(R.string.diaperEntry, diaperType, time, timeUnit)
    }

    // Get the diaper type
    private fun getDiaperType() : String {
        return when {
            binding.radioButtonDry.isChecked -> "Dry"
            binding.radioButtonDirty.isChecked -> "Dirty"
            binding.radioButtonWet.isChecked -> "Wet"
            else -> ""
        }
    }

    // Get the time (defaults to the current time if user left the field empty)
    private fun getTime() : String {
        val timeEdit = binding.editTextTime
       return if (timeEdit.text.toString().isEmpty()) getCurrentTime()
       else {
            return if (isValidTime()) timeEdit.text.toString()
            else {
               Toast.makeText(this, toastMessageTime, Toast.LENGTH_SHORT).show()
               timeEdit.text.clear()
               return ""
            }
        }
    }

    // Get the current time (formatted as hour and minute in 12-hour format)
    private fun getCurrentTime() : String {
        val dateNow = Calendar.getInstance().time
        return DateFormat.format("h:mm", dateNow).toString()
    }

    // Get the time unit (am or pm)
    private fun getTimeUnit() : String {
        return if (binding.toggleButtonAmPm.checkedButtonId == R.id.buttonAM) "am" else "pm"
    }

    // Check if the time is in the correct format (e.g. "2:00" or "02:00" on a 12-hour clock)
    private fun isValidTime() : Boolean {
        val timeRegex = Regex("^(0?[1-9]|1[0-2]):[0-5][0-9]$")
        return timeRegex.matches(binding.editTextTime.text.toString())
    }

    // Update the diaper list with the new diaper entry
    private fun updateDiaperList(newDiaper : String) {
        val diaperList = binding.textViewDiaperList
        when (diaperCounter) {
            1 -> diaperList.text = newDiaper
            else -> diaperList.text = getString(R.string.diaperList, diaperList.text.toString(), newDiaper)
        }
        diaperList.visibility = TextView.VISIBLE
    }

    // Update the diaper count summary
    private fun updateDiaperCountSummary() {
        val nounForm = if (diaperCounter == 1) "" else "s"
        diaperCount = "$diaperCounter diaper$nounForm changed"
        // Invalidate all binding expressions to refresh the UI
        binding.invalidateAll()
    }

    // Reset all values to default state
    private fun clear() {
        resetDiaperInput()
        clearDiaperList()
        resetDiaperCountSummary()
        resetDiaperCounter()
        hideKeyboard()
    }

    // Clear the time field
    private fun clearTime() {
        val timeEdit = binding.editTextTime
        timeEdit.text.clear()
        timeEdit.clearFocus()
    }

    // Reset diaper input to default state
    private fun resetDiaperInput() {
        binding.radioButtonDry.isChecked = true
        binding.radioButtonDirty.isChecked = false
        binding.radioButtonWet.isChecked = false
        clearTime()
        defaultTimeToggle()
    }

    // Clear diaper list
    private fun clearDiaperList() {
        val diaperList = binding.textViewDiaperList
        diaperList.text = ""
        diaperList.visibility = TextView.INVISIBLE
    }

    // Reset the diaper counter
    private fun resetDiaperCounter() {
        diaperCounter = 0
    }

    // Reset diaper count summary
    private fun resetDiaperCountSummary() {
        diaperCount = ""
        binding.invalidateAll()
    }

    // Hide keyboard
    private fun hideKeyboard() {
        imm.hideSoftInputFromWindow(binding.editTextTime.windowToken, 0)
    }

    // Set the am/pm toggle button to AM
    private fun defaultTimeToggle() {
        binding.toggleButtonAmPm.check(R.id.buttonAM)
    }
}