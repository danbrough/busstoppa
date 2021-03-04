package danbroid.busapp

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.util.TypedValue
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import danbroid.busapp.data.BusStopRepository
import danbroid.busapp.metlink.Metlink
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit


/**
 * Context level variables
 */

private const val CACHE_SIZE = 10 * 1024 * 1024L


private val log by lazy {
  org.slf4j.LoggerFactory.getLogger(Singletons::class.java)
}

private const val HTTP_TIMEOUT_SECS = 5L

private class Singletons(context: Context) {


  val cache: Cache by lazy {
    Cache(File(context.cacheDir, "okhttp"), CACHE_SIZE)
  }

  val repository by lazy {
    BusStopRepository(context)
  }

/*  val metlinkAPI: MetlinkAPI by lazy {
    MetlinkAPI(okhttpClient)
  }*/

  val okhttpClient: OkHttpClient by lazy {
    OkHttpClient.Builder()
      .cache(cache)
      .readTimeout(HTTP_TIMEOUT_SECS, TimeUnit.SECONDS)
      .connectTimeout(HTTP_TIMEOUT_SECS, TimeUnit.SECONDS)
      .writeTimeout(HTTP_TIMEOUT_SECS, TimeUnit.SECONDS)
      .also {

        if (BuildConfig.DEBUG)
          it.addInterceptor {

            //val request = it.request().newBuilder().build()

/*          .cacheControl(
          CacheControl.Builder()
            //.maxAge(1, TimeUnit.DAYS)
            .build()
        ).build()*/


            val request = it.request()
            val response = it.proceed(request)

            if (response.cacheResponse != null)
              log.trace("cached response for ${request.url}")

            if (response.networkResponse != null)
              log.trace("network response for ${request.url}")

            response
          }
      }

      .build()
  }

  val newMetlink by lazy {
    Metlink(context.getString(R.string.metlink_api_key), okhttpClient)
  }


/*    Retrofit.Builder()
      .client(OkHttpClient())
      .addConverterFactory(
        GsonConverterFactory.create(
          GsonBuilder()
            .create()
        )
      )*/


  companion object {
    @Volatile
    var instance: Singletons? = null

    fun getInstance(context: Context) = instance ?: synchronized(this) {
      instance ?: Singletons(context).also { instance = it }
    }
  }

}

fun Context?.debugToast(msg: CharSequence): Unit =
  if (this != null && BuildConfig.DEBUG) {
    try {
      log.debug(msg.toString())
      Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
    } catch (t: Throwable) {
      log.error(t.message, t)
    }
  } else Unit

private val Context.singletons
  get() = Singletons.getInstance(this)

val Context.okhttpClient
  get() = singletons.okhttpClient

/*val Context.metlinkApi: MetlinkAPI
  get() = singletons.metlinkAPI*/


val Context.prefs: SharedPreferences
  get() = getSharedPreferences("prefs", Context.MODE_PRIVATE)

val Context.repository: BusStopRepository
  get() = singletons.repository

val Context.metlink: Metlink
  get() = singletons.newMetlink


@ColorInt
fun Resources.Theme.themeColor(@AttrRes colorAttr: Int): Int = TypedValue().let {
  resolveAttribute(colorAttr, it, true)
  it.data
}


const val INTENT_EXTRA_STOP_CODE = "code"





