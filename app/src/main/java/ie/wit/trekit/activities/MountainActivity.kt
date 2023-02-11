package ie.wit.trekit.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.maps.model.LatLng
import ie.wit.trekit.R
import ie.wit.trekit.databinding.ActivityMountainBinding
import ie.wit.trekit.main.MainApp
import ie.wit.trekit.models.MountainModel
import timber.log.Timber

class MountainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMountainBinding
    var mountain = MountainModel()
    //var location = Location(52.245696, -7.139102, 15f)

    lateinit var app: MainApp

    private lateinit var mapIntentLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMountainBinding.inflate(layoutInflater)
        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)
        setContentView(binding.root)
        app = application as MainApp


        if (intent.hasExtra("mountain_show")) {
            mountain = intent.extras?.getParcelable("mountain_show")!!
            binding.mountainName.text = mountain.mountainName
            binding.areaName.text = mountain.areaName
            binding.elevation.text = mountain.elevation.toString()
            binding.mountainLat.text = mountain.mountainLat.toString()
            binding.mountainLong.text = mountain.mountainLong.toString()


            binding.mountainLocation.setOnClickListener{
                val lat = mountain.mountainLat.toString()
                val long = mountain.mountainLong.toString()
                //val loc = LatLng(mountain.mountainLat, mountain.mountainLong)
                val mapActivityIntent = Intent(this, MapActivity::class.java)
                    mapActivityIntent.putExtra("location", lat)
                    mapActivityIntent.putExtra("location1", long)
              //  mapIntentLauncher.launch(launcherIntent)
                startActivity(mapActivityIntent)
            }
            //registerMapCallback()

        }
    }
    private fun registerMapCallback() {
        mapIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { Timber.i("Map Loaded") }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_mountain_activity, menu)
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