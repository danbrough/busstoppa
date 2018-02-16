package danbroid.busapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import danbroid.busapp.db.model.BusStop;

/**
 * Displays a splash screen and loads the main activity.
 * <p>
 * if {@link SplashActivity#INTENT_EXTRA_STOP_CODE} has been specified then
 * the live information screen will be displayed for the specified stop.
 * <p>
 * <p>
 * This occurs when the activity has been loaded via a shortcut created by
 * {@link danbroid.busapp.BusApp#createShortcut(BusStop)}
 */

public class SplashActivity extends Activity {

  /**
   * Intent parameter for selecting a particular bus stopS
   */

  public static final String INTENT_EXTRA_STOP_CODE = "stop_code";

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    showMainActivity();
  }

  private void showMainActivity() {
    Intent intent = getIntent();
    String action = intent.getAction();
    Bundle extras = getIntent().getExtras();

    intent = new Intent(SplashActivity.this, MainActivity_.class);
    intent.setAction(action);

    if (extras != null) {
      intent.putExtras(extras);
    }
    startActivity(intent);
    finish();
  }
}
