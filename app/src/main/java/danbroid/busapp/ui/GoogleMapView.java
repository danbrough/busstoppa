package danbroid.busapp.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.UiThread;

import java.util.List;

import danbroid.busapp.BusApp;
import danbroid.busapp.R;
import danbroid.busapp.db.BusStopDB;
import danbroid.busapp.db.model.BusStop;
import danbroid.busapp.util.HelpCodes;
import danbroid.busapp.interfaces.MainView;
import danbroid.busapp.interfaces.MapView;
import danbroid.touchprompt.TouchPrompt;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

@EFragment
@OptionsMenu(R.menu.map)
public class GoogleMapView extends SupportMapFragment
    implements OnInfoWindowClickListener, OnMapClickListener, MapView, OnMapReadyCallback, EasyPermissions.PermissionCallbacks, GoogleMap.OnMyLocationChangeListener {
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GoogleMapView
      .class);
  private static final int RC_LOCATION_REQUEST = 131;
  //private static final int RC_SETTINGS_SCREEN = 132;


  private GoogleMap map;

  GpsState getGpsState() {
    return GpsState.valueOf(getPrefs().getString(PREF_GPS_STATE, GpsState.DISABLED.name()));
  }

  @Bean
  BusStopDB gtfsDao;

  @OptionsMenuItem(R.id.menu_nearest_stop)
  MenuItem nearestStopMenu;

  private static final int MSG_FLASH_GPS = 1;

  private boolean flashToggle = false;

  private final Handler handler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case MSG_FLASH_GPS:
          if (nearestStopMenu != null) {
            nearestStopMenu.setIcon(getResources().getDrawable(flashToggle ? R.drawable.ic_gps_fixed : R.drawable.ic_gps_not_fixed));
            flashToggle = !flashToggle;
          }
          sendEmptyMessageDelayed(MSG_FLASH_GPS, 1000);
          break;
      }
    }
  };

  void setGpsState(GpsState state) {
    handler.removeMessages(MSG_FLASH_GPS);

    String msg = null;
    switch (state) {
      case DISABLED:
        break;
      case SEARCHING:
        flashToggle = true;
        handler.sendEmptyMessageDelayed(MSG_FLASH_GPS, 1000);
        Toast.makeText(getContext(), "Searching for the nearest stop..", Toast.LENGTH_SHORT).show();
        break;
      case FOUND:
        break;
    }

    if (nearestStopMenu != null) {
      int icon = 0;
      switch (state) {
        case DISABLED:
          icon = R.drawable.ic_gps_off;
          break;
        case SEARCHING:
          icon = R.drawable.ic_gps_not_fixed;
          break;
        case FOUND:
          icon = R.drawable.ic_gps_fixed;
          break;
      }
      nearestStopMenu.setIcon(getResources().getDrawable(icon));
    }
    getPrefs().edit().putString(PREF_GPS_STATE, state.name()).apply();
  }


  enum GpsState {
    DISABLED, SEARCHING, FOUND;
  }


  private Marker marker;
  private BusStop stop;

  @App
  BusApp app;


  public static GoogleMapView getInstance() {
    return GoogleMapView_.builder().build();
  }


  @SuppressLint("ResourceType")
  @AfterViews
  void init() {
    log.debug("init()");
    getMapAsync(this);
    setGpsState(GpsState.DISABLED);
  }

  SharedPreferences getPrefs() {
    return getContext().getSharedPreferences("map", Context.MODE_PRIVATE);
  }


  @Override
  public void onMapReady(final GoogleMap map) {
    if (!isResumed()) return;
    log.info("onMapReady()");
    this.map = map;


    UiSettings uiSettings = map.getUiSettings();
    uiSettings.setMapToolbarEnabled(false);
    uiSettings.setCompassEnabled(true);
    uiSettings.setZoomControlsEnabled(true);

    SharedPreferences prefs = getPrefs();

    double lat = Double.parseDouble(prefs.getString(PREF_LAT, "-41.29099483152741"));
    double lng = Double.parseDouble(prefs.getString(PREF_LNG, "174.77808706462383"));
    float zoom = prefs.getFloat(PREF_ZOOM, 16);

    LatLng location = new LatLng(lat, lng);
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoom));


    map.setOnMapClickListener(this);

    map.setIndoorEnabled(false);
    map.setTrafficEnabled(true);
    map.setOnInfoWindowClickListener(this);
    map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
      @Override
      public void onCameraIdle() {
      /*TODO   GoogleMapView.this.zoom = map.getCameraPosition().zoom;
	      log.error("ZOOM: " + zoom);*/
      }
    });


    stop = app.getCurrentStop();

    if (stop != null) {
      showStop(stop, false);
    }


  }

  @Override
  @Background
  public void onMapClick(LatLng location) {
    log.trace("onMapClick: {}", location);

    BusStop nearestStop = gtfsDao.getNearestStop(location.latitude, location.longitude, null);
    if (nearestStop != null)
      showStop(nearestStop, true);

  }

  private Marker createMarker(BusStop stop) {
    log.trace("createMarker() :{}", stop);
    if (map == null)
      return null;

    Marker marker = map.addMarker(new MarkerOptions().position(stop.getLocation()).title(stop
        .getStopName())
        .snippet("code: " + stop.getShortStopCode())
        .icon(BitmapDescriptorFactory.fromResource(danbroid.busapp.R.drawable.blank)));
    marker.showInfoWindow();
    return marker;
  }


  @Override
  public void onInfoWindowClick(Marker marker) {
    if (stop != null)
      ((MainView) getActivity()).selectStop(stop);
  }


  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    MenuItem item = menu.findItem(R.id.menu_nearest_stop);

    GpsState state = getGpsState();
    switch (state) {
      case DISABLED:
        item.setIcon(getResources().getDrawable(R.drawable.ic_gps_off));
        break;
      case SEARCHING:
        item.setIcon(getResources().getDrawable(R.drawable.ic_gps_not_fixed));
        break;
      case FOUND:
        item.setIcon(getResources().getDrawable(R.drawable.ic_gps_fixed));
        break;
    }
  }


  @OptionsItem(R.id.menu_nearest_stop)
  void onNearestStopClicked() {
    log.debug("onNearestStopClicked()");
    switch (getGpsState()) {
      case DISABLED:
        log.debug("state: disabled");
        enableGPS();
        break;
      case SEARCHING:
      case FOUND:
        disableGPS();
        break;
    }
  }


  @AfterPermissionGranted(RC_LOCATION_REQUEST)
  void enableGPS() {
    log.info("enableGPS()");
    String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
    if (EasyPermissions.hasPermissions(getContext(), perms)) {
      // Already have permission, do the thing
      log.debug("we have permission");
      setGpsState(GpsState.SEARCHING);

      showStop(null, false);

      if (map != null) {
        try {
          log.debug("map.setMyLocationEnabled(true);");
          map.setMyLocationEnabled(true);
          map.getUiSettings().setMyLocationButtonEnabled(false);
          map.setOnMyLocationChangeListener(this);

        } catch (SecurityException e) {
          log.error(e.getMessage(), e);
        }
      }

      // ...
    } else {
      // Do not have permissions, request them now
      log.debug("requesting permission ..");
      EasyPermissions.requestPermissions(this, getString(R.string.msg_require_location_permission),
          RC_LOCATION_REQUEST, perms);
    }
  }


  void disableGPS() {
    log.debug("disableGPS()");
    Toast.makeText(getContext(), "GPS disabled", Toast.LENGTH_SHORT).show();
    setGpsState(GpsState.DISABLED);
    if (map != null) {
      try {
        map.setMyLocationEnabled(false);
      } catch (SecurityException e) {
        log.error(e.getMessage(), e);
      }
    }
  }


  @Override
  public void onMyLocationChange(Location location) {
    log.trace("onMyLocationChange(): {}", location);
    GpsState state = getGpsState();
    log.trace("state: {} currentStop: {}", state, stop);

    if (state == GpsState.SEARCHING) {


      BusStop nearestStop = gtfsDao.getNearestStop(location.getLatitude(), location.getLongitude(), null);
      if (nearestStop != null && !nearestStop.equals(stop)) {
        showStop(nearestStop, true);
        setGpsState(GpsState.FOUND);
      }
    }

  }

  @Override
  public void onStart() {
    log.debug("onStart()");
    super.onStart();
    getActivity().setTitle("");
    SharedPreferences prefs = getPrefs();

    new TouchPrompt(getActivity())
        .setSingleShotID(HelpCodes.MAP_LOCATION_BUTTON)
        .setShortDelay()
        .setPrimaryText("GPS Search Button")
        .setSecondaryText("Click here to enable/disable GPS discovery of your nearest stop")
        .setTarget(R.id.menu_nearest_stop)
        .show();
  }

