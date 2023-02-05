package ie.wit.trekit.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.ActionBarDrawerToggle
import ie.wit.trekit.R
import ie.wit.trekit.databinding.ActivityMainBinding
import ie.wit.trekit.main.MainApp
import ie.wit.trekit.models.MountainModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import timber.log.Timber
import timber.log.Timber.DebugTree
import timber.log.Timber.i


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var mountain = MountainModel()
    var app : MainApp? = null
    private lateinit var refreshIntentLauncher : ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(toolbar)
        app = application as MainApp

        binding.buttonPeaks.setOnClickListener(View.OnClickListener { startActivity(Intent(this, MountainListActivity::class.java)) })


        val toggle = ActionBarDrawerToggle(this,drawerLayout, toolbar,
            R.string.open,
            R.string.close
        )
        toggle.isDrawerIndicatorEnabled = true
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
            }
}