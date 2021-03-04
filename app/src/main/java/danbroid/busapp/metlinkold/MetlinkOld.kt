package danbroid.busapp.metlinkold

import com.google.gson.annotations.SerializedName
import danbroid.busapp.data.BusStop
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.*


val metlinkOld: MetlinkOld by lazy {
  Retrofit.Builder()
    .client(OkHttpClient())
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl("https://old.metlink.org.nz/api/v1/")
    //.baseUrl("https://www.metlink.org.nz/api/v1/")
    .build().create(MetlinkOld::class.java)
}


@Deprecated("Move to new api")
interface MetlinkOld {

/*  companion object {

    private const val METLINK_DATE_FORMAT = "yyyy-MM-persistanceID'T'HH:mm:ssZ"

    @SuppressLint("SimpleDateFormat")
    private val metlinkDateFormat = SimpleDateFormat(Metlink.METLINK_DATE_FORMAT)

    fun parseDate(dateString: String): Long = metlinkDateFormat.parse(dateString).time
  }*/

  @GET("Stop/{code}")
 suspend fun stopInfo(
    @Path("code") code: String
  ): BusStop

/*  @GET("StopSearch/{query}")
  fun stopSearch(
    @Path("query") query: String
  ): Call<List<BusStopInfo>>*/

/*
  @GET("StopNearby/{lat}/{lng}")
  fun stopsNearby(
    @Path("lat") lat: Double,
    @Path("lng") lng: Double
  ): Call<List<BusStop>>
*/

  @GET("StopDepartures/{code}")
  suspend fun stopDepartures(
    @Path("code") code: String
  ): StopDeparturesOld

  @GET("StopList")
  fun stopList(): Call<StopList>
}


data class BusStopInfo(
  @SerializedName("ID")
  val id: String,
  @SerializedName("Sms")
  val code: String,
  @SerializedName("Name")
  val name: String
)


/*
 "Service": {
        "Code": "262",
        "TrimmedCode": "262",
        "Name": "Paraparaumu Beach - Paraparaumu (via Mazengarb Road)",
        "Mode": "Bus",
        "Link": "\/timetables\/bus\/262"
      }
 */
data class Service(
  @SerializedName("Code")
  val code: String,
  @SerializedName("TrimmedCode")
  val trimmedCode: String,
  @SerializedName("Name")
  val name: String,
  @SerializedName("Mode")
  val mode: String,
  @SerializedName("Link")
  val link: String
)


/*
"Services": [
    {
      "ServiceID": "262",
      "IsRealtime": true,
      "VehicleRef": "9017",
      "Direction": "Outbound",
      "OperatorRef": "UZM",
      "OriginStopID": "1001",
      "OriginStopName": "ParaparaumuStn",
      "DestinationStopID": "1148",
      "DestinationStopName": "Paraparaumu Beach",
      "AimedArrival": "2018-08-23T08:07:00+12:00",
      "AimedDeparture": "2018-08-23T08:07:00+12:00",
      "VehicleFeature": null,
      "DepartureStatus": "onTime",
      "ExpectedDeparture": "2018-08-23T08:07:28+12:00",
      "DisplayDeparture": "2018-08-23T08:07:28+12:00",
      "DisplayDepartureSeconds": 601,
      "Service": {
        "Code": "262",
        "TrimmedCode": "262",
        "Name": "Paraparaumu Beach - Paraparaumu (via Mazengarb Road)",
        "Mode": "Bus",
        "Link": "\/timetables\/bus\/262"
      }
    }
 */
/*
	"Notices": [
		{
			"RecordedAtTime": "2018-08-23T10:45:24+12:00",
			"MonitoringRef": "WELL",
			"LineRef": "",
			"DirectionRef": "",
			"LineNote": "WRL: 25 - 26 Aug - Some WRL services between WELL and MAST will be replaced by bus. See metlink.org.nz for more
information."
*/

data class Notice(
  @SerializedName("RecordedAtTime")
  val recordedAtTime: Date,
  @SerializedName("LineNote")
  val lineNote: String
)

data class DepartureStatus(
  @SerializedName("ServiceID")
  val serviceID: String,
  @SerializedName("IsRealtime")
  val isRealtime: Boolean,
  @SerializedName("VehicleRef")
  val vehicleRef: String,
  @SerializedName("Direction")
  val direction: String,
  @SerializedName("DestinationStopID")
  val destinationStopID: String,
  @SerializedName("DestinationStopName")
  val destinationStopName: String,
  //null or lowFloor
  @SerializedName("VehicleFeature")
  val vehicleFeature: String?,

  /**
   * "DepartureStatus":"cancelled",
   * "DepartureStatus":"delayed",
   * "DepartureStatus":"early",
   * "DepartureStatus":"onTime",
   * "DepartureStatus":null,
   */
  @SerializedName("DepartureStatus")
  val departureStatus: String?,
  @SerializedName("ExpectedDeparture")
  val expectedDeparture: Date,
  @SerializedName("DisplayDeparture")
  val displayDeparture: Date,
  @SerializedName("DisplayDepartureSeconds")
  val displayDepartureSeconds: Int,
  @SerializedName("Service")
  val service: Service
) {

  val timetableURL: String
    get() = "https://www.metlink.org.nz/service/${service.code}/timetable"
  //"https://old.metlink.org.nz${service.link}${if (direction == "Outbound") "/outbound" else ""}"

}

data class StopDeparturesOld(
  @SerializedName("LastModified")
  val lastModified: Date,
  @SerializedName("Stop")
  val stop: BusStop,
  @SerializedName("Notices")
  val notices: List<Notice>?,
  @SerializedName("Services")
  val services: List<DepartureStatus>?
)

/**
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
val status: String,
@Json(name = "wheelchair_accessible")
val wheelChairAccessible: Boolean
)
 */
/*fun StopDepartures.Departure.toOld(): DepartureStatus
 = DepartureStatus()
fun StopDepartures.toOld(lastModified: Date,stop: BusStop): StopDeparturesOld =
  StopDeparturesOld(lastModified,stop,null,departures.map{it.toOld()})*/


data class StopList(
  @SerializedName("LastModified")
  val lastModified: Date,
  @SerializedName("Stops")
  val stops: List<BusStop>
)






