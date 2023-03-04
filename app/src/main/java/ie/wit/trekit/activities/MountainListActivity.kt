package ie.wit.trekit.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import ie.wit.trekit.R
import ie.wit.trekit.adapters.MountainAdapter
import ie.wit.trekit.adapters.MountainListener
import ie.wit.trekit.main.MainApp
import ie.wit.trekit.databinding.ActivityMountainListBinding
import ie.wit.trekit.models.MountainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MountainListActivity : AppCompatActivity(), MountainListener {

    lateinit var app: MainApp
    private lateinit var binding: ActivityMountainListBinding
    private lateinit var refreshIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var mapIntentLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMountainListBinding.inflate(layoutInflater)
        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)
        setContentView(binding.root)

        app = application as MainApp

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        updateRecyclerView()

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
        menuInflater.inflate(R.menu.menu_mountain_list, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_cancel -> {
                finish()
            }
            R.id.item_favouriteList -> {
                val launcherIntent = Intent(this, FavouriteListActivity::class.java)
                mapIntentLauncher.launch(launcherIntent)
            }
            R.id.item_map -> {
                val launcherIntent = Intent(this, MountainMapsActivity::class.java)
                mapIntentLauncher.launch(launcherIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun registerMapCallback() {
        mapIntentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {}
    }

    private fun updateRecyclerView() {
        GlobalScope.launch(Dispatchers.Main) {
            binding.recyclerView.adapter = MountainAdapter(app.mountains.findAll(), this@MountainListActivity)
        }
    }

}

