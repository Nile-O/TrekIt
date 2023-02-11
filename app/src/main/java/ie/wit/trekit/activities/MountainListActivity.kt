package ie.wit.trekit.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import ie.wit.trekit.adapters.MountainAdapter
import ie.wit.trekit.adapters.MountainListener
import ie.wit.trekit.main.MainApp
import ie.wit.trekit.databinding.ActivityMountainListBinding
import ie.wit.trekit.models.MountainModel

class MountainListActivity : AppCompatActivity(), MountainListener {

    lateinit var app: MainApp
    private lateinit var binding: ActivityMountainListBinding
    private lateinit var refreshIntentLauncher : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMountainListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MainApp

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = MountainAdapter(app.mountains.findAll(), this)

        registerRefreshCallback()

    }

    override fun onMountainClick(mountain: MountainModel) {
        val launcherIntent = Intent(this, MountainActivity::class.java)
        launcherIntent.putExtra("mountain_show", mountain)
        refreshIntentLauncher.launch(launcherIntent)
    }
    private fun registerRefreshCallback() {
        refreshIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { binding.recyclerView.adapter?.notifyDataSetChanged() }
    }

}

