package danbroid.busapp.metlinkold

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class StopDepartures(
  @Json(name = "farezone")
  val fareZone: String,
  @Json(name = "closed")
  val closed: Boolean,
  val departures: List<Departure>
) {
  @Transient
  var etag: String? = null

  @JsonClass(generateAdapter = true)
  data class StopTime(
    @Json(name = "aimed")
    val aimed: Date?,
    val expected: Date?
  )

  @JsonClass(generateAdapter = true)
  data class StopID(
    @Json(name = "stop_id")
    val stopID: String,
    val name: String
  )



  enum class DepartureStatus{
    @Json(name="")
    NOSTATUS,
    @Json(name="cancelled")
    CANCELLED,
    @Json(name="delayed")
    DELAYED,
    @Json(name="early")
    EARLY,
    @Json(name="ontime")
    ONTIME,
  }

  @JsonClass(generateAdapter = true)
  data class Departure(
    @Json(name = "stop_id")
    val stopID: String,
    @Json(name = "service_id")
    val serviceID: String,
    val direction: String,
    val operator: String,
    val origin: StopID,
    val destination: StopID,
    val delay: String,
    @Json(name = "vehicle_id")
    val vehicleID: String,
    val name: String,
    val arrival: StopTime,
    val departure: StopTime,
    val status: DepartureStatus,
    @Json(name = "wheelchair_accessible")
    val wheelChairAccessible: Boolean
  )
}

//private val log = org.slf4j.LoggerFactory.getLogger(StopDepartures::class.java)
