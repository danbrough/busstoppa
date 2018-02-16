package danbroid.busapp.db.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/*

CREATE TABLE t_stop (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    stop_code TEXT NOT NULL UNIQUE,
    stop_name TEXT NOT NULL,
    stop_lat REAL NOT NULL,
    stop_lon REAL NOT NULL,
    zone_id TEXT NOT NULL DEFAULT '',
    location_type INTEGER NOT NULL DEFAULT 0
);



);*/
public class BusStop implements Serializable {


  public static final String REGION_WELLINGTON = "WN_";
  public static final String REGION_CHRISTCHURCH = "CH_";

  public BusStop() {
  }

  public BusStop(Cursor cursor) {
    int n = 0;
    id = cursor.getLong(n++);
    stopCode = cursor.getString(n++);
    stopName = cursor.getString(n++);
    lat = cursor.getDouble(n++);
    lng = cursor.getDouble(n++);
    zoneId = cursor.getString(n++);
    locationType = cursor.getInt(n++);
  }


  @Expose(serialize = false)
  long id;


  @SerializedName("Sms")
  private String stopCode;

  @SerializedName("Name")
  private String stopName;

  @SerializedName("Lat")
  private double lat;

  @SerializedName("Long")
  private double lng;

  @SerializedName("Farezone")
  private String zoneId = "";


  private int locationType = 0;


  @Override
  public String toString() {
    return "BusStop[" + id + ":" + stopCode + ":" + stopName + "]";
  }

  public long getId() {
    return id;
  }


  public void setId(long id) {
    this.id = id;
  }

  public String getStopCode() {
    return stopCode;
  }

  public String getShortStopCode() {
    return stopCode.substring(3);
  }

  public void setStopCode(String stopCode) {
    this.stopCode = stopCode;
  }

  public String getStopName() {
    return stopName;
  }

  public void setStopName(String stopName) {
    this.stopName = stopName;
  }

  public double getLat() {
    return lat;
  }

  public void setLat(double lat) {
    this.lat = lat;
  }

  public double getLng() {
    return lng;
  }

  public void setLng(double lng) {
    this.lng = lng;
  }

  public LatLng getLocation() {
    return new LatLng(lat, lng);
  }

  public void setLocationType(int locationType) {
    this.locationType = locationType;
  }

  public int getLocationType() {
    return locationType;
  }

  public String getZoneId() {
    return zoneId;
  }

  public void setZoneId(String zoneId) {
    this.zoneId = zoneId;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null)
      return false;
    return ((BusStop) o).id == this.id;
  }

  public ContentValues getValues() {
    ContentValues values = new ContentValues();
    values.put(Tables.BusStop.STOP_CODE, stopCode);
    values.put(Tables.BusStop.STOP_NAME, stopName);
    values.put(Tables.BusStop.STOP_LAT, lat);
    values.put(Tables.BusStop.STOP_LNG, lng);
    values.put(Tables.BusStop.LOCATION_TYPE, locationType);
    values.put(Tables.BusStop.ZONE_ID, zoneId);
    values.put(Tables.BusStop._ID, id);
    return values;
  }


}

