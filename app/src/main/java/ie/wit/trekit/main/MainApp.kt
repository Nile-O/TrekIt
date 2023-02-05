package ie.wit.trekit.main

import android.app.Application
import ie.wit.trekit.models.MountainJSONStore
import ie.wit.trekit.models.MountainMemStore
import ie.wit.trekit.models.MountainModel
import ie.wit.trekit.models.MountainStore
import timber.log.Timber
import timber.log.Timber.i

class MainApp: Application() {

   // val mountains = MountainMemStore()

    lateinit var mountains: MountainStore
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        mountains = MountainJSONStore(applicationContext)
        i("TrekIt started")
       // mountains.create(MountainModel(1,"One", "About one...", 1200.0))
        //mountains.create(MountainModel(2,"two", "About one...", 1400.8))
    }
}