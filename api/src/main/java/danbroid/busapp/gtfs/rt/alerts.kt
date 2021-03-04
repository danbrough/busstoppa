package danbroid.busapp.gtfs

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class Alert(
  val effect: Effect, val cause: Cause,
  @Json(name = "active_period")
  val activePeriod: List<TimePeriod>,
  @Json(name = "description_text")
  val descriptionText: TranslatedText,
  @Json(name = "header_text")
  val headerText: TranslatedText,
  @Json(name = "severity_level")
  val severityLevel: SeverityLevel,
  @Json(name = "url")
  val urlText: TranslatedText?,
  @Json(name="informed_entity")
  val regarding: List<InformedEntity>
) {

  val url: String?
    get() = urlText?.text()

  val description: String?
    get() = descriptionText.text()

  val header: String?
    get() = headerText.text()

  enum class SeverityLevel {
    INFO, WARNING
  }

  @JsonClass(generateAdapter = true)
  data class InformedEntity(
    @Json(name="route_id")
    val routeID:String? = null,
    @Json(name="route_type")
    val routeType:Int? = null,
    @Json(name="stop_id")
    val stopID:String? = null
  )

  enum class Effect {
    NO_EFFECT, NO_SERVICE, REDUCED_SERVICE, SIGNIFICANT_DELAYS, DETOUR, ADDITIONAL_SERVICE, MODIFIED_SERVICE, OTHER_EFFECT, UNKNOWN_EFFECT, STOP_MOVED, ACCESSIBILITY_ISSUE
  }

  enum class Cause {
    UNKNOWN_CAUSE,
    OTHER_CAUSE,
    TECHNICAL_PROBLEM,
    STRIKE,
    DEMONSTRATION,
    ACCIDENT,
    HOLIDAY,
    WEATHER,
    MAINTENANCE,
    CONSTRUCTION,
    POLICE_ACTIVITY,
    MEDICAL_EMERGENCY
  }
}