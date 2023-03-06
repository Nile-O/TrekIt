package ie.wit.trekit.main

import android.app.Application
import ie.wit.trekit.models.*
import timber.log.Timber
import timber.log.Timber.i

class MainApp: Application() {

   // val mountains = MountainMemStore()

    lateinit var mountains: MountainStore

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        mountains = MountainFireStore(applicationContext)
        i("TrekIt started")
    }
}