package danbroid.busapp.ui;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import danbroid.busapp.R;

/**
 * Created by dan on 11/02/18.
 */
@EFragment(R.layout.recycler_view_list)
public abstract class RecyclerViewList
    extends Fragment {
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RecyclerViewList.class);


  @ViewById(R.id.spinner_container)
  View spinnerContainer;

  @ViewById(R.id.spinner)
  View spinner;

  @ViewById(R.id.spinner_text)
  TextView spinnerText;

  @ViewById(R.id.empty_text_container)
  View emptyTextContainer;

  @ViewById(R.id.empty_text)
  TextView emptyText;

  @ViewById
  View busyIndicator;

  @ViewById(R.id.recycler_view)
  RecyclerView recyclerView;


  @AfterViews
  protected void init() {
    log.debug("init()");
  }

  public void setBusyIndictorVisible(boolean visible) {
    busyIndicator.setVisibility(visible ? View.VISIBLE : View.GONE);
  }

  public void showRecyclerView() {
    emptyTextContainer.setVisibility(View.GONE);
    spinnerContainer.setVisibility(View.GONE);
    recyclerView.setVisibility(View.VISIBLE);
  }

  public void showEmptyText(CharSequence text) {
    recyclerView.setVisibility(View.INVISIBLE);
    spinnerContainer.setVisibility(View.GONE);
    emptyTextContainer.setVisibility(View.VISIBLE);
    if (text != null) emptyText.setText(text);
  }

  public void showSpinner(String text) {
    recyclerView.setVisibility(View.INVISIBLE);
    emptyTextContainer.setVisibility(View.GONE);
    spinnerContainer.setVisibility(View.VISIBLE);
    if (text != null) spinnerText.setText(text);
  }
}
