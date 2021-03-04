package danbroid.busapp.gtfs

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*


@JsonClass(generateAdapter = true)
data class Arrival(val delay: Int, val time: String)


@JsonClass(generateAdapter = true)
data class TimePeriod(val start: Long, val end: Long)

@JsonClass(generateAdapter = true)
data class TextTranslation(val language: String, val text: String?)

@JsonClass(generateAdapter = true)
data class TranslatedText(val translation: List<TextTranslation>)

fun TranslatedText.text(): String? = this.translation.find { it.language == "en" }?.text



@JsonClass(generateAdapter = true)
data class FeedHeader(val timestamp: Long)

@JsonClass(generateAdapter = true)
data class FeedEntity(
  val id: String,
  @Json(name = "trip_update") val tripUpdate: TripUpdate? = null,
  @Json(name = "alert") val alert: Alert? = null,
  @Json(name = "vehicle") val vehiclePositions: VehiclePosition? = null,
){
  fun isForStop(stopID:String):Boolean = alert?.regarding?.firstOrNull {
    it.stopID == stopID
  } != null
}

@JsonClass(generateAdapter = true)
data class FeedMessage(
  val header: FeedHeader,
  val entity: List<FeedEntity>
)


