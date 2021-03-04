package  danbroid.busapp.gtfs

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class Route(
  @Json(name = "route_id")
  val routeID: String,
  @Json(name = "agency_id")
  val agencyID: String?,
  @Json(name = "route_short_name")
  val routeShortName: String?,
  @Json(name = "route_long_name")
  val routeLongName: String?,
  @Json(name = "route_type")
  val routeType:Int,
  @Json(name = "route_color")
  val routeColor: String?,
  @Json(name = "route_text_color")
  val routeTextColor: String?,
  @Json(name = "route_url")
  val routeUrl: String?,
)