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
import com.google.android.material.snackbar.Snackbar
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
    private lateinit var mapIntentLauncher: ActivityResultLauncher<Intent>
    private var climbedMountains = mutableListOf<ClimbedMountain>()
    private var fastestClimb: ClimbedMountain? = null
    private var totalClimbs: Int = 0

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
        registerMapCallback()

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

        binding.floatingActionButton.setOnClickListener {
            var totalClimbs = totalClimbs()
            var totalTime = getTotalTime()
            var averageTime = getAverageTime()
            var mostClimbed =  getMostClimbedMountain(climbedMountains)
            var fastestClimb = fastestClimb(climbedMountains)
            val intent = Intent(this, StatsActivity::class.java)
                intent.putExtra("totalTime", totalTime)
                intent.putExtra("averageTime", averageTime)
                intent.putExtra("totalClimbs", totalClimbs)
                intent.putExtra("fastestClimb", fastestClimb)
                intent.putExtra("mostClimbed", mostClimbed)

            startActivity(intent)
        }


    }

    override suspend fun onClimbedMountainClick(climbedMountain: ClimbedMountain) {
        val mountain = app.mountains.findOneMountainByName(climbedMountain.mountainName)
        val mountainIntent = Intent(this, MountainActivity::class.java).apply {
            putExtra("mountain", mountain)
        }
        startActivity(mountainIntent)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun registerRefreshCallback() {
        refreshIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { binding.recyclerView.adapter!!.notifyDataSetChanged() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_climbed_list, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_home -> {
                val launcherIntent = Intent(this, MainActivity::class.java)
                mapIntentLauncher.launch(launcherIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun registerMapCallback() {
        mapIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            {}
    }

    private fun updateRecyclerView(climbedMountains: MutableList<ClimbedMountain>) {
        GlobalScope.launch(Dispatchers.Main) {
            binding.recyclerView.adapter =
                ClimbedMountainsAdapter(climbedMountains, this@ClimbedListActivity)
        }
    }

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
        //showClimbingStats(climbedMountains)


    }
    private fun filterMountainList(query: String?) {
        GlobalScope.launch(Dispatchers.Main) {
            val filteredList = climbedMountains.filter { it.mountainName.contains(query ?: "", true) }
                .filter { it.mountainName.contains(query ?: "", true) }
            (binding.recyclerView.adapter as ClimbedMountainsAdapter).updateList(filteredList)
    }
}

    private fun showClimbingStats(climbedMountains: List<ClimbedMountain>) {
        var totalClimbs = climbedMountains.size
        var fastestClimb: ClimbedMountain? = null
        for (climbedMountain in climbedMountains) {
            if (fastestClimb == null || climbedMountain.duration < fastestClimb.duration) {
                fastestClimb = climbedMountain
            }
        }
        val fastestClimbText = fastestClimb?.let {
            "Fastest climb: ${it.mountainName} (${it.duration} minutes)"
        } ?: "No climbs recorded"
        val totalClimbsText = "Total climbs: $totalClimbs"


    }

    private fun totalClimbs():Int {
        var totalClimbs = climbedMountains.size
        return totalClimbs
    }

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

    private fun getTotalTime(): Int {
        var totalTime = 0
        for (climbedMountain in climbedMountains) {
            totalTime += climbedMountain.duration
        }
        return totalTime
    }

    private fun getAverageTime(): Double {
        val totalTime = getTotalTime()
        return if (totalTime > 0) {
            totalTime.toDouble() / climbedMountains.size
        } else {
            0.0
        }
    }
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