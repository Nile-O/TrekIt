package ie.wit.trekit.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class MountainModel( var fbId: String = "",
                          var id: Long = 0,
                          var mountainName: String = "",
                          var areaName: String = "",
                          var elevation: Double = 0.0,
                          var mountainLat: Double = 0.0,
                          var mountainLong: Double = 0.0,
                          var isFavourite: Boolean = false) : Parcelable

@Parcelize
data class ClimbedMountain(
    val mountainName: String = "",
    val dateClimbed: String = "",
    val duration: Int = 0,
    var key: String? = null

) : Parcelable