package ie.wit.trekit.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import ie.wit.trekit.R
import ie.wit.trekit.databinding.ActivityMountainBinding
import ie.wit.trekit.main.MainApp
import ie.wit.trekit.models.MountainModel
import timber.log.Timber

class MountainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMountainBinding
    var mountain = MountainModel()
    var db = FirebaseDatabase.getInstance("https://trekit-ded67-default-rtdb.firebaseio.com/").getReference("mountains")
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMountainBinding.inflate(layoutInflater)
        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)
        setContentView(binding.root)
        app = application as MainApp

        // launches activity with the mountain details either from extra mountain show or mountain depending on where the activity was launched from
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
                val mapActivityIntent = Intent(this, MapActivity::class.java)
                    mapActivityIntent.putExtra("location", lat)
                    mapActivityIntent.putExtra("location1", long)
                startActivity(mapActivityIntent)
            }
       } else
                if(intent.hasExtra("mountain")){
                    mountain = intent.extras?.getParcelable("mountain")!!
                    binding.mountainName.text = mountain.mountainName
                    binding.areaName.text = mountain.areaName
                    binding.elevation.text = mountain.elevation.toString()
                    binding.mountainLat.text = mountain.mountainLat.toString()
                    binding.mountainLong.text = mountain.mountainLong.toString()


                    binding.mountainLocation.setOnClickListener{
                        val lat = mountain.mountainLat.toString()
                        val long = mountain.mountainLong.toString()
                        val mapActivityIntent = Intent(this, MapActivity::class.java)
                        mapActivityIntent.putExtra("location", lat)
                        mapActivityIntent.putExtra("location1", long)
                        startActivity(mapActivityIntent)
           }

        }
        onResume()
    }

    //menu for mountainActivity created
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_mountain_activity, menu)
        val favouriteItem = menu.findItem(R.id.item_favourite)
        favouriteItem.isVisible = mountain.isFavourite.not()
        return super.onCreateOptionsMenu(menu)
    }

//menu item is selected runs one of the options associated
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_cancel -> {
                finish()
            }
            R.id.item_favourite -> {
                setFavourite(mountain,true)
            }
            R.id.item_climbed-> {
                onAddToClimbedClicked()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //on Resume to ensure add to favourites not visible if mountain already on favourite list
    override fun onResume() {
        super.onResume()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val userFavouritesRef = FirebaseDatabase.getInstance("https://trekit-ded67-default-rtdb.firebaseio.com/")
            .getReference("user_favourites/$userId")
            userFavouritesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.toolbar.menu.findItem(R.id.item_favourite).isVisible =
                    !snapshot.child(mountain.mountainName).exists()
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "Failed to read user favourites")
            }
        })
    }

    //set the mountain as favourite and add to separate list

    private fun setFavourite(mountain: MountainModel, isFavourite: Boolean) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val userFavouritesRef = FirebaseDatabase.getInstance("https://trekit-ded67-default-rtdb.firebaseio.com/").getReference("user_favourites/$userId")
        mountain.isFavourite = isFavourite
        val favouriteIcon = binding.toolbar.menu.findItem(R.id.item_favourite)
        favouriteIcon.isVisible = !isFavourite
        db.child(mountain.mountainName).setValue(mountain)
        if (isFavourite) {
            userFavouritesRef.child(mountain.mountainName).setValue(true)
            Toast.makeText(this, "Added to Favourites", Toast.LENGTH_SHORT).show()
        } else {
            userFavouritesRef.child(mountain.mountainName).removeValue()
        }
    }

    //launches the AddClimbedDetailsActivity with the mountainName as extra
    private fun onAddToClimbedClicked(){
        val name = mountain.mountainName
        val intent = Intent(this, AddClimbedDetailsActivity::class.java)
        intent.putExtra("mountain_name", name)
        startActivity(intent)
    }
}