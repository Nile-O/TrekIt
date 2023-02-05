package ie.wit.trekit.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ie.wit.trekit.R
import ie.wit.trekit.databinding.ActivityMountainBinding
import ie.wit.trekit.main.MainApp
import kotlinx.android.synthetic.main.content_main.*
import ie.wit.trekit.models.MountainModel
import kotlinx.android.synthetic.main.activity_mountain.*

class MountainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMountainBinding
    var mountain = MountainModel()
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMountainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        app = application as MainApp

        if (intent.hasExtra("mountain_show")) {
            mountain = intent.extras?.getParcelable("mountain_show")!!
            binding.mountainName.text = mountain.mountainName
            binding.areaName.text = mountain.areaName
            binding.elevation.text = mountain.elevation.toString()

        }
    }
}