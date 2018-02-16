package danbroid.busapp.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.PopupMenu;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsMenu;

import danbroid.busapp.R;
import danbroid.busapp.db.BusStopContentProvider;
import danbroid.busapp.db.BusStopDB;
import danbroid.busapp.interfaces.MainView;

/**
 * Created by dan on 28/07/17.
 */


@EFragment(R.layout.recycler_view)
@OptionsMenu(R.menu.recent_stops)
public class RecentStops extends BusStopListFragment {
  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(RecentStops.class);

  @Bean
  BusStopDB busStopDB;

  public static RecentStops getInstance() {
    return RecentStops_.builder().build();
  }


  protected void init() {
    super.init();
    getActivity().setTitle(getString(R.string.lbl_recent_stops));
  }


  @Override
  public CharSequence getEmptyMessage() {
    return getString(R.string.msg_recent_stops_empty);
  }


  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    return new CursorLoader(getContext(),
        BusStopContentProvider.URI_RECENT_STOPS,
        null, null, null, null);
  }

  @Override
  protected void onItemClick(StopViewHolder holder) {
    ((MainView) getActivity()).selectStop(holder.stop);
  }

  @Override
  protected void onLongItemClick(StopViewHolder holder) {
    PopupMenu menu = new PopupMenu(getContext(), holder.itemView);
    menu.inflate(R.menu.stop_popup_menu);

    menu.setOnMenuItemClickListener(item -> {
      switch (item.getItemId()) {
        case R.id.menu_create_shortcut:
          app.createShortcut(holder.stop);
          break;
        case R.id.menu_remove_from_favourites:
          removeStop(holder);
          break;
      }
      return true;
    });

    menu.show();
  }

  @Override
  protected void trashClicked(final StopViewHolder holder) {
    removeStop(holder);
  }

  private void removeStop(final StopViewHolder holder) {

    log.debug("removeStop(): {}", holder.stop);
    busStopDB.removeFromFavourites(holder.stop);

    Snackbar.make(getView(),
        getString(R.string.msg_removed_recent_stop, holder.stop.getShortStopCode()),
        Snackbar.LENGTH_LONG)
        .setAction("UNDO", view -> {
          busStopDB.incrementAccessCount(holder.stop);
          getContext().getContentResolver().notifyChange(BusStopContentProvider.URI_RECENT_STOPS, null);

        })
        .show();
  }


}
