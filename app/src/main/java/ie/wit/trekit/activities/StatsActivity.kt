package ie.wit.trekit.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ie.wit.trekit.R
import ie.wit.trekit.databinding.ActivityStatsBinding
import ie.wit.trekit.main.MainApp

class StatsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStatsBinding
    lateinit var app: MainApp


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatsBinding.inflate(layoutInflater)
        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)
        setContentView(binding.root)
        app = application as MainApp

        val totalClimbs = intent.getIntExtra("totalClimbs", 0)
        val fastestClimb = intent.getStringExtra("fastestClimb") ?: "Unknown"
        val totalTime = intent.getIntExtra("totalTime", 0)
        val averageTime = intent.getDoubleExtra("averageTime", 0.0)
        val mostClimbed =  intent.getStringExtra("mostClimbed") ?: "Unknown"

        val totalClimbedTextView = findViewById<TextView>(R.id.totalClimbs)
        val fastestClimbTextView = findViewById<TextView>(R.id.fastestClimb)
        val averageTimeTextView = findViewById<TextView>(R.id.averageTime)
        val mostClimbedTextView = findViewById<TextView>(R.id.mostClimbed)
        val totalTimeTextView = findViewById<TextView>(R.id.totalTime)

        totalClimbedTextView.text = "Total Climbs: $totalClimbs"
        fastestClimbTextView.text = "Fastest Climb: $fastestClimb"
        totalTimeTextView.text = "Total climbing time: $totalTime minutes"
        averageTimeTextView.text = "Average time per climb: $averageTime minutes"
        mostClimbedTextView.text = "The most climbed peak is $mostClimbed"

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_favourite_list, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_cancel -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}