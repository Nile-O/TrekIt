package ie.wit.trekit.activities

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import ie.wit.trekit.R
import ie.wit.trekit.databinding.ActivityMountainMapsBinding
import ie.wit.trekit.databinding.ContentMountainMapsBinding
import ie.wit.trekit.main.MainApp
import kotlinx.android.synthetic.main.content_mountain_maps.*

class MountainMapsActivity : AppCompatActivity(), GoogleMap.OnMarkerClickListener {

    lateinit var app: MainApp
    private lateinit var binding: ActivityMountainMapsBinding
    private lateinit var contentBinding: ContentMountainMapsBinding
    lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMountainMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        app = application as MainApp

        contentBinding = ContentMountainMapsBinding.bind(binding.root)
        contentBinding.mapView.onCreate(savedInstanceState)
        contentBinding.mapView.getMapAsync{
            map = it
            configureMap()
        }
    }


   private fun configureMap() {
       map.setOnMarkerClickListener(this)
       map.uiSettings.isZoomControlsEnabled = true
       app.mountains.findAll().forEach {
           val loc = LatLng(it.mountainLat, it.mountainLong)
           val options = MarkerOptions().title(it.mountainName).position(loc)
           map.addMarker(options)?.tag = it.id
           map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 6f))
       }
   }

    override fun onDestroy() {
        super.onDestroy()
        contentBinding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        contentBinding.mapView.onLowMemory()
    }

    override fun onPause() {
        super.onPause()
        contentBinding.mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        contentBinding.mapView.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        contentBinding.mapView.onSaveInstanceState(outState)
    }
    override fun onMarkerClick(marker: Marker): Boolean {
        val tag = marker.tag as Long
        val mountain = app.mountains.findById(tag)
        contentBinding.currentName.text = marker.title
        contentBinding.currentLat.text = mountain!!.mountainLat.toString()
        contentBinding.currentLong.text = mountain!!.mountainLong.toString()

        return true

    }
}