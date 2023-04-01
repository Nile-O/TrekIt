package ie.wit.trekit.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
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
import ie.wit.trekit.adapters.ClimbedMountainListener
import ie.wit.trekit.adapters.ClimbedMountainsAdapter
import ie.wit.trekit.databinding.ActivityClimbedListBinding
import ie.wit.trekit.main.MainApp
import ie.wit.trekit.models.ClimbedMountain
import kotlinx.android.synthetic.main.activity_climbed_list.recyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber.i

class ClimbedListActivity : AppCompatActivity(), ClimbedMountainListener {
    lateinit var app: MainApp
    private lateinit var binding: ActivityClimbedListBinding
    private lateinit var refreshIntentLauncher: ActivityResultLauncher<Intent>
    private var climbedMountains = mutableListOf<ClimbedMountain>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClimbedListBinding.inflate(layoutInflater)
        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)
        setContentView(binding.root)

        app = application as MainApp

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager

        val adapter = ClimbedMountainsAdapter(climbedMountains, this)
        adapter.attachSwipeToDelete(recyclerView)

        fetchClimbedMountains()

        registerRefreshCallback()

        //implementing the search filter feature
        val searchView = findViewById<SearchView>(R.id.searchView)
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

        //FAB to launch statsActivity with the stats as extras
        binding.floatingActionButton.setOnClickListener {
            val totalClimbs = totalClimbs()
            val totalTime = getTotalTime()
            val averageTime = getAverageTime()
            val mostClimbed =  getMostClimbedMountain(climbedMountains)
            val fastestClimb = fastestClimb(climbedMountains)
            val intent = Intent(this, StatsActivity::class.java)
                intent.putExtra("totalTime", totalTime)
                intent.putExtra("averageTime", averageTime)
                intent.putExtra("totalClimbs", totalClimbs)
                intent.putExtra("fastestClimb", fastestClimb)
                intent.putExtra("mostClimbed", mostClimbed)

            startActivity(intent)
        }

    }

    //when the mountain is clicked the MountainActivity is launched with the mountain details added as extra
    @Suppress("NAME_SHADOWING")
    override suspend fun onClimbedMountainClick(mountain: ClimbedMountain) {
        val mountain = app.mountains.findOneMountainByName(mountain.mountainName)
        val mountainIntent = Intent(this, MountainActivity::class.java).apply {
            putExtra("mountain", mountain)
        }
        startActivity(mountainIntent)
    }

    //refresh the recyclerview when activity is launched
    @SuppressLint("NotifyDataSetChanged")
    private fun registerRefreshCallback() {
        refreshIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { binding.recyclerView.adapter!!.notifyDataSetChanged() }
    }

    //inflates the menu for climbed list
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_climbed_list, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //registers the selection and launches the main activity
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_home -> {
                val launcherIntent = Intent(this, MainActivity::class.java)
                refreshIntentLauncher.launch(launcherIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

//updates the recycler view to show the new list of climbedMountains
    private fun updateRecyclerView(climbedMountains: MutableList<ClimbedMountain>) {
        GlobalScope.launch(Dispatchers.Main) {
            binding.recyclerView.adapter =
                ClimbedMountainsAdapter(climbedMountains, this@ClimbedListActivity)
        }
    }
//retrieving the list of climbed mountains from firebase and updating the recyclerView
    private fun fetchClimbedMountains() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val userClimbedRef =
            FirebaseDatabase.getInstance("https://trekit-ded67-default-rtdb.firebaseio.com/")
                .getReference("user_climbed_mountains/$userId")

        userClimbedRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach { climbedSnapshot ->
                    val climbedMountain = climbedSnapshot.getValue(ClimbedMountain::class.java)
                    climbedMountain?.let {
                        climbedMountains.add(it)

                        }
                }
                updateRecyclerView(climbedMountains)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                i(databaseError.toException(), "fetchClimbedMountains:onCancelled")
            }
        })

    }
    //filtering the list based on when a string is input in the searchview
    private fun filterMountainList(query: String?) {
        GlobalScope.launch(Dispatchers.Main) {
            val filteredList = climbedMountains.filter { it.mountainName.contains(query ?: "", true) }
                .filter { it.mountainName.contains(query ?: "", true) }
            (binding.recyclerView.adapter as ClimbedMountainsAdapter).updateList(filteredList)
    }
}

//calculate total number of climbs
    private fun totalClimbs(): Int {
        return climbedMountains.size
    }
//calculate the fastest climb by looking at duration of each climb and saving the lowest value
    private fun fastestClimb(climbedMountains: List<ClimbedMountain>): String {
        if (climbedMountains.isEmpty()) {
            return "No mountains climbed yet"
        }
        var fastestClimb: ClimbedMountain? = null
        for (climbedMountain in climbedMountains) {
            if (fastestClimb == null || climbedMountain.duration < fastestClimb.duration) {
                fastestClimb = climbedMountain
            }
        }
        return "${fastestClimb?.mountainName} (${fastestClimb?.duration} minutes)"
    }
//total time calculated by adding all the durations
    private fun getTotalTime(): Int {
        var totalTime = 0
        for (climbedMountain in climbedMountains) {
            totalTime += climbedMountain.duration
        }
        return totalTime
    }
//average time calculated by dividing the total time by number of climbedMountains
    private fun getAverageTime(): Double {
        val totalTime = getTotalTime()
        return if (totalTime > 0) {
            totalTime.toDouble() / climbedMountains.size
        } else {
            0.0
        }
    }
    //most climbed calculated by mountainCounts map created then each climbed mountain added and count value increased by 1 -highest counted mountain's name is returned
    private fun getMostClimbedMountain(climbedMountains: List<ClimbedMountain>): String {
        if (climbedMountains.isEmpty()) {
            return "No mountains climbed yet"
        }

        val mountainCounts = mutableMapOf<String, Int>()
        for (climbedMountain in climbedMountains) {
            val count = mountainCounts.getOrDefault(climbedMountain.mountainName, 0)
            mountainCounts[climbedMountain.mountainName] = count + 1
        }

        val mostClimbedMountainName = mountainCounts.maxByOrNull { it.value }?.key
        return mostClimbedMountainName?.let {
            "$it (${mountainCounts[it]} climbs)"
        } ?: "No mountains climbed yet"
    }
}