package ie.wit.trekit.views.mountain

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import ie.wit.trekit.R
import ie.wit.trekit.databinding.ActivityMountainBinding
import ie.wit.trekit.models.MountainModel

class MountainView : AppCompatActivity() {
    private lateinit var binding: ActivityMountainBinding
    private lateinit var presenter: MountainPresenter
    var mountain = MountainModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMountainBinding.inflate(layoutInflater)
        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)
        setContentView(binding.root)

        presenter = MountainPresenter(this)


            binding.mountainLocation.setOnClickListener{
                presenter.cachePlacemark(binding.mountainName.text.toString(),binding.areaName.text.toString(),
                    (binding.mountainLong.text as String).toDouble(), (binding.elevation.text as String).toDouble(), (binding.mountainLat.text as String).toDouble())
                presenter.doSetLocation()

            }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_mountain_activity, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_cancel -> {
                presenter.doCancel()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun showMountain(mountain: MountainModel){

        binding.mountainName.text = mountain.mountainName
        binding.areaName.text = mountain.areaName
        binding.elevation.text = mountain.elevation.toString()
        binding.mountainLat.text = mountain.mountainLat.toString()
        binding.mountainLong.text = mountain.mountainLong.toString()
    }
}