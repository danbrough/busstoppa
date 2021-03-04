package danbroid.busapp.models

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import danbroid.busapp.R
import danbroid.busapp.activities.appModel
import danbroid.busapp.gtfs.Stop
import danbroid.busapp.metlink
import danbroid.busapp.metlinkold.StopDepartures
import kotlinx.coroutines.*
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

const val REFRESH_INTERVAL = 15000L

class StopDeparturesModel(context: Context, val appModel: AppModel, val stopCode: String) :
  ViewModel() {


  val metlink = context.metlink
  var timeoutMessage = context.getString(R.string.msg_timeout)


/*
  init {
    appModel.currentStop.observeForever {
      log.trace("current stop: ${it.code} this stop: ${stop.code}")
      live.value = it.code == stop.code
    }
  }*/


  private val _stopDepartures = object : MutableLiveData<Pair<Stop, StopDepartures>>() {
    override fun onActive() {
      super.onActive()
      log.error("onactive refreshing data $stopCode")
      refreshData()
    }

    override fun onInactive() {
      super.onInactive()
      refreshJob?.also {
        log.error("cancelling stop refresh for $stopCode")
        it.cancel("stopDepartures livedata is inactive $stopCode")
        refreshJob = null
      }
    }
  }

  val stopDepartures: LiveData<Pair<Stop, StopDepartures>> = _stopDepartures

  private val _updateInProgress = MutableLiveData(false)
  val updateInProgress: LiveData<Boolean> = _updateInProgress

  override fun onCleared() {
    log.debug("onCleared() $stopCode")
  }

  private var refreshJob: Job? = null

  fun refreshData() {
    refreshJob?.cancel()
    refreshJob = viewModelScope.launch(Dispatchers.IO) {
      try {


        val stop = metlink.getStops()
          .firstOrNull {
            it.code == stopCode
          } ?: throw IllegalArgumentException("Stop $stopCode not found")


        while (true) {
          try {
            _updateInProgress.postValue(true)

            log.warn("getting stop departures for ${stopCode}")

            metlink.getStopDepartures(stopCode).also {
              _stopDepartures.postValue(Pair(stop, it))
            }

          } catch (err: Exception) {
            var msg = err.message
            if (err is SocketTimeoutException) msg = timeoutMessage
            if (err is CancellationException) return@launch
            log.error(err.message, err)
            appModel.errorMessage.postValue(msg)
            delay(TimeUnit.SECONDS.toMillis(5))
          } finally {
            _updateInProgress.postValue(false)
          }

          log.trace("sleeping for $REFRESH_INTERVAL")
          delay(REFRESH_INTERVAL)
        }
      } catch (_: CancellationException) {
        //log.error("WAS CANCELLED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
      } catch (err: Exception) {
        var msg = err.message
        if (err is SocketTimeoutException) msg = timeoutMessage
        log.error(err.message, err)
        appModel.errorMessage.postValue(msg)
        delay(TimeUnit.SECONDS.toMillis(5))
      }
    }.also {
      it.invokeOnCompletion {
        log.error("REFRESH JOB FINISHED FOR $stopCode")
      }
    }
  }


}

class StopDeparturesModelFactory(
  private val fragment: Fragment,
  private val stopCode: String
) : ViewModelProvider.NewInstanceFactory() {

  @Suppress("unchecked_cast")
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    return StopDeparturesModel(fragment.requireContext(), fragment.appModel, stopCode) as T
  }
}


private val log = org.slf4j.LoggerFactory.getLogger(StopDeparturesModel::class.java)