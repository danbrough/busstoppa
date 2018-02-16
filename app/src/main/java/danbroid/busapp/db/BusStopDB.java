package danbroid.busapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import danbroid.busapp.db.model.BusStop;
import danbroid.busapp.db.model.Tables;

@EBean(scope = EBean.Scope.Singleton)
public class BusStopDB {
  private static final org.slf4j.Logger log = LoggerFactory.getLogger(BusStopDB.class);

  public static final String DB_FILE = "busapp.db";
  public static final int DB_VERSION = 1;
  private static final String SQL_ASSET = "busapp.sql";
  private static final String WGTN_STOPS_JSON = "wgtn_stops.json";

  @RootContext
  Context context;


  private SQLiteOpenHelper dbHelper;


  @AfterInject
  protected void init() {
    log.warn("init()");

    dbHelper = new SQLiteOpenHelper(context, DB_FILE, null, DB_VERSION) {
      @Override
      public void onCreate(SQLiteDatabase db) {
        BusStopDB.this.onCreate(db);
      }

      @Override
      public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        performUpgrade(db, oldVersion, newVersion);
      }
    };

    upgradeOldDB(dbHelper.getWritableDatabase());
  }

  /**
   * Migrate favourite stops from the old database if it exists
   * and then delete it.
   *
   * @param newDB
   */
  private void upgradeOldDB(final SQLiteDatabase newDB) {
    final String oldDB = "gtfs.mp3";

    if (!context.getDatabasePath(oldDB).exists()) {
      return;
    }

    new SQLiteOpenHelper(context, oldDB, null, 99) {
      @Override
      public void onCreate(SQLiteDatabase db) {
        BusStopDB.this.onCreate(db);
      }

      @Override
      public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        log.warn("upgrading old database ..");
        Cursor c = db.rawQuery("select stop_id,access_count from t_stop where access_count != 0", null);
        while (c.moveToNext()) {
          String stopCode = "WN_" + c.getString(0);
          int accessCount = c.getInt(1);
          log.debug("importing favourite stop: {} access_count: {}", stopCode, accessCount);
          newDB.execSQL("INSERT OR REPLACE INTO " + Tables.StopAttributes.TABLE_NAME + "("
              + Tables.StopAttributes.STOP_CODE + "," + Tables.StopAttributes.ACCESS_COUNT +
              ") VALUES (?,?)", new Object[]{stopCode, accessCount});
        }
        c.close();

      }
    }.getReadableDatabase();

    try {
      context.getDatabasePath(oldDB).delete();
    } catch (Exception e) {
    }
    try {
      context.getDatabasePath(oldDB + "-journal").delete();
    } catch (Exception e) {
    }
  }

  private void performUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    log.warn("peformUpgrade(): {} -> {}", oldVersion, newVersion);

  }

  protected void onCreate(SQLiteDatabase db) {
    log.warn("onCreate()");
    try {
      importSQL(SQL_ASSET, db);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
  }

  public void importSQL(String asset, SQLiteDatabase db) throws SQLException, IOException {
    log.info("importAsset(): {}", asset);
    importSQL(context.getAssets().open(asset), db);
    importStops(db);
  }

  public void importStops(SQLiteDatabase db) throws SQLException, IOException {
    log.debug("importStops()");

    long time = System.currentTimeMillis();
    InputStream input = context.getAssets().open(WGTN_STOPS_JSON);
    Gson gson = new GsonBuilder().create();
    JsonReader reader = new JsonReader(new InputStreamReader(input, "UTF-8"));
    reader.beginObject();
    reader.nextName();
    String lastModified = reader.nextString();
    log.trace("lastModified: {}", lastModified);
    reader.nextName();
    reader.beginArray();

    int count = 0;

    while (reader.hasNext()) {
      count++;
      BusStop stop = gson.fromJson(reader, BusStop.class);
      //add the wellington stop_code prefix to avoid clashes with other (potential) regions
      stop.setStopCode(BusStop.REGION_WELLINGTON + stop.getStopCode());
      importStop(db, stop);
    }

    reader.endArray();
    reader.close();

    log.info("imported {} initial bus stops in {}", count, System.currentTimeMillis() - time);

  }

  public long importStop(SQLiteDatabase db, BusStop stop) {
    long ret = 0;
    try {
      db.beginTransaction();
      ContentValues values = stop.getValues();
      values.remove(Tables.BusStop._ID);
      ret = db.insert(Tables.BusStop.TABLE_NAME, null, values);
      db.setTransactionSuccessful();
    } finally {
      db.endTransaction();
    }

    return ret;
  }


  /**
   * Update an existing (or insert new) stop in the database
   *
   * @param db
   * @param stop
   * @return the id of the new or updated stop
   */
  public void updateStop(BusStop stop) {
    SQLiteDatabase db = getWritableDatabase();

    try {
      db.beginTransaction();
      String sql = "SELECT " + Tables.BusStop._ID + " FROM " + Tables.BusStop.TABLE_NAME
          + " WHERE " + Tables.BusStop.STOP_CODE + " = '" + stop.getStopCode() + "'";

      //Cursor c = db.rawQuery(sql, new String[]{stop.getStopCode()});*/
      log.trace("running query: {}", sql);
      Cursor c = db.rawQuery(sql, null);
      log.trace("c.count = " + c.getCount());

      ContentValues values = stop.getValues();
      long id = 0;
      if (c.moveToNext()) {
        id = c.getLong(0);
        log.trace("updating existing stop: {} code: " + stop.getStopCode(), id);
        values.put(Tables.BusStop._ID, id);
        db.updateWithOnConflict(Tables.BusStop.TABLE_NAME, values, null, null, SQLiteDatabase.CONFLICT_REPLACE);
        c.close();
      } else {
        log.error("FAILED TO FIND STOP: " + stop.getStopCode());
        values.remove(Tables.BusStop._ID);
        id = db.insert(Tables.BusStop.TABLE_NAME, null, values);
        log.trace("inserted new row: {} stop_code: {}", id, stop.getStopCode());
        stop.setId(id);
        c.close();
        c = db.rawQuery(sql, null);
        if (c.moveToNext()) {
          log.debug("found new row: {}", stop.getStopCode());
        } else {
          log.debug("still cant find it");
        }
        c.close();

      }
      db.setTransactionSuccessful();
    } finally {
      db.endTransaction();
    }
  }

  public void importSQL(InputStream input, SQLiteDatabase db) throws SQLException, IOException {
    byte buf[] = new byte[1024];
    int c = 0;
    StringWriter sw = new StringWriter();
    while ((c = input.read(buf)) != -1) {
      sw.write(new String(buf, 0, c, "UTF-8"));
    }
    input.close();
    String content = sw.toString();
    String stmts[] = content.split(";");

    for (String stmt : stmts) {
      stmt = stmt.trim();
      if (stmt.isEmpty() || stmt.charAt(0) == '#') continue;
      execSQL(db, stmt);
    }

  }

  protected void execSQL(SQLiteDatabase db, String stmt) {
    log.trace("execSQL(): [{}]", stmt);
    db.execSQL(stmt);
  }

  public BusStop getNearestStop(LatLng location, float distanceToStop[]) throws SQLException {
    return getNearestStop(location.latitude, location.longitude, distanceToStop);
  }

  /**
   * Badly implemented method for finding the nearest BusStop for the given latitude,longitude.
   *
   * @param latitude
   * @param longitude
   * @param distanceToStop The distance (in metres) from the specified lat/lng to this stop will
   *                       be put in the first element of this array
   * @return The nearest BusStop
   * @throws SQLException
   */

  public BusStop getNearestStop(double latitude, double longitude, float distanceToStop[]) throws
      SQLException {
    log.debug("getNearestStop() {}:{}", latitude, longitude);
    Cursor c = dbHelper.getReadableDatabase().query(Tables.BusStop.TABLE_NAME,
        new String[]{Tables.BusStop._ID, Tables.BusStop.STOP_LAT, Tables.BusStop.STOP_LNG}, null,
        null, null, null, null, null);
    float shortestDistance = 0f;
    BusStop stop = null;
    float[] distance = new float[]{0};
    long busID = -1;

    if (distanceToStop != null && distanceToStop.length > 0)
      distanceToStop[0] = -1f;

    while (c.moveToNext()) {
      long id = c.getLong(0);
      double lat = c.getDouble(1);
      double lng = c.getDouble(2);

      Location.distanceBetween(lat, lng, latitude, longitude, distance);
      if (shortestDistance == 0f || distance[0] < shortestDistance) {
        shortestDistance = distance[0];
        busID = id;
      }
    }
    c.close();

    if (busID != 0)
      stop = getBusStop(busID);

    if (stop != null) {
      log.trace("closest stop: " + stop + " distance: " + distance[0]);
      if (distanceToStop != null && distanceToStop.length > 0)
        distanceToStop[0] = distance[0];
    }
    return stop;
  }

  public BusStop getBusStop(long id) {
    log.debug("getBusStop(): " + id);

    SQLiteDatabase db = dbHelper.getReadableDatabase();

    Cursor c = db.query(Tables.BusStop.TABLE_NAME, null, Tables.BusStop
            ._ID + "= ?", new String[]{"" + id},
        null, null, null, null);
    BusStop stop = null;
    if (c.moveToFirst()) {
      stop = new BusStop(c);
    }
    c.close();


    return stop;
  }


  public BusStop getBusStop(String code) {
    SQLiteDatabase db = null;
    db = dbHelper.getReadableDatabase();

    Cursor c = db.query(Tables.BusStop.TABLE_NAME, null, Tables.BusStop
            .STOP_CODE + "= ?", new String[]{"" + code},
        null, null, null, null);
    BusStop stop = null;
    if (c.moveToFirst()) {
      stop = new BusStop(c);
    }
    c.close();

    return stop;
  }


  public Cursor query(String table, String[] columns, String selection, String[] selectionArgs,
                      String orderBy,
                      String limit) throws SQLException {
    return query(table, columns, selection, selectionArgs, null, null, orderBy, limit);
  }

  public Cursor query(String table, String[] columns, String selection, String[] selectionArgs,
                      String groupBy,
                      String having, String orderBy, String limit) throws SQLException {
    return dbHelper.getReadableDatabase().query(table, columns, selection, selectionArgs,
        groupBy, having, orderBy,
        limit);
  }


  public Cursor getRecentStops(int limit) {

    String sql = "SELECT * FROM " + Tables.BusStop.TABLE_NAME
        + "," + Tables.StopAttributes.TABLE_NAME + " WHERE " + Tables.BusStop.TABLE_NAME + "." +
        Tables.BusStop.STOP_CODE + " = " + Tables.StopAttributes.TABLE_NAME + "." +
        Tables.StopAttributes.STOP_CODE + " ORDER BY " + Tables.StopAttributes.ACCESS_COUNT +
        " DESC LIMIT " + limit;

    return dbHelper.getReadableDatabase().rawQuery(sql, null);


  }


  public void close() {
    dbHelper.close();
  }


  public void removeFromFavourites(long id) {
    removeFromFavourites(getBusStop(id));
  }

  @Background(serial = "db")
  public void removeFromFavourites(BusStop stop) {
    log.trace("removeFromFavourites(): {}", stop);
    dbHelper.getWritableDatabase().delete(Tables.StopAttributes.TABLE_NAME,
        Tables.StopAttributes.STOP_CODE + " = ?", new String[]{stop.getStopCode()});
    context.getContentResolver().notifyChange(BusStopContentProvider.URI_RECENT_STOPS, null);
  }

  public SQLiteDatabase getWritableDatabase() {
    return dbHelper.getWritableDatabase();
  }

  public SQLiteDatabase getReadableDatabase() {
    return dbHelper.getReadableDatabase();
  }

  @Background(serial = "db")
  public void incrementAccessCount(BusStop stop) {
    log.trace("incrementAccessCount(): {}", stop);
    Cursor c = getReadableDatabase().rawQuery("SELECT " + Tables.StopAttributes.ACCESS_COUNT +
        " FROM " + Tables.StopAttributes.TABLE_NAME + " WHERE " + Tables.StopAttributes.STOP_CODE +
        " = ?", new String[]{stop.getStopCode()});
    int access_count = 0;
    if (c.moveToFirst()) {
      access_count = c.getInt(0);

      getWritableDatabase().execSQL("UPDATE " + Tables.StopAttributes.TABLE_NAME + " SET "
              + Tables.StopAttributes.ACCESS_COUNT + " = ? WHERE "
              + Tables.StopAttributes.STOP_CODE + " = ?",
          new Object[]{access_count + 1, stop.getStopCode()});
    } else {

      getWritableDatabase().execSQL("INSERT INTO " + Tables.StopAttributes.TABLE_NAME + "("
              + Tables.StopAttributes.STOP_CODE + "," + Tables.StopAttributes.ACCESS_COUNT +
              " ) VALUES (?,?)",
          new Object[]{stop.getStopCode(), 1});
    }
    c.close();

    context.getContentResolver().notifyChange(BusStopContentProvider.URI_RECENT_STOPS, null);
  }
}
