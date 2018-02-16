package danbroid.busapp.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;

import danbroid.busapp.R;
import danbroid.busapp.db.model.BusStop;
import danbroid.busapp.interfaces.MainView;

/**
 * Google map activity for finding and displaying bus stop locations
 */

@EActivity(R.layout.map_activity)
public class MapActivity extends AppCompatActivity implements MainView {
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MapActivity.class);


  @ViewById(R.id.toolbar)
  Toolbar toolbar;

  @AfterViews
  void init() {
    log.debug("init()");
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
  }

  @OptionsItem(android.R.id.home)
  @Override
  public void onBackPressed() {
    super.onBackPressed();
  }

  /**
   * Starts the main activity passing it the stop_code as an Intent extra.
   * Similar to launching the main activity using a bus stop shortcut.
   *
   * @param stop
   */
  @Override
  public void selectStop(BusStop stop) {
    log.trace("selectStop()");

    Intent intent = new Intent(Intent.ACTION_VIEW)
        .setComponent(new ComponentName(getPackageName(), MainActivity_.class.getName()))
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        .putExtra(SplashActivity.INTENT_EXTRA_STOP_CODE, stop.getStopCode());
    startActivity(intent);
    finish();
  }

  @Override
  public void setSwipeRefreshEnabled(boolean enabled) {
  }

  @Override
  public void setRefreshing(boolean refreshing) {
  }


  @Override
  public void showBrowser(String url) {
  }
}
