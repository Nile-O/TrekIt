package ie.wit.trekit.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MountainModel( var mountainName: String = "", var areaName: String = "", var elevation: Double = 0.0) : Parcelable