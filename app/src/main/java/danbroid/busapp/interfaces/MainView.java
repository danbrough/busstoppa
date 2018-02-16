package danbroid.busapp.interfaces;

import danbroid.busapp.db.model.BusStop;

/**
 * Created by dan on 1/08/17.
 */

public interface MainView {

  void showBrowser(String url);

  void selectStop(BusStop stop);


  void setSwipeRefreshEnabled(boolean enabled);

  void setRefreshing(boolean refreshing);


}
