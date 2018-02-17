package danbroid.busapp.interfaces;

import danbroid.busapp.db.model.BusStop;

/**
 * Created by dan on 1/08/17.
 */

public interface MainView {

  /**
   * Displays a web-page
   * @param url
   */
  void showWebBrowser(String url);

  /**
   * Display the departure information for the stop
   * @param stop
   */
  void selectStop(BusStop stop);

  /**
   * Enable or disable the swipe-to-refresh functionalityS
   * @param enabled
   */

  void setSwipeRefreshEnabled(boolean enabled);

  /**
   * Display the busy/refreshing/reloading feedback
   * @param refreshing
   */
  void setRefreshing(boolean refreshing);


}
