package ie.wit.trekit.activities

import android.app.ActionBar
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import ie.wit.trekit.R
import ie.wit.trekit.databinding.ActivityMainBinding
import ie.wit.trekit.main.MainApp
import ie.wit.trekit.models.MountainModel
import ie.wit.trekit.views.login.LoginView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import timber.log.Timber.DebugTree
import timber.log.Timber.i


class MainActivity : AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var binding: ActivityMainBinding
    var app: MainApp? = null
    private lateinit var refreshIntentLauncher: ActivityResultLauncher<Intent>
    private lateinit var editIntentLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        app = application as MainApp

        binding.buttonPeaks.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    this,
                    MountainListActivity::class.java
                )
            )
        })
        registerEditCallback()

        val actionbar: androidx.appcompat.app.ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_hiker_foreground)
        }
        mDrawerLayout = findViewById(R.id.drawerLayout)

        val navigationView: NavigationView = findViewById(R.id.nav_menu)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // set item as selected to persist highlight
            menuItem.isChecked = true
            // close drawer when item is tapped
            mDrawerLayout.closeDrawers()

            // Handle navigation view item clicks here.
            when (menuItem.itemId) {

                R.id.item_logout -> {
                    GlobalScope.launch(Dispatchers.IO) { doLogout()}
                    Toast.makeText(this, "Logout", Toast.LENGTH_LONG).show()
                }
                R.id.item_climbed -> {
                    Toast.makeText(this, "Climbed", Toast.LENGTH_LONG).show()
                }
                R.id.item_all -> {
                    doAllPeaks()
                    Toast.makeText(this, "All", Toast.LENGTH_LONG).show()
                }
                R.id.item_about -> {
                    Toast.makeText(this, "About", Toast.LENGTH_LONG).show()
                }
                R.id.item_favourite -> {
                    Toast.makeText(this, "Favourite", Toast.LENGTH_LONG).show()
                }
            }

            true
        }
    }

    private fun registerEditCallback() {
        editIntentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {  }
    }


    //appbar - toolbar button click
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                mDrawerLayout.openDrawer(GravityCompat.START)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private suspend fun doLogout() {
        FirebaseAuth.getInstance().signOut()
        app?.mountains?.clear()
        val launcherIntent = Intent(this, LoginView::class.java)
        editIntentLauncher.launch(launcherIntent)
    }

    private fun doAllPeaks() {
        val launcherIntent = Intent(this, MountainListActivity::class.java)
        editIntentLauncher.launch(launcherIntent)
    }
}