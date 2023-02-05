package ie.wit.trekit.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import ie.wit.trekit.adapters.MountainAdapter
import ie.wit.trekit.adapters.MountainListener
import ie.wit.trekit.main.MainApp
import ie.wit.trekit.databinding.ActivityMountainListBinding
import ie.wit.trekit.models.MountainModel

class MountainListActivity : AppCompatActivity(), MountainListener {

    lateinit var app: MainApp
    private lateinit var binding: ActivityMountainListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMountainListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MainApp

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = MountainAdapter(app.mountains.findAll(), this)
        //loadMountains()

    }

    override fun onMountainClick(mountain: MountainModel) {
        val launcherIntent = Intent(this, MountainActivity::class.java)
        launcherIntent.putExtra("mountain_show", mountain)
        startActivityForResult(launcherIntent,0)
    }


   // private fun loadMountains() {
  //      showMountains(app.mountains.findAll())
   // }

  //  fun showMountains (mountains: List<MountainModel>) {
    //    binding.recyclerView.adapter = MountainAdapter(mountains)
    //    binding.recyclerView.adapter?.notifyDataSetChanged()
  //  }

}

