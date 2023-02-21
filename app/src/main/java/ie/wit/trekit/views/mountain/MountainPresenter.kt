package ie.wit.trekit.views.mountain

import android.content.Intent
import android.location.Location
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import ie.wit.trekit.activities.MapActivity
import ie.wit.trekit.databinding.ActivityMountainBinding
import ie.wit.trekit.main.MainApp
import ie.wit.trekit.models.MountainModel
import timber.log.Timber

class MountainPresenter(private val view: MountainView) {

    var mountain = MountainModel()
    lateinit var app: MainApp
    var binding: ActivityMountainBinding = ActivityMountainBinding.inflate(view.layoutInflater)
    lateinit var mapIntentLauncher : ActivityResultLauncher<Intent>


    init {
        if (view.intent.hasExtra("mountain_show")) {
            mountain = view.intent.extras?.getParcelable("mountain_show")!!
            view.showMountain(mountain)
        }
    }
        fun doCancel() {
            view.finish()

        }
        fun doSetLocation() {
            val lat = mountain.mountainLat.toString()
            val long = mountain.mountainLong.toString()
            val mapActivityIntent = Intent(view, MapActivity::class.java)
            mapActivityIntent.putExtra("location", lat)
            mapActivityIntent.putExtra("location1", long)
            mapIntentLauncher.launch(mapActivityIntent)
        }
        fun cachePlacemark (mountainName: String, areaName: String, elevation: Double, mountainLat: Double, mountainLong: Double) {
            mountain.mountainName = mountainName
            mountain.areaName = areaName
            mountain.elevation = elevation
            mountain.mountainLat = mountainLat
            mountain.mountainLong = mountainLong
        }
}