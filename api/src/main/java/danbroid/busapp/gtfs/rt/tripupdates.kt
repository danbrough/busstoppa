package danbroid.busapp.gtfs

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StopTimeUpdate(
  @Json(name = "stop_sequence")
  val stopSequence: Int,
  @Json(name = "stop_id")
  val stopID: String,
  val arrival: Arrival
)

@JsonClass(generateAdapter = true)
data class TripUpdate(
  val timestamp: Long,
  @Json(name = "stop_time_update")
  val stopTimeUpdate: StopTimeUpdate
)
