package danbroid.busapp.db.model;

import android.provider.BaseColumns;

/**
 * Column names used in the SQL database
 */
public interface Tables {

  interface BusStop extends BaseColumns {
    String TABLE_NAME = "t_stop";
    String STOP_CODE = "stop_code";
    String STOP_NAME = "stop_name";
    String STOP_LAT = "stop_lat";
    String STOP_LNG = "stop_lon";
    String ZONE_ID = "zone_id";
    String LOCATION_TYPE = "location_type";
  }

  interface StopAttributes {
    String TABLE_NAME = "t_stop_attributes";
    String STOP_CODE = "stop_code";
    String ACCESS_COUNT = "access_count";
  }

}
