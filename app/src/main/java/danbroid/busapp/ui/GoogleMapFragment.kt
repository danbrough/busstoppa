package danbroid.busapp.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.widget.RelativeLayout
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.appbar.AppBarLayout
import danbroid.busapp.R
import danbroid.busapp.data.BusStop
import danbroid.busapp.data.toBusStop
import danbroid.busapp.metlink
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class GoogleMapFragment : MapFragment(), OnMapReadyCallback {
  private var map: GoogleMap? = null

  //private var marker: Marker? = null

  private val markers = mutableSetOf<Marker>()

  private fun createMarker(stop: BusStop, showInfoWindow: Boolean = false) {
    //log.trace("createMarker() :{}", stop)
    val map = map ?: return

    map.addMarker(
        MarkerOptions()
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_marker_png))
            .position(stop.latLng)
            .anchor(0.5f, 0.5f)
            //.alpha(0.7f)
            .title(stop.name)
            .visible(true)
            .snippet("code: " + stop.code)
    ).also {
      markers.add(it)
      it.tag = stop
      if (showInfoWindow) {
        it.showInfoWindow()
        it.isVisible = true
      }
    }
  }

  @SuppressLint("MissingPermission")
  override fun onMapReady(map: GoogleMap) {
    log.debug("onMapReady() $map")
    this.map = map
    map.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.mapstyle))




    val locationButton =
        (childFragmentManager.findFragmentById(R.id.gmap)?.view?.findViewById<View>(Integer.parseInt("1"))?.parent as? ViewGroup)?.findViewById<View>(
            Integer.parseInt("2")
        )

    (locationButton?.layoutParams as? RelativeLayout.LayoutParams)?.apply {
      addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
      addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)
      setMargins(0, 180, 30, 0)
    }


    map.isIndoorEnabled = false
    map.isTrafficEnabled = true

    map.isMyLocationEnabled = mapModel.currentLocationEnabled.value ?: false
    map.isBuildingsEnabled = true

    with(map.uiSettings) {
      isMapToolbarEnabled = false
      isCompassEnabled = true
      isZoomControlsEnabled = true
    }

    map.moveCamera(
        CameraUpdateFactory.newLatLngZoom(mapModel.initialMapPosition.latLng,
            mapModel.initialZoom.let {
              if (it == 0f) 16f else it
            })
    )


    /*  map.setOnMapClickListener {
        log.trace("map clicked: ${it.latitude},${it.longitude}")
        searchNearby(it.latitude, it.longitude)
      }*/

    map.setOnInfoWindowClickListener { marker ->
      //model.showDepartures((marker.tag as BusStop))
      onStopSelected((marker.tag as BusStop).code)
    }

    lifecycleScope.launch(Dispatchers.IO) {
      try {
        requireContext().metlink.getStops().map { it.toBusStop() }.let {stopList ->
        //metlink.stopList().execute().body()?.let { stopList ->
          withContext(Dispatchers.Main) {
            stopList.forEach { stop ->
              createMarker(stop)
            }

            showOrHideStops()
            map.setOnCameraIdleListener {
              log.debug("zoom level ${map.cameraPosition.zoom}")
              showOrHideStops()
            }
            stop?.also {
              showStop(it, true)
            }
          }
        }
      } catch (err: Exception) {
        log.error(err.message, err)
      }
    }


  }

  var stopsVisible = false

  fun showOrHideStops() {
    /*     val pannedOut = map!!.cameraPosition.zoom < 13f
         log.error("showOrHideStops() stopsVisible: $stopsVisible : pannedOut: ${pannedOut}")
         if (pannedOut == stopsVisible) {
             stopsVisible = !stopsVisible
             log.error("settings stops visible to $stopsVisible marker count: ${markers.size}")
             markers.forEach {
                 it.isVisible = stopsVisible
             }
         }*/
  }

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ) = inflater.inflate(R.layout.google_map_fragment, container, false)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    (childFragmentManager.findFragmentById(R.id.gmap) as SupportMapFragment).getMapAsync(this)
  }

  fun showStop(stop: BusStop, zoomIn: Boolean) {
    log.trace("showStop() $stop zoomIn: $zoomIn")
    val map = map ?: return
    val cameraUpdate: CameraUpdate
/*    val cameraCallback = object : GoogleMap.CancelableCallback {
      override fun onFinish() {
        createMarker(stop, showInfoWindow = true)
*//*
TODO
        object : TouchPrompt(activity) {
          override fun onBeforeShow(): Boolean {
            log.debug("onBeforeShow()")
            setPrimaryText(stop.name)
            setSecondaryText("You can click on this to display its departure information")
            val location = intArrayOf(0, 0)
            view?.findViewById<View>(android.R.id.content)
              ?.getLocationOnScreen(location)

            val p = map.projection.toScreenLocation(stop.latLng)
            p.y += location[1] - 60
            setTarget(p.x.toFloat(), p.y.toFloat())
            return true
          }
        }.setSingleShotID(HELP_MAP_MARKER)
          .show()*//*

      }

      override fun onCancel() {}
    }*/
/*
    if (zoomIn) {
      var zoom = map.cameraPosition.zoom
      if (zoom < 17) zoom = 17f
      cameraUpdate = CameraUpdateFactory.newLatLngZoom(stop.latLng, zoom)
      map.animateCamera(cameraUpdate, cameraCallback)
    } else {
      cameraUpdate = CameraUpdateFactory.newLatLng(stop.latLng)
      map.moveCamera(cameraUpdate)
      createMarker(stop, showInfoWindow = true)
    }*/
    var zoom = map.cameraPosition.zoom
    if (zoom < 17) zoom = 17f
    cameraUpdate = CameraUpdateFactory.newLatLngZoom(stop.latLng, zoom)
    map.moveCamera(cameraUpdate)
    log.trace("searching ${markers.size} for ${stop.code}")
    markers.firstOrNull { (it.tag as BusStop).code == stop.code }?.apply {
      log.warn("showing info window")
      isVisible = true
      showInfoWindow()
    }
  }



  /*
  if (isMap) {
      toolbarAnim = binding.appbar.animate().run {
        duration = 500
        alpha(0.5f)
        start()
        this
      }
    } else {
      //appbar.alpha = 1f
      toolbarAnim = binding.appbar.animate().run {
        duration = 500
        alpha(1f)
        start()
        this
      }
    }
   */


