package danbroid.busapp.metlink

import com.squareup.moshi.*
import danbroid.busapp.gtfs.FeedMessage
import danbroid.busapp.gtfs.Route
import danbroid.busapp.gtfs.Stop
import danbroid.busapp.metlink.MetlinkRetrofit
import danbroid.busapp.metlinkold.StopDepartures
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.buffer
import okio.source
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException
import java.util.*
import java.util.zip.GZIPInputStream


object MetLinkDateAdapter {

  val fmt = org.joda.time.format.DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ")

  @FromJson
  fun fromJson(reader: JsonReader): Date? =
    reader.readJsonValue()?.toString()?.let {
      if (it == "") null else fmt.parseDateTime(it).toDate()
    }


  @ToJson
  fun toJson(writer: JsonWriter, value: Date?) {
    if (value == null) writer.nullValue()
    else
      writer.value(fmt.print(value.time))
  }
}

fun metlinkAuth(apiKey: String) = Interceptor {
  val request = it.request().newBuilder()
    .addHeader("accept", "application/json")
    .addHeader("x-api-key", apiKey)
    .build()
  it.proceed(request).also {
    it.networkResponse?.also {
      log.info("NETWORK REQUEST")
    }
    it.cacheResponse?.also {
      log.info("CACHE REQUEST!")
    }
    val headers = it.headers
    val names = headers.names()
    names.forEach {
      log.info("HEADER: $it ${headers.get(it)}")
    }
  }
}

/*fun createMetlinkRetrofit(apiKey: String, okHttpClient: OkHttpClient) =
  Retrofit.Builder()
    .client(okHttpClient.newBuilder().addInterceptor(metlinkAuth(apiKey)).build())
    .addConverterFactory(
      MoshiConverterFactory.create(
        Moshi.Builder().add(MetLinkDateAdapter).build()
      )
    )
    .baseUrl(METLINK_API_URL)
    .build()
    .create(MetlinkRetrofit::class.java)*/


open class Metlink(apiKey: String, val okHttpClient: OkHttpClient) {

  val apiOkHttpClient = okHttpClient.newBuilder().addInterceptor(metlinkAuth(apiKey)).build()

  val api: MetlinkRetrofit

  val stopListAdapter: JsonAdapter<List<Stop>>
  val stopDeparturesAdapter: JsonAdapter<StopDepartures>
  val entityListAdapter: JsonAdapter<FeedMessage>
  val routeListAdapter: JsonAdapter<List<Route>>

  init {
    val moshi = Moshi.Builder().add(MetLinkDateAdapter).build()
    stopDeparturesAdapter = moshi.adapter()
    entityListAdapter = moshi.adapter()
    stopListAdapter = moshi.adapter()
    routeListAdapter = moshi.adapter()

    val apiRetrofit = Retrofit.Builder()
      .client(apiOkHttpClient)
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .baseUrl(METLINK_API_URL)
      .build()
      .create(MetlinkRetrofit::class.java)

    api = object : MetlinkRetrofit by apiRetrofit {
    }
  }

  @Suppress("BlockingMethodInNonBlockingContext")
  suspend fun getRoutes(): List<Route> = request(ContentUrls.URL_ROUTES_GZ, routeListAdapter,gzip = true)


  @Suppress("BlockingMethodInNonBlockingContext")
  suspend fun getStops(): List<Stop> = request(ContentUrls.URL_STOPS_GZ, stopListAdapter,gzip = true)


  @Suppress("BlockingMethodInNonBlockingContext")
  private suspend fun <T> request(url: String, adapter: JsonAdapter<T>, gzip: Boolean = false,processHeaders:((T,Headers)->Unit)?= null): T =
    withContext(Dispatchers.IO) {
      log.trace("requesting $url ...")
      Request.Builder().url(url).build().let {
        okHttpClient.newCall(it).execute().use { response->
          if (!response.isSuccessful)
            throw IOException("Request failed: error:${response.code} ${response.message}").also{
              log.error(it.message,it)
            }

          var input = response.body!!.byteStream()
          if (gzip) input = GZIPInputStream(input)
          adapter.fromJson(JsonReader.of(input.source().buffer()))!!.also {
            processHeaders?.invoke(it,response.headers)
          }
        }
      }
    }

  @Suppress("BlockingMethodInNonBlockingContext")
  suspend fun getStopDepartures(stopID: String): StopDepartures =
    request("${ContentUrls.URL_STOP_DEPARTURES}/$stopID", stopDeparturesAdapter){ departures,headers->
      departures.etag = headers.get("etag")
    }

}


private val log = org.slf4j.LoggerFactory.getLogger(Metlink::class.java)
