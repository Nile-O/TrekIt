package ie.wit.trekit.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ie.wit.trekit.R
import ie.wit.trekit.adapters.FavMountainListener
import ie.wit.trekit.adapters.FavouriteMountainsAdapter
import ie.wit.trekit.databinding.ActivityFavouriteListBinding
import ie.wit.trekit.main.MainApp
import ie.wit.trekit.models.MountainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FavouriteListActivity : AppCompatActivity(), FavMountainListener {
    lateinit var app: MainApp
    private lateinit var binding: ActivityFavouriteListBinding
    private lateinit var refreshIntentLauncher: ActivityResultLauncher<Intent>
    private lateinit var mapIntentLauncher: ActivityResultLauncher<Intent>
    private lateinit var favouriteMountains: List<MountainModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavouriteListBinding.inflate(layoutInflater)
        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)
        setContentView(binding.root)

        app = application as MainApp

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager

        fetchFavoriteMountains()

        registerRefreshCallback()
        registerMapCallback()

    }

    override fun onMountainClick(mountain: MountainModel) {
        val launcherIntent = Intent(this, MountainActivity::class.java)
        launcherIntent.putExtra("mountain_show", mountain)
        refreshIntentLauncher.launch(launcherIntent)
    }

    private fun registerRefreshCallback() {
        refreshIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { binding.recyclerView.adapter!!.notifyDataSetChanged() }
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

    private fun registerMapCallback() {
        mapIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            {}
    }

    private fun updateRecyclerView(favouriteMountains: List<MountainModel>) {
        GlobalScope.launch(Dispatchers.Main) {
            binding.recyclerView.adapter =
                FavouriteMountainsAdapter(favouriteMountains, this@FavouriteListActivity)
        }
    }

    private fun fetchFavoriteMountains() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val db =
            FirebaseDatabase.getInstance("https://trekit-ded67-default-rtdb.firebaseio.com/").reference
        //val userFavourites = ArrayList<MountainModel>()
        val userFavouritesRef =
            FirebaseDatabase.getInstance("https://trekit-ded67-default-rtdb.firebaseio.com/")
                .getReference("user_favourites/$userId")
        val valueEventListener = object : ValueEventListener {
            override fun onCancelled(dataSnapshot: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val mountains = ArrayList<MountainModel>()
                for (mountainSnapshot in snapshot.children) {
                    val mountainId = mountainSnapshot.key
                    val isFavourite = mountainSnapshot.getValue(Boolean::class.java)
                    if (isFavourite == true) {
                        db.child("mountains/$mountainId")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(databaseError: DatabaseError) {
                                    Snackbar.make(binding.root, "Database error", Snackbar.LENGTH_LONG)
                                        .show()
                                }

                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    val mountain = dataSnapshot.getValue(MountainModel::class.java)
                                    mountain?.let {
                                        mountains.add(it)
                                    }
                                    favouriteMountains = mountains  // Store the list in a field
                                    updateRecyclerView(mountains)  // Call updateRecyclerView() once the list is retrieved
                                }
                            })
                    }
                }

            }
        }
        userFavouritesRef.addListenerForSingleValueEvent(valueEventListener)
    }
}


