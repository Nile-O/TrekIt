package ie.wit.trekit.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import ie.wit.trekit.R
import ie.wit.trekit.databinding.ActivityMainBinding
import ie.wit.trekit.main.MainApp
import ie.wit.trekit.views.login.LoginView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var binding: ActivityMainBinding
    var app: MainApp? = null
    private lateinit var editIntentLauncher: ActivityResultLauncher<Intent>


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        app = application as MainApp

        //binding buttons to views and setting activities to be launched when clicked
        val allBtn = findViewById<Button>(R.id.allBtn)
        val faveBtn = findViewById<Button>(R.id.faveBtn)
        val climbBtn = findViewById<Button>(R.id.climbBtn)

        allBtn.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    MountainListActivity::class.java
                )
            )
        }

        faveBtn.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    FavouriteListActivity::class.java
                )
            )
        }

        climbBtn.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    ClimbedListActivity::class.java
                )
            )
        }

        registerEditCallback()

        //set up of navigation drawer and selection of items from menu in drawer
        val actionbar: androidx.appcompat.app.ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)
        }
        mDrawerLayout = findViewById(R.id.drawerLayout)

        val navigationView: NavigationView = findViewById(R.id.nav_menu)
        val headerView = navigationView.getHeaderView(0)

        val welcomeTextView: TextView = headerView.findViewById(R.id.action_bar_welcome)
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userEmail = currentUser?.email
        welcomeTextView.text = "Welcome $userEmail"

        navigationView.setNavigationItemSelectedListener { menuItem ->
            // set item as selected to persist highlight
            menuItem.isChecked = true
            // close drawer when item is tapped
            mDrawerLayout.closeDrawers()

            // Handle navigation view item clicks here.
            when (menuItem.itemId) {

                R.id.item_logout -> {
                    GlobalScope.launch(Dispatchers.IO) { doLogout()}
                        Toast.makeText(this, "Logout", LENGTH_SHORT).show()
                }
                R.id.item_climbed -> {
                    doClimbedPeaks()
                }
                R.id.item_all -> {
                    doAllPeaks()
                }
                R.id.item_profile -> {
                    Toast.makeText(this, "Profile features coming soon!", LENGTH_SHORT).show()
                }
                R.id.item_favourite -> {
                    doFavouritePeaks()
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

    //logout of app
    private suspend fun doLogout() {
        FirebaseAuth.getInstance().signOut()
        app?.mountains?.clear()
        val launcherIntent = Intent(this, LoginView::class.java)
        editIntentLauncher.launch(launcherIntent)
    }

    //open mountainlistactivity
    private fun doAllPeaks() {
        val launcherIntent = Intent(this, MountainListActivity::class.java)
        editIntentLauncher.launch(launcherIntent)
    }
    //open favouritelistactivity
    private fun doFavouritePeaks() {
        val launcherIntent = Intent(this, FavouriteListActivity::class.java)
        editIntentLauncher.launch(launcherIntent)
    }
    //open climbedlistactivity
    private fun doClimbedPeaks() {
        val launcherIntent = Intent(this, ClimbedListActivity::class.java)
        editIntentLauncher.launch(launcherIntent)
    }
}