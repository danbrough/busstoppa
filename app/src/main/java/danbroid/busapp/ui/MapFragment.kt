package danbroid.busapp.ui

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import danbroid.busapp.HelpCodes
import danbroid.busapp.R
import danbroid.busapp.activities.mainActivity
import danbroid.busapp.data.BusStop
import danbroid.busapp.models.MapModel
import danbroid.busapp.models.mapModel
import danbroid.touchprompt.touchPrompt
import kotlinx.coroutines.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


abstract class MapFragment : Fragment() {


  protected lateinit var mapModel: MapModel

  var gpsMenuItem: MenuItem? = null
  private var blinkGpsMenuJob: Job? = null

  var stop: BusStop? = null


  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.map, menu)
    gpsMenuItem = menu.findItem(R.id.action_gps)
  }

  private lateinit
  var gpsIconFixed: Drawable
  private lateinit var gpsIconNotFixed: Drawable
  private lateinit var gpsIconOff: Drawable


  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    log.info("onViewCreated()")


/*    requireActivity().findViewById<FragmentContainerView>(R.id.nav_host_fragment).layoutParams.let {
      if (it is CoordinatorLayout.LayoutParams)
        it.behavior = null
    }
    requireActivity().findViewById<AppBarLayout>(R.id.appbar)?.background = null*/

    setHasOptionsMenu(true)
    mapModel = mapModel()

    gpsIconFixed = ResourcesCompat.getDrawable(resources, R.drawable.ic_gps_fixed, null)!!
    gpsIconNotFixed = ResourcesCompat.getDrawable(resources, R.drawable.ic_gps_not_fixed, null)!!
    gpsIconOff = ResourcesCompat.getDrawable(resources, R.drawable.ic_gps_off, null)!!

    mapModel.currentLocation.observe(viewLifecycleOwner, Observer(::onLocationChanged))

    mapModel.currentLocationEnabled.observe(viewLifecycleOwner, Observer(::onLocationEnabled))


    touchPrompt(HelpCodes.MAP_GPS_BUTTON) {
      primaryTextID = R.string.lbl_gps_button
      secondaryTextID = R.string.msg_gps_button
      targetID = R.id.action_gps
      initialDelay = 500
    }
  }

  override fun onDestroyView() {
    log.info("onDestroyView()")
    super.onDestroyView()

  }


  protected open fun onLocationEnabled(enabled: Boolean) {
    log.debug("onLocationEnabled() $enabled")
    if (enabled) {
      blinkGpsMenuJob?.cancel()
      blinkGpsMenuJob = GlobalScope.launch(Dispatchers.Main) {
        var blink = true
        while (true) {
          gpsMenuItem?.icon =
            if (blink) gpsIconFixed else gpsIconNotFixed
          blink = !blink
          delay(1000L)
        }
      }
    } else {
      blinkGpsMenuJob?.cancel()
      gpsMenuItem?.icon = gpsIconOff
    }

    if (enabled) {

      val locationManager: LocationManager =
        requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

      val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
      log.debug("gps enabled:  ${gpsEnabled}")
      val networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
      log.debug("network provider enabled:  ${networkEnabled}")

      if (!gpsEnabled && !networkEnabled) {
        mapModel.currentLocationEnabled.value = false

        AlertDialog.Builder(requireContext())
          .setIcon(R.drawable.ic_warning)
          .setTitle(R.string.lbl_warning)
          .setMessage(R.string.msg_location_not_enabled)
          .setPositiveButton(
            android.R.string.ok
          ) { dialog, which ->
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
          }
          .setNegativeButton(android.R.string.cancel) { dialog, which ->
            dialog.dismiss()
          }
          .show()
      }
    }
  }


  protected open fun onLocationChanged(location: Location) {
    log.debug("onLocationChanged() $location")
  }

  fun showStopPrompt(stop: BusStop, x: Double, y: Double) {
    touchPrompt(HelpCodes.MAP_STOP_TAP) {
      setTargetPosition(x.toFloat(), y.toFloat())
      primaryTextID = R.string.msg_tap_here_for_departures
    }
  }


  protected fun toggleLocationSearch() {
    log.trace("toggleLocationSearch() mapModel.locationEnabled.value = ${mapModel.currentLocationEnabled.value}")

    if (mapModel.currentLocationEnabled.value == true) {
      mapModel.currentLocationEnabled.value = false
      return
    }


    startGPS()

  }

  @AfterPermissionGranted(MapModel.PERMISSIONS_ID)
  protected fun startGPS() {
    log.warn("startGPS()")
    if (EasyPermissions.hasPermissions(requireContext(), *MapModel.PERMISSIONS)) {
      log.warn("we have permissions")
      mapModel.currentLocationEnabled.value = true
    } else {
      log.warn("requesting permissions")
      EasyPermissions.requestPermissions(
        this,
        requireContext().getString(R.string.msg_permissions_required),
        MapModel.PERMISSIONS_ID,
        *MapModel.PERMISSIONS
      )
    }
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
  }


  override fun onOptionsItemSelected(item: MenuItem): Boolean =
    when (item.itemId) {
      R.id.action_gps -> {
        toggleLocationSearch()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }

  override fun onResume() {
    super.onResume()
    log.trace("onResume()")
    requireActivity().title = ""
  }

  override fun onPause() {
    blinkGpsMenuJob?.cancel().also {
      blinkGpsMenuJob = null
    }
    super.onPause()
  }

  protected fun onStopSelected(stopCode: String) =
    mainActivity.addToHistoryAndShowDepartures(stopCode)


}

private val log = org.slf4j.LoggerFactory.getLogger(MapFragment::class.java)


