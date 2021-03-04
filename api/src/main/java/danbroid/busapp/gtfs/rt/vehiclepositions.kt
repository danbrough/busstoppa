package danbroid.busapp.gtfs

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VehiclePosition(val id:String)