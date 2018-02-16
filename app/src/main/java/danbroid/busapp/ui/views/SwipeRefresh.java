package danbroid.busapp.ui.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

import danbroid.busapp.R;

/**
 * Created by dan on 13/02/18.
 */
public class SwipeRefresh extends SwipeRefreshLayout {
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SwipeRefresh.class);


  public SwipeRefresh(@NonNull Context context) {
    super(context);

  }

  public SwipeRefresh(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    setColorSchemeResources(R.color.primary_bright, R.color.primary, R.color.yellow);
  }
}
