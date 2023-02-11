package ie.wit.trekit.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import ie.wit.trekit.R
import ie.wit.trekit.databinding.ActivityMapBinding
import ie.wit.trekit.models.MountainModel
import timber.log.Timber.i

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapBinding

    private lateinit var map: GoogleMap
    var mountain = MountainModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_map)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


       // binding.mountain.mountainLat.text = mountain.mountainLat.toString()
      //  binding.mountain.mountainLong.text = mountain.mountainLong.toString()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val loc = LatLng(mountain.mountainLat.toDouble(), mountain.mountainLong.toDouble())
        //val loc = LatLng(-34.0, 151.0)

        val options = MarkerOptions()
            .title("Peak")
            .snippet("GPS : $loc")
            .position(loc)
        map.addMarker(options)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 16f))
        i("map ready")
    }
}