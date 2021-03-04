package danbroid.busapp.models

import android.app.Application
import android.content.SharedPreferences
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import danbroid.busapp.data.BusStop
import danbroid.busapp.gtfs.Route
import danbroid.busapp.metlink
import danbroid.busapp.metlink.Metlink
import danbroid.busapp.metlinkold.BusStopInfo
import danbroid.busapp.metlinkold.metlinkOld
import danbroid.busapp.prefs
import danbroid.busapp.repository
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.TimeUnit


class AppModel(val context: Application) : AndroidViewModel(context), HasPrefs {



  private val parentJob = Job()
  private var searchJob: Job? = null
  private val metlink: Metlink = context.metlink


  val errorMessage = object : MutableLiveData<String>() {
    override fun setValue(value: String?) {
      if (super.getValue() == value) return
      super.setValue(value)
    }
  }

  private val _routes: MutableLiveData<List<Route>> = object : MutableLiveData<List<Route>>() {
    override fun onActive() {
      super.onActive()
      loadRoutes()
    }

    override fun onInactive() {
      super.onInactive()
      log.info("_routes onInactive()")
    }
  }

  val routes: LiveData<List<Route>> = _routes

  private val _results = MutableLiveData<List<BusStopInfo>>()

  val results: LiveData<List<BusStopInfo>>
    get() = _results

  init {
    log.trace("Created AppModel")
    loadRoutes()
  }

  private fun launch(block: suspend CoroutineScope.() -> Unit) =
    viewModelScope.launch(Dispatchers.IO, block = block)

  fun removeStop(stop: BusStop) = launch {
    repository.db.stopDAO.delete(stop)
  }

  fun updateStop(stop: BusStop) = launch {
    repository.db.stopDAO.update(stop)
  }

  fun addStop(stop: BusStop, retainOrder: Boolean = false) = launch {
    repository.db.stopDAO.addStop(stop, retainOrder)
  }

  suspend fun addStop(stopCode: String) {
    log.info("addStop() $stopCode")
    try {
      metlinkOld.stopInfo(stopCode).also {
        addStop(it)
      }
    } catch (err: Exception) {
      log.error(err.message, err)
    }
  }

  private fun loadRoutes() = viewModelScope.launch(Dispatchers.IO) {
    log.warn("loadRoutes()")
    metlink.getRoutes().also {
      _routes.postValue(it)
    }
  }.also {
    it.invokeOnCompletion {
      if (it != null) {
        log.error("loadRoutes failed: ${it.message}", it)
        errorMessage.postValue("Whoops: ${it.message}")
      }
    }
  }

  override val prefs: SharedPreferences
    get() = context.prefs

  val repository = context.repository

  val recentStops: LiveData<List<BusStop>> = repository.db.stopDAO.getAllStops()


/*

  fun findNearbyStops(lat: Double, lng: Double) = GlobalScope.async {
    metlink.stopsNearby(lat, lng).execute().body()
  }
*/

  init {
    //context.debugToast("created app model")


    MigrationManager(this)

  }


  fun cancelSearch() {
    searchJob?.apply {
      log.trace("cancelling search job")
      cancel()
      searchJob = null
      _results.value = emptyList()
    }
  }

  override fun onCleared() {
    log.warn("onCleared()")
    parentJob.cancel()
  }


  fun performSearch(queryString: String) {
    cancelSearch()

    val query = queryString.replace("[^0-9a-zA-Z]".toRegex(), "").trim()


    if (query.length < 3) return
/*    if (query.length == 3) {
      try {
        Integer.parseInt(query)
      } catch (e: Exception) {
        return
      }
    }*/

    log.trace("performSearch() $query")

    try {

      searchJob = GlobalScope.launch(Dispatchers.Main + parentJob) {

        delay(TimeUnit.MILLISECONDS.toMillis(500))


        val regex = ".*${query.split("\\s+").joinToString(".*")}.*".toRegex(RegexOption.IGNORE_CASE)
        log.trace("performing a search with $regex")
        metlink.getStops().filter {
          "${it.code} ${it.name}".matches(regex)
        }.sortedBy {
          it.code.toLowerCase(Locale.ROOT) == query
        }.take(20).also {
          _results.postValue(it.map { BusStopInfo(it.stopID, it.code, it.name) })
        }


        /*    stopSearch = metlink.stopSearch(query).also {

              it.enqueue(object : Callback<List<BusStopInfo>> {

                override fun onFailure(call: Call<List<BusStopInfo>>, t: Throwable) {
                  log.error("Error ${t.message}", t)
                  searchBusy.postValue(false)
                }

                override fun onResponse(
                  call: Call<List<BusStopInfo>>,
                  response: Response<List<BusStopInfo>>
                ) {
                  searchBusy.postValue(false)
                  response.body()?.let {
                    _results.postValue(it)
                  }
                }

              })
            }*/

      }
      searchJob?.invokeOnCompletion {
        //    searchBusy.value = false
      }
    } catch (err: Exception) {
      log.error(err.message, err)
    }
  }

  fun moveStop(srcStop: BusStop, destStop: BusStop) =
    launch {
      repository.db.stopDAO.moveStop(srcStop, destStop)
    }


}





private val log = org.slf4j.LoggerFactory.getLogger(AppModel::class.java)

