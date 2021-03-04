package danbroid.busapp.test

import danbroid.busapp.metlink.Metlink
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.Cache
import okhttp3.OkHttpClient
import okio.buffer
import okio.source
import org.junit.Test
import java.io.File
import java.io.FileInputStream
import kotlin.system.measureTimeMillis

class Tests {

  fun sysProp(name:String,default:String) = System.getProperty(name).let {
    if (it.isNullOrBlank()) default else it
  }

  @Test
  fun stopDepartures() {
    stopDepartures(sysProp("stopCode", "WELL"))
  }

  fun stopDepartures(stopCode: String) {
    log.info("stopDepartures() $stopCode")
    runBlocking {
      (0 .. 10).forEach {
        testMetlink.getStopDepartures(stopCode).also {

          log.debug("etag: ${it.etag}")
        }
        delay(10000)
      }
    }
  }

  @Test
  fun test1() {
    runBlocking {
      var startTime = System.currentTimeMillis()
      testMetlink.getStops().also {
        log.debug("received ${it.size} stops in ${System.currentTimeMillis() - startTime}")
      }

      startTime = System.currentTimeMillis()
      testMetlink.getStops().also {
        log.debug("received ${it.size} stops in ${System.currentTimeMillis() - startTime}")
      }

      startTime = System.currentTimeMillis()
      testMetlink.api.routes().also {
        log.debug("received ${it.size} routes in ${System.currentTimeMillis() - startTime}")
      }

      startTime = System.currentTimeMillis()
      testMetlink.api.routes().also {
        log.debug("received ${it.size} routes in ${System.currentTimeMillis() - startTime}")
      }
    }
  }

  @Test
  fun stops() {
    runBlocking {
      val startTime = System.currentTimeMillis()
      testMetlinkAPI.getStops().also {
        log.debug("received ${it.size} stops in ${System.currentTimeMillis() - startTime}")
      }
    }
  }

  @Test
  fun stops2() {
    runBlocking {
      val startTime = System.currentTimeMillis()
      testMetlink.getStops().also {
        log.debug("received ${it.size} stops in ${System.currentTimeMillis() - startTime}")
      }
    }
  }

  @Test
  fun stopDeparturesOld() {
    runBlocking {
      testMetlinkAPI.getStopDepartures("7336").also {
        log.debug("got departures: $it")
      }
    }
  }

  suspend fun stopSearch(query: String) {
    measureTimeMillis {
      log.info("stopSearch() $query")
      val regex = ".*$query.*".toRegex(RegexOption.IGNORE_CASE)
      testMetlink.getStops().filter {
        val code = it.code.toLowerCase()
        val name = it.name.toLowerCase()
        "$code $name".matches(regex)
      }.sortedBy {
        it.code.toLowerCase() == query
      }
        .also {
          log.debug("found ${it.size} matches")
          it.take(5).forEach {
            log.debug("stop: ${it.code} title: ${it.name}")
          }
        }
    }.also {
      log.trace("search took: $it millis")
    }
  }

  @Test
  fun stopSearch() {
    runBlocking {
      System.getProperty("stopSearchQuery")?.also {
        stopSearch(it)
        return@runBlocking
      }
      stopSearch("wellington")
      stopSearch("6017")
      stopSearch("lane")
    }

  }


  @Test
  fun tripUpdates() {
    FileInputStream("../data/metlink2/tripupdates.json").use {
      testMetlinkAPI.entityListAdapter.fromJson(it.source().buffer())?.also {
        log.debug("header: ${it.header}")
        it.entity.forEach {
          log.debug("update: $it")
        }
      }
    }
  }

  @Test
  fun serviceAlerts() {
    FileInputStream("../data/metlink2/servicealerts.json").use {
      testMetlinkAPI.entityListAdapter.fromJson(it.source().buffer())?.also {
        log.debug("header: ${it.header}")

        it.entity.forEach {
          log.debug("update: $it")
        }
      }
    }
  }

  @Test
  fun test6() {
    runBlocking {
      testMetlink.api.rtServiceAlerts().entity.filter {
        it.isForStop("WELL")
      }.forEach {
        log.info("alert: ${it.alert}")
      }
    }
  }
}

private object testMetlinkAPI : Metlink(
  System.getenv("METLINK_API_KEY")
    ?: throw Exception("METLINK_API_KEY environment variable not set"), okhttpClient
)

val okhttpClient by lazy {
  val cache = Cache(File("/tmp/metlink"), 1024 * 1024 * 20)
  OkHttpClient.Builder()
    .cache(cache)
    .addInterceptor {
      val request = it.request()
      val response = it.proceed(request)
      response.networkResponse?.also {
        log.trace("network response")
      }
      response.cacheResponse?.also {
        log.trace("cache response")
      }
      response.headers.forEach {
        log.trace("header: ${it.first} = ${it.second}")
      }
      response
    }
    .build()
}

private val testMetlink by lazy {
  val apiKey = System.getenv("METLINK_API_KEY")
    ?: throw Exception("METLINK_API_KEY environment variable not set")
  Metlink(apiKey, okhttpClient)
}


private val log = org.slf4j.LoggerFactory.getLogger(Tests::class.java)
