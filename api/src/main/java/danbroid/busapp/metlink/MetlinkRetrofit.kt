package danbroid.busapp.metlink

import danbroid.busapp.gtfs.FeedMessage
import danbroid.busapp.gtfs.Route
import danbroid.busapp.gtfs.Stop
import retrofit2.http.GET
import retrofit2.http.Query

const val METLINK_API_URL = "https://api.opendata.metlink.org.nz/v1/"

interface MetlinkRetrofit {

  @GET("gtfs-rt/servicealerts")
  suspend fun rtServiceAlerts(): FeedMessage

  @GET("gtfs-rt/tripupdates")
  suspend fun rtTripUpdates(): FeedMessage

  @GET("gtfs-rt/vehiclepositions")
  suspend fun rtVehiclePositions(): FeedMessage

  @GET("gtfs/routes")
  suspend fun routes(@Query("stop_id") stopID: String? = null): List<Route>

  @GET("gtfs/stops")
  suspend fun stops(
    @Query("route_id") routeID: String? = null,
    @Query("trip_id") tripID: String? = null,
  ): List<Stop>

}




