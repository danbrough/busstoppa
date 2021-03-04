package danbroid.busapp.metlinkold

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.stream.JsonWriter
import danbroid.busapp.okhttpClient
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Before
import org.junit.Test
import org.slf4j.LoggerFactory
import retrofit2.Call
import java.io.StringWriter
import java.security.MessageDigest

private val log = LoggerFactory.getLogger(MetlinkTests::class.java)

fun <T> metlinkCall(call: Call<T>, handler: (T) -> Unit) {
  val response = call.execute()
  if (response.isSuccessful) {
    handler(response.body()!!)
  } else {
    throw Exception("Response: ${response.code()} : ${response.message()}")
  }
}

class MetlinkTests {

  lateinit var context: Context
  lateinit var metlink: MetlinkOld

  @Before
  fun setup() {
    context = ApplicationProvider.getApplicationContext()
    metlink = context.metlink
  }

  @Test
  fun testStopInfo() {
    log.debug("testStopInfo()")
    metlinkCall(metlink.stopInfo("5313")) {
      log.info("FOUND STOP $it")
    }
  }

  @Test
  fun testStopsNearby() {
    runBlocking {
      log.info("testStopsNearby()")
      metlink.stopsNearby(-41.33048247835469, 174.82289151448117).execute()?.body()?.forEach {
        log.debug("FOUND STOP: $it")
      }
    }
  }


  @Test
  fun stopSearch() {
    runBlocking {
      val query = "tea"
      log.info("Performing a stop search with $query")
      metlink.stopSearch(query).execute()?.body()?.foldRight("") { info, content ->
        if (content.isEmpty()) info.name else "$content,${info.name}"
      }.also {
        log.debug("stops: $it")
      }
    }
  }


  @Test
  fun testStopDepartures() {
    runBlocking {
      val stop = "WELL"
      metlink.stopDepartures(stop)?.execute()?.body()?.let {
        log.debug("departures $it")
      }
    }
  }

  @Test
  fun test() {
    runBlocking {
      log.error("cacheTest()")
      test(context.okhttpClient)
      log.error("noCacheTest()")
      test(OkHttpClient())
    }
  }

  @Test
  fun testStopList() {
    runBlocking {
      log.error("testStopList()")
      metlink.stopList().execute()?.body()?.let { stopList ->
        log.debug("got stop list: modified: ${stopList.lastModified}")
        stopList.stops.forEach { stop ->
          log.debug("stop: $stop")
        }

      }
    }
  }

  private fun test(httpClient: OkHttpClient) {
    httpClient.newCall(
      Request.Builder()
        .url("https://www.metlink.org.nz/api/v1/StopList/")
        .build()
    ).execute()?.body()?.let {
      log.debug("got response")
      val json = JsonParser().parse(it.charStream()).asJsonObject
      json.getAsJsonArray("Stops").forEach {
        (it as JsonObject).remove("LastModified")
      }
      val digest = MessageDigest.getInstance("MD5")
      val out = StringWriter()
      val writer = JsonWriter(out)
      GsonBuilder().create().toJson(json, writer)
      digest.update(out.buffer.toString().toByteArray())
      out.close()

      val hash = digest.digest()
      log.debug("hash is $hash")
    }
  }

}
