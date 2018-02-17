package danbroid.busapp.interfaces;

import danbroid.busapp.db.model.BusStop;

public interface MapView  {

  String PREF_LAT = MapView.class.getName() + ":lat";
  String PREF_LNG = MapView.class.getName() + ":lng";
  String PREF_ZOOM = MapView.class.getName() + ":zoom";
  String PREF_GPS_STATE = MapView.class.getName() + ":gpsState";

  void showStop(BusStop stop, boolean zoomIn);

}
