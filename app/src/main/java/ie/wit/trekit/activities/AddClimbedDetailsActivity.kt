package ie.wit.trekit.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import ie.wit.trekit.R
import ie.wit.trekit.models.ClimbedMountain
import kotlinx.android.synthetic.main.activity_add_climbed_details.*
import timber.log.Timber.i
import java.util.*


@Suppress("NAME_SHADOWING")
class AddClimbedDetailsActivity : AppCompatActivity() {
    private var datePickerDialog: DatePickerDialog? = null
    private var dateButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_climbed_details)

        // Set up the toolbar
        setSupportActionBar(toolbar as Toolbar)

        //add mountainName from the intent
        val mountainNameTextView: TextView = findViewById(R.id.mountainName)
        val mountainName = intent.getStringExtra("mountain_name")
        mountainNameTextView.text = mountainName

        //initialising the date picker and displaying today's date on the date button
        initDatePicker()
        dateButton = findViewById(R.id.date_of_climb_input)
        i("dateButton = $dateButton")
        dateButton?.text = getTodayDate()
        numberPickerSetup()

        //assigning save button to create climbedMountain variable
        val saveButton: Button = findViewById(R.id.button_save_details)
        saveButton.setOnClickListener {
            val mountainNameTextView: TextView = findViewById(R.id.mountainName)
            val mountainName = mountainNameTextView.text.toString()

            //converting the values from numberpickers into total minutes for the duration of the trek
            val hoursTakenPicker: NumberPicker = findViewById(R.id.hours_taken)
            val minutesTakenPicker: NumberPicker = findViewById(R.id.minutes_taken)

            val hours = hoursTakenPicker.value
            val minutes = minutesTakenPicker.value

            val duration = hours * 60 + minutes

            val dateClimbed = dateButton?.text.toString()
            //combining the mountainName, date and duration to  a climbedMountain object
            val climbedMountain = ClimbedMountain(mountainName, dateClimbed, duration)
            i("ClimbedMountain: $mountainName on $dateClimbed and it took $duration")
            saveClimbedMountain(climbedMountain)
        }
    }

    //function to save the climbed mountain to firebase
    private fun saveClimbedMountain(climbedMountain: ClimbedMountain) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val userClimbedMountainsRef = FirebaseDatabase.getInstance("https://trekit-ded67-default-rtdb.firebaseio.com/").getReference("user_climbed_mountains/$userId")
        val key = userClimbedMountainsRef.push().key
        climbedMountain.key = key
        if (key != null) {
            userClimbedMountainsRef.child(key).setValue(climbedMountain)
                .addOnSuccessListener {
                    Toast.makeText(this, "Climbed mountain details saved successfully", Toast.LENGTH_SHORT).show()
                    openClimbedList()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save climbed mountain details", Toast.LENGTH_SHORT).show()
                }
            i("KEY $key")
        }

    }

    //launching climbedListActivity upon saving details
    private fun openClimbedList() {
        startActivity(
        Intent(
            this,
            ClimbedListActivity::class.java
        )
    )
    }

    //getting today's date
    @SuppressLint("TimberArgCount")
    private fun getTodayDate(): String {
        i("getTodayDate() called")
        val cal: Calendar = Calendar.getInstance()
        val year: Int = cal.get(Calendar.YEAR)
        var month: Int = cal.get(Calendar.MONTH)
        month += 1
        val day: Int = cal.get(Calendar.DAY_OF_MONTH)
        return makeDateString(day, month, year)
    }

    //initialising the date picker
    private fun initDatePicker() {
        val dateSetListener =
            OnDateSetListener { _, year, month, day ->
                var month = month
                month += 1
                val date: String = makeDateString(day, month, year)
                dateButton!!.text = date
            }
        val cal = Calendar.getInstance()
        val year = cal[Calendar.YEAR]
        val month = cal[Calendar.MONTH]
        val day = cal[Calendar.DAY_OF_MONTH]
        val style: Int = AlertDialog.BUTTON_POSITIVE
        datePickerDialog = DatePickerDialog(this, style, dateSetListener, year, month, day)
        datePickerDialog!!.datePicker.maxDate = System.currentTimeMillis()
    }

    //creating string for displaying date
    private fun makeDateString(day: Int, month: Int, year: Int): String {
        return getMonthFormat(month) + " " + day + " " + year
    }

    //how month will be displayed
    private fun getMonthFormat(month: Int): String {
        return when (month) {
            1 -> "JAN"
            2 -> "FEB"
            3 -> "MAR"
            4 -> "APR"
            5 -> "MAY"
            6 -> "JUN"
            7 -> "JUL"
            8 -> "AUG"
            9 -> "SEP"
            10 -> "OCT"
            11 -> "NOV"
            12 -> "DEC"
            else -> "JAN" //default should never happen
        }
    }

    fun openDatePicker(view: View?) {
        datePickerDialog!!.show()
    }

    //setting up number pickers for hours and mins - hours max 24 mins max 59
    private fun numberPickerSetup(){
        val hoursTakenPicker: NumberPicker = findViewById(R.id.hours_taken)
        hoursTakenPicker.minValue = 0
        hoursTakenPicker.maxValue = 24

        val minutesTakenPicker: NumberPicker = findViewById(R.id.minutes_taken)
        minutesTakenPicker.minValue = 0
        minutesTakenPicker.maxValue = 59
    }
}