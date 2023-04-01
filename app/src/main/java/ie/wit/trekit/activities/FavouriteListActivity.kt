package ie.wit.trekit.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
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
import kotlinx.android.synthetic.main.activity_favourite_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber.i
import android.widget.SearchView

class FavouriteListActivity : AppCompatActivity(), FavMountainListener {
    lateinit var app: MainApp
    private lateinit var binding: ActivityFavouriteListBinding
    private lateinit var refreshIntentLauncher: ActivityResultLauncher<Intent>
    private var favouriteMountains = mutableListOf<MountainModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavouriteListBinding.inflate(layoutInflater)
        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)
        setContentView(binding.root)

        app = application as MainApp

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager

        val adapter = FavouriteMountainsAdapter(favouriteMountains, this)
        adapter.attachSwipeToDelete(recyclerView)

        fetchFavoriteMountains()

        registerRefreshCallback()

        val searchView = findViewById<SearchView>(R.id.searchView2)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Filter the list of mountains based on the search query
                filterMountainList(newText)
                return true
            }
        })



    }

    //when the mountain is clicked the MountainActivity is launched with the mountain details added as extra
    override fun onMountainClick(mountain: MountainModel) {
        val launcherIntent = Intent(this, MountainActivity::class.java)
        launcherIntent.putExtra("mountain_show", mountain)
        refreshIntentLauncher.launch(launcherIntent)
    }

    //refresh the recyclerview when activity is launched
    private fun registerRefreshCallback() {
        refreshIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { binding.recyclerView.adapter!!.notifyDataSetChanged() }
    }

    //inflates the menu for favourite list
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_favourite_list, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //registers the selection and cancels this activity
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_cancel -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //updates the recycler view to show the new list of favourites
    private fun updateRecyclerView(favouriteMountains: MutableList<MountainModel>) {
            GlobalScope.launch(Dispatchers.Main) {
                binding.recyclerView.adapter =
                    FavouriteMountainsAdapter(favouriteMountains, this@FavouriteListActivity)
            }
        }

    //retrieving the list of favourite mountains from firebase and updating the recyclerView
    private fun fetchFavoriteMountains() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val userFavouritesRef = FirebaseDatabase.getInstance("https://trekit-ded67-default-rtdb.firebaseio.com/").getReference("user_favourites/$userId")

        userFavouritesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach { favouriteSnapshot ->
                    val mountainName = favouriteSnapshot.key
                    val mountainRef = FirebaseDatabase.getInstance("https://trekit-ded67-default-rtdb.firebaseio.com/").getReference("mountains/$mountainName")

                    mountainRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val mountain = dataSnapshot.getValue(MountainModel::class.java)
                            mountain?.let {
                                favouriteMountains.add(it)
                                binding.recyclerView.adapter?.notifyDataSetChanged()

                                updateRecyclerView(favouriteMountains)
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            i(databaseError.toException(), "fetchFavoriteMountains:onCancelled")
                        }
                    })
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                i(databaseError.toException(), "fetchFavoriteMountains:onCancelled")
            }
        })
    }

    //filtering the list based on when a string is input in the searchview
    private fun filterMountainList(query: String?) {
        GlobalScope.launch(Dispatchers.Main) {
            val filteredList = favouriteMountains.filter { it.mountainName.contains(query ?: "", true) }
                .filter { it.mountainName.contains(query ?: "", true) }
            (binding.recyclerView.adapter as FavouriteMountainsAdapter).updateList(filteredList)
        }
    }
}
