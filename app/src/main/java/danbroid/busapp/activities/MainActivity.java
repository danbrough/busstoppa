package danbroid.busapp.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import danbroid.busapp.BuildConfig;
import danbroid.busapp.BusApp;
import danbroid.busapp.R;
import danbroid.busapp.db.BusStopDB;
import danbroid.busapp.db.model.BusStop;
import danbroid.busapp.interfaces.HandlesBackButton;
import danbroid.busapp.interfaces.MainView;
import danbroid.busapp.interfaces.SwipeRefreshable;
import danbroid.busapp.ui.AboutDialogHelper;
import danbroid.busapp.ui.MetlinkLiveInfo;
import danbroid.busapp.ui.RecentStops;
import danbroid.busapp.ui.WebBrowser;
import danbroid.busapp.ui.views.SwipeRefresh;
import danbroid.busapp.util.HelpCodes;
import danbroid.touchprompt.TouchPrompt;

@EActivity(R.layout.main_activity)
@OptionsMenu(R.menu.main_activity)
public class MainActivity extends AppCompatActivity implements MainView {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MainActivity.class);


  @App
  BusApp app;

  @Bean
  BusStopDB busStopDB;

  @ViewById(R.id.progress_bar)
  View progressBar;

  private long backPressedAt = 0;

  @ViewById(R.id.toolbar)
  Toolbar toolbar;

  @ViewById(R.id.swipe_refresh)
  SwipeRefresh swipeRefresh;


  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    log.info("onNewIntent() action:{} stop_code:{}", intent.getAction(), intent.getStringExtra
        ("stop_code"));

    processIntent(intent);
  }


  @AfterViews
  void init() {


    setSupportActionBar(toolbar);

    processIntent(getIntent());


    swipeRefresh.setOnRefreshListener(() -> {
      Fragment contentView = getContentView();
      if (contentView instanceof SwipeRefreshable) {
        ((SwipeRefreshable) contentView).refresh();
      }
    });

  }

  protected void processIntent(Intent intent) {
    log.info("processIntent(): action:{}", intent.getAction());

    String action = intent.getAction();

    Bundle extras = intent.getExtras();

    if (BuildConfig.DEBUG && extras != null) {
      for (String extra : extras.keySet()) {
        log.trace("extra: {}:{}", extra, extras.get(extra));
      }
    }


    if (action == null) {
    } else if (action.equals(Intent.ACTION_VIEW)) {
      selectStop(intent.getStringExtra(SplashActivity.INTENT_EXTRA_STOP_CODE));
      return;
    } else if (action.equals(Intent.ACTION_SEARCH)) {
      Uri data = intent.getData();
      if (data != null) {
        log.info("Intent.ACTION_SEARCH with: {}", data);

        selectStop(Long.parseLong(data.getLastPathSegment()));

      } else {
        String query = intent.getStringExtra(SearchManager.QUERY);
        log.trace("Intent.ACTION_SEARCH with query: {}", query);
        //not supported

      }
      return;
    }

    if (getContentView() == null) {
      //initialise the main content with the recent stops list
      showRecentStops();
    }

    showHelp();
  }


  protected boolean navigateBack() {
    Fragment contentView = getContentView();

    if (contentView instanceof HandlesBackButton) {
      if (((HandlesBackButton) contentView).onBackButton()) {
        return true;
      }
    }

    FragmentManager fm = getSupportFragmentManager();
    if (fm.getBackStackEntryCount() > 0) {
      fm.popBackStack();
      return true;
    }

    if (!(getContentView() instanceof RecentStops)) {
      showRecentStops();
      return true;
    }

    return false;
  }

  @OptionsItem(android.R.id.home)
  @Override
  public void onBackPressed() {

    if (navigateBack()) return;

    long time = System.currentTimeMillis();

    if (time - backPressedAt < 2000) {
      //back button pressed twice within 2 seconds
      onActionExit();
      return;
    }

    backPressedAt = time;
    Toast.makeText(this, getString(R.string.msg_press_back_to_quit), Toast.LENGTH_SHORT).show();
  }


  void setContentView(Fragment contentView) {
    setContentView(contentView, false);
  }

  public void setContentView(Fragment contentView, boolean addToBackStack) {
    log.trace("setContentView() {}", contentView);


    TouchPrompt.closeChain();
    progressBar.setVisibility(View.GONE);


    setSwipeRefreshEnabled(contentView instanceof SwipeRefreshable);


    FragmentTransaction ft = getSupportFragmentManager().beginTransaction()
        .replace(R.id.fragment_container, contentView);

    if (addToBackStack) ft.addToBackStack(null);
    ft.commit();

    getSupportActionBar().setDisplayHomeAsUpEnabled(!(contentView instanceof RecentStops));
  }

  public Fragment getContentView() {
    return getSupportFragmentManager().findFragmentById(R.id.fragment_container);
  }

  @UiThread
  @Override
  public void setSwipeRefreshEnabled(boolean enabled) {
    swipeRefresh.setEnabled(enabled);
  }


  @UiThread
  @Override
  public void setRefreshing(boolean refreshing) {
    swipeRefresh.setRefreshing(refreshing);
  }

  public void showLiveInfo(BusStop stop) {
    log.info("showLiveInfo(): {} ", stop);
    setContentView(MetlinkLiveInfo.newInstance(stop));

    busStopDB.incrementAccessCount(stop);
  }


  @UiThread
  public void selectStop(BusStop stop) {
    log.trace("selectStop(): {}", stop);
    app.setCurrentStop(stop);
    showLiveInfo(stop);
  }

  @Background
  public void selectStop(String stopCode) {
    log.trace("selectStop(): code: {}", stopCode);
    BusStop stop = busStopDB.getBusStop(stopCode);
    if (stop != null)
      selectStop(stop);
  }

  @Background
  public void selectStop(long stopID) {
    selectStop(busStopDB.getBusStop(stopID));
  }


  private static final String PREF_ABOUT_DIALOG_VERSION = BusApp.class
      .getName() + ".aboutDialogVersion";
  private static final int ABOUT_DIALOG_VERSION = 1;


  /**
   * Displays the about dialog and then displays introductory help prompts
   */
  public void showHelp() {
    log.trace("showHelp()");

    int aboutVersion = getPrefs().getInt(PREF_ABOUT_DIALOG_VERSION, 0);
    log.warn("showHelp() aboutVersion: " + aboutVersion);
    if (aboutVersion != ABOUT_DIALOG_VERSION) {
      getPrefs().edit().putInt(PREF_ABOUT_DIALOG_VERSION, ABOUT_DIALOG_VERSION).apply();
      new AboutDialogHelper(this).showAbout(false, () -> showPrompts());
    } else {
      showPrompts();
    }

  }

  /**
   * Displays introductory help prompts
   */
  private void showPrompts() {

    //TODO: move messages to strings.xml
    new TouchPrompt(this)
        .setTarget(R.id.menu_search)
        .setPrimaryText("Search Button")
        .setShortDelay()
        .setSecondaryText("Click here to search for a stop.\nYou can search using the name of the stop or via its code.")
        .setSingleShotID(HelpCodes.MAIN_SEARCH_BUTTON)
        .show();
    new TouchPrompt(this)
        .setTarget(R.id.menu_map)
        .setShortDelay()
        .setPrimaryText("Map Button")
        .setSecondaryText("Click here to find a stop using a map")
        .setSingleShotID(HelpCodes.MAIN_MAP_BUTTON)
        .show();
  }

  public SharedPreferences getPrefs() {
    return app.getPrefs();
  }

  @OptionsItem(R.id.menu_about)
  public void onActionAbout() {
    log.debug("onActionAbout()");
    new AboutDialogHelper(this).showAbout(true, null);
  }


  @OptionsItem(R.id.menu_exit)
  public void onActionExit() {
    log.info("onActionExit();");
    finish();
  }

  /**
   * Displays the google map view
   */
  @OptionsItem(R.id.menu_map)
  public void showMap() {
    log.debug("showMap();");
    startActivity(new Intent(this, MapActivity_.class));
  }

  /**
   * Display the recent sstops history list
   */
  public void showRecentStops() {
    log.info("showRecentStops()");
    setContentView(RecentStops.getInstance());
  }

  /**
   * Displays a webpage
   *
   * @param url
   */
  @Override
  public void showWebBrowser(String url) {
    log.info("showWebBrowser() :{}", url);
    setContentView(WebBrowser.getInstance(url), true);
  }

}
