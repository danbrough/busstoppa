package danbroid.busapp.db;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import org.slf4j.LoggerFactory;

import java.util.Locale;

import danbroid.busapp.BusApp;
import danbroid.busapp.db.model.BusStop;
import danbroid.busapp.db.model.Tables;

public class BusStopContentProvider extends ContentProvider {
  private static final org.slf4j.Logger log = LoggerFactory.getLogger(BusStopContentProvider.class);


  public static final String AUTHORITY = BusStopContentProvider.class.getName();

  public static final Uri STOPS_URI = Uri.parse("content://" + AUTHORITY + "/stops");

  public static final Uri STOP_SEARCH_URI = Uri
      .parse("content://" + AUTHORITY + "/" + SearchManager.SUGGEST_URI_PATH_QUERY);

  public static final Uri URI_RECENT_STOPS = Uri.parse("content://" + AUTHORITY +
      "/recent_stops");

  public static final String STOPS_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
      "/stops";

  public static final String STOPS_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/stops";


  public static final int MATCH_STOPS = 1;

  public static final int MATCH_STOP = 2;

  public static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


  public static final String ARG_LOCATION = "location";
  public static final String ARG_STOP = "stop";
  public static final String ARG_ID = "id";
  public static final String ARG_STOP_CODE = "stop_code";
  private static final int MATCH_SEARCH_SUGGEST = 3;

  private static final int MATCH_RECENT_STOPS = 6;

  static {
    uriMatcher.addURI(AUTHORITY, "stops", MATCH_STOPS);
    uriMatcher.addURI(AUTHORITY, "recent_stops", MATCH_RECENT_STOPS);

    uriMatcher.addURI(AUTHORITY, "stops/*", MATCH_STOP);


    // to get suggestions...
    uriMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, MATCH_SEARCH_SUGGEST);
    uriMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", MATCH_SEARCH_SUGGEST);

  }

  private BusStopDB busStopDB;



  @Override
  public boolean onCreate() {
    return true;
  }

  public BusStopDB getBusStopDB() {
    if (busStopDB == null) {
      busStopDB = BusStopDB_.getInstance_(getContext());
    }
    return busStopDB;
  }


  @Override
  public String getType(final Uri uri) {
    //log.trace("getType() :{}", uri);

    switch (uriMatcher.match(uri)) {
      case MATCH_STOPS:
        return STOPS_CONTENT_TYPE;
      case MATCH_STOP:
        return STOPS_CONTENT_ITEM_TYPE;
      case MATCH_SEARCH_SUGGEST:
        return SearchManager.SUGGEST_MIME_TYPE;
      case MATCH_RECENT_STOPS:
        return STOPS_CONTENT_TYPE;
      default:
        throw new IllegalArgumentException("Unknown uri type: " + uri);
    }
  }

  @Override
  public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                      String sortOrder) {
    log.trace("query() uri:{} selection:{}", uri, selection);

    switch (uriMatcher.match(uri)) {

      case MATCH_SEARCH_SUGGEST:
        log.trace("MATCH_SEARCH_SUGGEST");
        if (selectionArgs == null) {
          throw new IllegalArgumentException("selectionArgs must be provided for the Uri: " + uri);
        }
        try {
          return getStopSuggestions(uri, selectionArgs[0]);
        } catch (Throwable t) {
          log.error(t.getMessage(), t);
          return null;
        }

      case MATCH_STOPS:
        log.trace("MATCH_STOPS");
        return getStops(uri, projection, selection, selectionArgs, sortOrder);

      case MATCH_STOP:
        log.trace("query():MATCH_STOP: {}", uri.getLastPathSegment());
        Cursor cur = getBusStopDB().query(Tables.BusStop.TABLE_NAME, null, Tables.BusStop._ID + " = ?", new
                String[]{uri.getLastPathSegment()}, null,
            null);
        if (!cur.moveToFirst()) {
          cur.close();
          return null;
        }
        return cur;

      case MATCH_RECENT_STOPS:
        return getRecentStops();

      default:
        throw new IllegalArgumentException("Unknown uri: " + uri);
    }
  }

  private Cursor getRecentStops() {
    Cursor cursor = getBusStopDB().getRecentStops(10);
    cursor.setNotificationUri(getContext().getContentResolver(), URI_RECENT_STOPS);
    return cursor;
  }

  private Cursor getStopSuggestions(Uri uri, String query) {
    String limit = uri.getQueryParameter("limit");
    log.trace("getStopSuggestions() :{} limit:{}", query, limit);

    query = query.toLowerCase(Locale.getDefault());

    String[] columns = new String[]{BaseColumns._ID, "substr(" + Tables.BusStop.STOP_CODE + ",4) AS " +
        SearchManager.SUGGEST_COLUMN_TEXT_2,
        Tables.BusStop.STOP_NAME + " AS " + SearchManager.SUGGEST_COLUMN_TEXT_1,
        "'" + ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + BusApp.class.getPackage().getName
            () + "/"
            + danbroid.busapp.R.mipmap.ic_launcher + "' AS " + SearchManager.SUGGEST_COLUMN_ICON_1,
        Tables.BusStop._ID + " AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID};

    if (query == null || query.trim().equals("")) {
      //query is empty
      return null;
    }

    return getBusStopDB().query(Tables.BusStop.TABLE_NAME, columns,
        "substr(" + Tables.BusStop.STOP_CODE + ",4) = ? OR " +
            "lower(" + Tables.BusStop.STOP_NAME + ") LIKE ? OR substr(" +
            Tables.BusStop.STOP_CODE + ",4) LIKE ?",
        new String[]{query, "%" + query + "%", query + "%"},
        Tables.BusStop.STOP_CODE + " DESC," +
            Tables.BusStop.STOP_NAME, limit);
  }


  @SuppressLint("DefaultLocale")
  private Cursor getStops(Uri uri, String[] projection, String selection, String[] selectionArgs,
                          String sortOrder) {

    if (selectionArgs == null) {
      throw new IllegalArgumentException("selectionArgs must be provided for the Uri: " + uri);
    }

    String query = selectionArgs[0].toLowerCase();
    log.trace("getStops(): uri:{} query:{}", uri, query);
    return getBusStopDB().query(Tables.BusStop.TABLE_NAME, new String[]{Tables.BusStop._ID, Tables.BusStop
            .STOP_CODE, Tables.BusStop.STOP_NAME},
        Tables.BusStop.STOP_CODE + " LIKE ? OR lower(" + Tables.BusStop.STOP_NAME + ") LIKE ?",
        new String[]{query + "%", "%" + query + "%"}, null, null);

  }

  @Override
  public Uri insert(Uri uri, ContentValues values) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int delete(Uri uri, String selection, String[] selectionArgs) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
    throw new UnsupportedOperationException();
  }


}
