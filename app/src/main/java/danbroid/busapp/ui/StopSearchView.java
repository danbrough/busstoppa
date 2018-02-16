package danbroid.busapp.ui;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.v7.widget.SearchView;
import android.util.AttributeSet;


/**
 * Action view for the search button in the toolbar
 */
public class StopSearchView extends SearchView {
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(StopSearchView.class);

  public StopSearchView(Context context) {
    super(context);
    init();
  }

  public StopSearchView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public StopSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  private Activity getActivity() {
    Context context = getContext();
    while (context instanceof ContextWrapper) {
      if (context instanceof Activity) {
        return (Activity) context;
      }
      context = ((ContextWrapper) context).getBaseContext();
    }
    return null;
  }

  protected void init() {
    log.debug("init()");
    Activity context = getActivity();

    SearchManager searchManager = (SearchManager) context.getSystemService(Context
        .SEARCH_SERVICE);

    setSearchableInfo(searchManager.getSearchableInfo(context.getComponentName()));
    setIconifiedByDefault(true);
    setMaxWidth(1000);

  }

}