/*
  private var animation:ViewPropertyAnimator? = null

  override fun onStart() {
    super.onStart()
     animation = requireActivity().findViewById<View>(R.id.appbar)?.animate()?.apply {
      duration = 500
      alpha(0.5f)
      start()
    }
  }
*/

  override fun onStop() {
    super.onStop()
    //requireActivity().findViewById<View>(R.id.appbar)?.alpha = 1f
  }
  override fun onPause() {
    super.onPause()
    map?.cameraPosition?.also {
      mapModel.initialZoom = it.zoom
      mapModel.initialMapPosition = Pair(it.target.latitude, it.target.longitude)
    }
  }


  @SuppressLint("MissingPermission")
  override fun onLocationEnabled(enabled: Boolean) {
    super.onLocationEnabled(enabled)
    map?.isMyLocationEnabled = enabled
  }

/*




  private var currentPosCircle: Circle? = null

  override fun onMapReady(map: GoogleMap) {
    log.debug("onMapReady()")
    this.map = map

    map.isIndoorEnabled = false
    map.isTrafficEnabled = true

    with(map.uiSettings) {
      isMapToolbarEnabled = false
      isCompassEnabled = true
      isZoomControlsEnabled = true
    }

    log.debug("centerPos: $centerPos")


    map.setOnCameraIdleListener {
      log.debug("zoom level ${map.cameraPosition.zoom}")
    }

    map.moveCamera(CameraUpdateFactory.newLatLngZoom(centerPos, zoom))

    map.setOnMapClickListener {
      log.trace("map clicked: ${it.latitude},${it.longitude}")
      searchNearby(it.latitude, it.longitude)
    }

    map.setOnInfoWindowClickListener { marker ->
      model.showDepartures((marker.tag as BusStop))
    }

    currentPosCircle = map.addCircle(CircleOptions().apply {
      center(centerPos)   //set center
      radius(100.0)   //set radius in meters
      fillColor(0x550000FF)
      strokeWidth(0f)
      strokeColor(Color.TRANSPARENT)
      visible(false)
    })


    displayStop?.let {
      showStop(it)
    } ?: mapModel.location.observe(this, Observer { location ->
      log.error("location $location !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")


      if (marker == null) {
        searchNearby(location.latLng.latitude, location.latLng.longitude)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(location.latLng, maxOf(18f, map.cameraPosition.zoom)))
      }

      currentPosCircle!!.apply {
        center = location.latLng
        radius = location.accuracy.toDouble().let {
          if (it < 1.0) 1.0 else it
        }
        isVisible = true
      }
    })

  }



*/


}


val Pair<Double, Double>.latLng
  get() = LatLng(first, second)

val BusStop.latLng: LatLng
  get() = LatLng(this.lat, this.lng)

val Location.latLng
  get() = LatLng(this.latitude, this.longitude)

private val log = org.slf4j.LoggerFactory.getLogger(GoogleMapFragment::class.java)

fun Activity.createMapFragment() = GoogleMapFragment()