package danbroid.busapp;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.StrictMode;
import android.support.v4.content.pm.ShortcutInfoCompat;
import android.support.v4.content.pm.ShortcutManagerCompat;
import android.support.v4.graphics.drawable.IconCompat;
import android.widget.Toast;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.UiThread;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;

import danbroid.busapp.activities.SplashActivity;
import danbroid.busapp.db.BusStopDB;
import danbroid.busapp.db.model.BusStop;
import danbroid.touchprompt.material.MaterialTouchPrompt;
import okhttp3.Cache;
import okhttp3.OkHttpClient;

@EApplication
public class BusApp extends Application {
  private static org.slf4j.Logger log = LoggerFactory.getLogger(BusApp.class);

  public static final String PREFS_FILE_NAME = "prefs";
  private static final String PREF_INSTALL_DATE = BusApp.class.getName() + ":INSTALL_DATE";

  static {
    MaterialTouchPrompt.install();
  }

  @TargetApi(Build.VERSION_CODES.GINGERBREAD)
  private static void init_gingerbread() {
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog
        ().build();
    StrictMode.setThreadPolicy(policy);
  }

  private static void sleep(long time) {
    try {
      Thread.sleep(time);
    } catch (Exception e) {
    }
  }

  public static BusApp get(Context context) {
    return (BusApp) context.getApplicationContext();
  }

  @Override
  public void onCreate() {
    log.trace("onCreate()");
    super.onCreate();


    SharedPreferences prefs = getPrefs();

    long installDate = prefs.getLong(PREF_INSTALL_DATE, 0L);
    if (installDate == 0) {
      installDate = System.currentTimeMillis();
      prefs.edit().putLong(PREF_INSTALL_DATE, installDate).apply();
    }

    log.debug("installDate: " + new Date(installDate));
  }


  public SharedPreferences getPrefs() {
    return getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
  }

  @Bean
  BusStopDB gtfsDao;

  @UiThread
  public void toast(CharSequence msg) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
  }

  public void createShortcut(BusStop stop) {
    log.info("createShortcut() : " + stop);

    Intent intent = new Intent(Intent.ACTION_VIEW)
        .setComponent(new ComponentName(getPackageName(), SplashActivity.class.getName()))
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        .putExtra(SplashActivity.INTENT_EXTRA_STOP_CODE, stop.getStopCode());

    ShortcutInfoCompat info = new ShortcutInfoCompat.Builder(this, stop.getStopCode())
        .setShortLabel(stop.getStopName())
        .setLongLabel(stop.getStopName())
        .setIcon(IconCompat.createWithResource(this, R.mipmap.ic_launcher))
        .setIntent(intent)
        .build();


    ShortcutManagerCompat.requestPinShortcut(this, info, null);


    toast(getString(R.string.msg_shortcut_created, stop
        .getStopName()));

  }

  private OkHttpClient httpClient;

  public OkHttpClient getHttpClient() {

    if (httpClient == null) {
      synchronized (this) {
        if (httpClient == null) {

          File cacheDir = new File(getCacheDir(), "okhttp");
          cacheDir.mkdirs();
          Cache cache = new Cache(cacheDir, 1024 * 1024 * 2);
          httpClient = new OkHttpClient.Builder().cache(cache).build();

        }
      }
    }

    return httpClient;
  }

  private BusStop currentStop;


  public void setCurrentStop(BusStop currentStop) {
    this.currentStop = currentStop;
  }

  public BusStop getCurrentStop() {
    return currentStop;
  }
}
