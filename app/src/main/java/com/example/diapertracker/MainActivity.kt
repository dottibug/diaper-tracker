package com.example.diapertracker

import android.content.Context
import android.icu.util.Calendar
import android.os.Bundle
import android.text.format.DateFormat
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButtonToggleGroup

class MainActivity : AppCompatActivity() {

    // Declare variables
    private lateinit var radioButtonDry: RadioButton
    private lateinit var radioButtonDirty: RadioButton
    private lateinit var radioButtonWet: RadioButton
    private lateinit var timeEdit : EditText
    private lateinit var toggleButtonAmPm : MaterialButtonToggleGroup
    private lateinit var diaperList : TextView
    private lateinit var diaperCount : TextView
    private var diaperCounter : Int = 0
    private lateinit var imm : InputMethodManager
    private val toastMessageTime = "Enter the correct time format (12:00)"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Button references
        val buttonAddDiaper : Button = findViewById(R.id.buttonAddDiaper)
        val buttonClear : Button = findViewById(R.id.buttonClear)

        // Button click listeners
        buttonAddDiaper.setOnClickListener { addDiaper() }
        buttonClear.setOnClickListener { clear() }

        // Initialize variables
        initialize()

        // Toggle AM button
        defaultTimeToggle()
    }

    // Initialize variables
    private fun initialize() {
        radioButtonDry = findViewById(R.id.radioButtonDry)
        radioButtonDirty = findViewById(R.id.radioButtonDirty)
        radioButtonWet = findViewById(R.id.radioButtonWet)
        timeEdit = findViewById(R.id.editTextTime)
        toggleButtonAmPm = findViewById(R.id.toggleButtonAmPm)
        diaperList = findViewById(R.id.textViewDiaperList)
        diaperCount = findViewById(R.id.textViewDiaperCount)
        diaperList.visibility = TextView.INVISIBLE
        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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
            radioButtonDry.isChecked -> "Dry"
            radioButtonDirty.isChecked -> "Dirty"
            radioButtonWet.isChecked -> "Wet"
            else -> ""
        }
    }

    // Get the time (defaults to the current time if user left the field empty)
    private fun getTime() : String {
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
        return if (toggleButtonAmPm.checkedButtonId == R.id.buttonAM) "am" else "pm"
    }

    // Check if the time is in the correct format (e.g. "2:00" or "02:00" on a 12-hour clock)
    private fun isValidTime() : Boolean {
        val timeRegex = Regex("^(0?[1-9]|1[0-2]):[0-5][0-9]$")
        return timeRegex.matches(timeEdit.text.toString())
    }

    // Update the diaper list with the new diaper entry
    private fun updateDiaperList(newDiaper : String) {
        when (diaperCounter) {
            1 -> diaperList.text = newDiaper
            else -> diaperList.text = getString(R.string.diaperList, diaperList.text.toString(), newDiaper)
        }
        diaperList.visibility = TextView.VISIBLE
    }

    // Update the diaper count summary
    private fun updateDiaperCountSummary() {
        val nounForm = if (diaperCounter == 1) "" else "s"
        diaperCount.text = getString(R.string.diaperCount, diaperCounter, nounForm)
        diaperCount.visibility = TextView.VISIBLE
    }

    // Reset all values to default state
    private fun clear() {
        resetDiaperInput()
        clearDiaperList()
        resetDiaperCounter()
        hideKeyboard()
    }

    // Clear the time field
    private fun clearTime() {
        timeEdit.text.clear()
        timeEdit.clearFocus()
    }

    // Reset diaper input to default state
    private fun resetDiaperInput() {
        radioButtonDry.isChecked = true
        radioButtonDirty.isChecked = false
        radioButtonWet.isChecked = false
        clearTime()
        defaultTimeToggle()
    }

    // Clear diaper list
    private fun clearDiaperList() {
        diaperList.text = ""
        diaperList.visibility = TextView.INVISIBLE
    }

    // Reset the diaper counter
    private fun resetDiaperCounter() {
        diaperCounter = 0
        diaperCount.visibility = TextView.INVISIBLE
    }

    // Hide keyboard
    private fun hideKeyboard() {
        imm.hideSoftInputFromWindow(timeEdit.windowToken, 0)
    }

    // Set the am/pm toggle button to AM
    private fun defaultTimeToggle() {
        toggleButtonAmPm.check(R.id.buttonAM)
    }
}