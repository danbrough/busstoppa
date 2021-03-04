package  danbroid.busapp.gtfs

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class Stop(
  val id: Long,
  @Json(name = "stop_id")
//@SerialName("stop_id")
  val stopID: String,
  @Json(name = "stop_code")
//@SerialName("stop_code")
  val code: String,
  @Json(name = "stop_name")
//@SerialName("stop_name")
  val name: String,
  @Json(name = "stop_desc")
//@SerialName("stop_desc")
  val description: String,
  @Json(name = "zone_id")
//@SerialName("zone_id")
  val zoneID: String,
  @Json(name = "stop_lat")
//@SerialName("stop_lat")
  val latitude: Double,
  @Json(name = "stop_lon")
//@SerialName("stop_lon")
  val longitude: Double,
  @Json(name = "location_type")
//@SerialName("location_type")
  val locationType: Int,
  @Json(name = "parent_station")
//@SerialName("parent_station")
  val parentStation: String,
  @Json(name = "stop_url")
//@SerialName("stop_url")
  val stopUrl: String,
  @Json(name = "stop_timezone")
//@SerialName("stop_timezone")
  val stopTimezone: String,
)