/*  @Override
  public void onResume() {
    log.debug("onResume()");
    super.onResume();
    if (map == null) {
      getMapAsync(this);
    }

  }*/


  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull
      int[] grantResults) {
    EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
  }


  @Override
  public void onPermissionsGranted(int requestCode, List<String> perms) {
    log.trace("onPermissionsGranted() requestCode: " + requestCode + " count: " + perms.size());
    for (String permission : perms) {
      if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
        log.trace("ACCESS_FINE_LOCATION granted");
      }
    }

  }


  @Override
  public void onPermissionsDenied(int requestCode, List<String> perms) {
    log.trace("onPermissionsDenied() requestCode: " + requestCode + " count: " + perms.size());

    if (EasyPermissions.permissionPermanentlyDenied(this, Manifest.permission
        .ACCESS_FINE_LOCATION)) {
      log.trace("ACCESS_FINE_LOCATION permission permanently denied");
    }
  }

/*  protected void setupPermissions() {
    log.trace("setupPermissions()");

    if (getPrefs().getBoolean(PREF_REQUEST_PERMISSIONS, true)) {
      log.trace("not requesting permissions");
      return;
    }

    String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
    if (EasyPermissions.hasPermissions(getContext(), perms)) {
      onLocationAccessGranted();
    } else {
      log.trace("requesting permissions");
      EasyPermissions.requestPermissions(this, getString(R.string.msg_require_location_permission),
          RC_LOCATION_REQUEST, perms);
    }
  }*/

  @Override
  public void onPause() {
    super.onPause();
    handler.removeMessages(MSG_FLASH_GPS);
  }

  @Override
  public void onStop() {
    log.debug("onStop();");
    super.onStop();

    if (marker != null) {
      marker.remove();
      marker = null;
    }

    //locationTracker.stop();

    if (map != null) {
      CameraPosition cameraPosition = map.getCameraPosition();
      SharedPreferences prefs = getPrefs();
      prefs.edit()
          .putString(PREF_LAT, String.valueOf(cameraPosition.target.latitude))
          .putString(PREF_LNG, String.valueOf(cameraPosition.target.longitude))
          .putFloat(PREF_ZOOM, cameraPosition.zoom)
          .apply();
    }


    TouchPrompt.closeChain();
  }

  @Override
  @UiThread
  public void showStop(final BusStop stop, boolean zoomIn) {
    log.warn("showStop() :{}", stop);
    this.stop = stop;
    if (marker != null)
      marker.remove();
    if (stop == null) return;

    if (map == null) {
      log.trace("map not ready");
      return;
    }


    CameraUpdate cameraUpdate = null;
    if (zoomIn) {
      float zoom = map.getCameraPosition().zoom;
      if (zoom < 16) zoom = 16;
      cameraUpdate = CameraUpdateFactory.newLatLngZoom(stop.getLocation(), zoom);
    } else {
      cameraUpdate = CameraUpdateFactory.newLatLng(stop.getLocation());
    }


    map.animateCamera(cameraUpdate, new GoogleMap.CancelableCallback() {
      @Override
      public void onFinish() {
        if (!isResumed()) return;
        marker = createMarker(stop);

        new TouchPrompt(getActivity()) {
          @Override
          protected boolean onBeforeShow() {
            log.debug("onBeforeShow()");
            if (stop == null || map == null) {
              log.warn("map : {} stop: {}", map, stop);
              return false;
            }
            setPrimaryText("Stop: " + stop.getShortStopCode());
            setSecondaryText("You can click on this to display its departure information");
            int location[] = {0, 0};
            getView().getLocationOnScreen(location);
            Point p = map.getProjection().toScreenLocation(stop.getLocation());
            p.y += location[1] - 60;
            setTarget(p.x, p.y);
            return true;
          }
        }
            .setSingleShotID(HelpCodes.MAP_MARKER_HELP)
            .show();

      }

      @Override
      public void onCancel() {
      }
    });


  }


}
