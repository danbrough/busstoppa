package danbroid.busapp.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import danbroid.busapp.BusApp;
import danbroid.busapp.R;
import danbroid.busapp.db.BusStopContentProvider;
import danbroid.busapp.db.model.BusStop;
import danbroid.util.ui.CursorRecyclerViewAdapter;

/**
 * Created by dan on 28/07/17.
 */


@EFragment(R.layout.recycler_view)
public class BusStopListFragment extends Fragment implements  LoaderManager.LoaderCallbacks<Cursor> {
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BusStopListFragment
      .class);


  protected CursorRecyclerViewAdapter<StopViewHolder> adapter;
  @ViewById(R.id.empty_text)
  TextView emptyText;

  @ViewById(R.id.recycler_view)
  RecyclerView recyclerView;

  @App
  protected BusApp app;


  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    return new CursorLoader(getContext(), BusStopContentProvider.STOPS_URI, null, null, null, null);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    log.trace("onLoadFinished()");
    if (data == null) return;
    adapter.swapCursor(data);
    int count = data.getCount();
    if (count > 0) {
      emptyText.setVisibility(View.GONE);
      recyclerView.setVisibility(View.VISIBLE);
    } else {
      emptyText.setText(getEmptyMessage());
    }
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
  }

  public CharSequence getEmptyMessage() {
    return getString(R.string.msg_no_stops_found);
  }

  public CharSequence getLoadingMessage() {
    return getString(R.string.msg_loading);
  }

  protected class StopViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
    private final TextView title;
    private final TextView text2;
    protected BusStop stop;

    public StopViewHolder(ViewGroup container) {
      super(container);
      title = container.findViewById(R.id.title);
      text2 = container.findViewById(R.id.code);
      container.findViewById(R.id.delete_icon).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          trashClicked(StopViewHolder.this);
        }
      });
      container.setOnLongClickListener(this);
      container.setOnClickListener(this);
    }

    public void bind(BusStop stop) {
      this.stop = stop;

      title.setText(stop.getStopName());
      text2.setText(stop.getShortStopCode());
    }

    @Override
    public boolean onLongClick(View v) {
      BusStopListFragment.this.onLongItemClick(this);
      return true;
    }

    @Override
    public void onClick(View v) {
      BusStopListFragment.this.onItemClick(this);

    }
  }

  protected void trashClicked(StopViewHolder holder) {

  }


  protected void onItemClick(StopViewHolder holder) {
  }

  protected void onLongItemClick(StopViewHolder holder) {
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.recycler_view, container, false);
  }

  @AfterViews
  protected void init() {


    adapter = new CursorRecyclerViewAdapter<StopViewHolder>() {
      @Override
      public void onBindViewHolder(StopViewHolder stopViewHolder, Cursor cursor) {
        stopViewHolder.bind(new BusStop(cursor));
      }

      @Override
      public StopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return BusStopListFragment.this.onCreateViewHolder(parent, viewType);
      }
    };


    recyclerView.setAdapter(adapter);
    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
    recyclerView.setLayoutManager(layoutManager);
    refresh();
  }


  public void refresh() {
    log.trace("refresh()");
    emptyText.setText(getLoadingMessage());
    recyclerView.setVisibility(View.GONE);
    emptyText.setVisibility(View.VISIBLE);
    getLoaderManager().restartLoader(0, getArguments(), this);
  }

  protected StopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    ViewGroup view = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.stop_row, parent, false);
    return new StopViewHolder(view);
  }


}
