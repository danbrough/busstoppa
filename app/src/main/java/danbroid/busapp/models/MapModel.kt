package danbroid.busapp.models

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import danbroid.busapp.debugToast
import danbroid.busapp.prefs



class MapModel(ctx: Context) : ViewModel(), HasPrefs, LocationListener {

  private val context: Context = ctx.applicationContext

  companion object {
    const val DEFAULT_LAT = -41.29099483152741
    const val DEFAULT_LNG = 174.77808706462383
    const val PERMISSIONS_ID = 19382
    val PERMISSIONS =
      arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
  }

  override val prefs: SharedPreferences
    get() = context.prefs

  private var _mapLatitude by DoublePref(PrefKeys.LOCATION_LAT, DEFAULT_LAT)
  private var _mapLongitude by DoublePref(PrefKeys.LOCATION_LNG, DEFAULT_LNG)
  private var _initialZoom by FloatPref(PrefKeys.INITIAL_ZOOM, 0f)

  var initialMapPosition = Pair(_mapLatitude,_mapLongitude)
  var initialZoom:Float = _initialZoom

  private var gpsEnabledPref by BooleanPref(PrefKeys.GPS_ENABLED, false)

  val currentLocationEnabled = object : MutableLiveData<Boolean>(gpsEnabledPref) {
    override fun setValue(value: Boolean) {
      log.trace("locationEnabled = $value")
      super.setValue(value)
      gpsEnabledPref = value
    }
  }


  private val locationManager: LocationManager
    get() = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

  override fun onLocationChanged(location: Location) {
    _currentLocation.postValue(location)
  }

  private val _currentLocation = object : MutableLiveData<Location>() {
    override fun onActive() {
      context.debugToast("Location Active: locationEnabled.value is ${currentLocationEnabled.value} gpsEnabledPref: $gpsEnabledPref")
      currentLocationEnabled.value = gpsEnabledPref
    }

    override fun onInactive() {
      context.debugToast("Location Inactive")
      stopGPS()
    }
  }

  val currentLocation: LiveData<Location> = _currentLocation

  init {
    log.info("init()")
    currentLocationEnabled.observeForever { enabled ->
      when (enabled) {
        true -> startGPS()
        false -> stopGPS()
      }
    }
  }

  private fun stopGPS() {
    log.warn("stopGPS()")
    locationManager.removeUpdates(this)
  }

  private fun startGPS() {
    log.warn("startGPS()")
    locationManager.removeUpdates(this)

    try {
      val networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

      val gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

      if (gpsLocation != null) {
        log.debug("got last known gps location")
        _currentLocation.value = gpsLocation
      } else if (networkLocation != null) {
        log.debug("got last known network location")
        _currentLocation.value = networkLocation
      }

      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0f, this)
    } catch (err: SecurityException) {
      log.error(err.message, err)
    }
  }


  override fun onCleared() {
    log.warn("onCleared() stopping gps..")
    stopGPS()
    _mapLatitude = initialMapPosition.first
    _mapLongitude =  initialMapPosition.second
    _initialZoom = initialZoom
  }

  override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
  }

}

private class MapModelFactory(val context: Context) : ViewModelProvider.NewInstanceFactory() {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    return MapModel(context) as T
  }
}

fun Fragment.mapModel() =
  ViewModelProvider(this, MapModelFactory(context!!)).get(MapModel::class.java)


private val log = org.slf4j.LoggerFactory.getLogger(MapModel::class.java)
