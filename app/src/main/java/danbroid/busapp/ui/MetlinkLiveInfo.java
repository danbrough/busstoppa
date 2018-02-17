package danbroid.busapp.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import danbroid.busapp.BusApp;
import danbroid.busapp.R;
import danbroid.busapp.activities.MainActivity;
import danbroid.busapp.db.Provider;
import danbroid.busapp.db.model.BusStop;
import danbroid.busapp.db.wgtn.MetlinkStopInfoProvider;
import danbroid.busapp.interfaces.BusStopView;
import danbroid.busapp.interfaces.MainView;
import danbroid.busapp.interfaces.SwipeRefreshable;


/**
 * Created by dan on 2/12/16.
 */

@EFragment(R.layout.metlink_liveinfo)
@OptionsMenu(R.menu.live_info)
public class MetlinkLiveInfo extends Fragment implements SwipeRefreshable {
  public static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MetlinkLiveInfo.class);

  private static final int TYPE_STOP = 0;
  private static final int TYPE_NOTICE = 1;
  private static final int TYPE_SERVICE = 2;
  private static final int TYPE_DAY_ROW = 3;

  private danbroid.busapp.db.wgtn.MetlinkLiveInfo stopInfo;

  @ViewById(R.id.recycler_view)
  RecyclerView recyclerView;

  @ViewById(R.id.error_container)
  View errorContainer;

  @ViewById(R.id.error_message)
  TextView errorMessage;


  private Adapter adapter;
  private java.text.DateFormat timeFormat;
  private List<Object[]> data = new LinkedList<>();
  private View noticeView;

  @App
  BusApp app;

  private BusStop stop;

  @Bean
  MetlinkStopInfoProvider stopInfoJob;

  private final Handler handler = new Handler(msg -> {
    refresh();
    return true;
  });

  static String getDayOfMonthSuffix(final int n) {
    if (n >= 11 && n <= 13) {
      return "th";
    }
    switch (n % 10) {
      case 1:
        return "st";
      case 2:
        return "nd";
      case 3:
        return "rd";
      default:
        return "th";
    }
  }

  public static MetlinkLiveInfo newInstance(BusStop stop) {
    return MetlinkLiveInfo_.builder().arg(BusStopView.ARG_STOP, stop).build();
  }

  @Click(R.id.action_retry)
  @Override
  public void refresh() {
    log.trace("refresh()");
    handler.removeMessages(1);


    if (isDetached()) {
      log.error("isDetached()");
      return;
    }

    if (getActivity() == null) {
      log.error("activity is null");
      return;
    }
    errorContainer.setVisibility(View.GONE);


    getActivity().setTitle(getString(R.string.msg_loading));


    stopInfoJob.process(stop.getStopCode(), new Provider<danbroid.busapp.db.wgtn.MetlinkLiveInfo, Void>() {
      @Override
      public void process(danbroid.busapp.db.wgtn.MetlinkLiveInfo stopInfo, Provider<Void, ?> next) {
        setStopInfo(stopInfo);
      }

      @Override
      public void onError(String message, Throwable t) {
        stopInfoError(message, t);
      }
    });
  }

  @UiThread
  protected void stopInfoError(String message, Throwable t) {
    log.error(message, t);

    if (!isResumed() || getActivity() == null) return;

    getActivity().setTitle(getString(R.string.lbl_error));

    errorMessage.setText(message);
    errorContainer.setVisibility(View.VISIBLE);
  }


  @AfterViews
  protected void init() {
    errorContainer.setVisibility(View.GONE);
    timeFormat = DateFormat.getTimeFormat(getContext());
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView.setAdapter(adapter = new Adapter());

    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        int topRowVerticalPosition =
            (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView
                .getChildAt(0).getTop();
        getMainView().setSwipeRefreshEnabled(topRowVerticalPosition >= 0);
      }

    });


  }

  MainView getMainView() {
    return (MainView) getActivity();
  }


  @Override
  public void onStart() {
    log.trace("onStart()");
    super.onStart();
    stop = (BusStop) getArguments().getSerializable(BusStopView.ARG_STOP);
  }

  @Override
  public void onResume() {
    log.trace("onResume()");
    super.onResume();

    refresh();
  }

  @Override
  public void onPause() {
    log.trace("onPause()");
    super.onPause();
    handler.removeMessages(1);
  }


  @UiThread
  public void setStopInfo(final danbroid.busapp.db.wgtn.MetlinkLiveInfo stopInfo) {
    log.info("setStopInfo(): stopInfo: {} stop: {}", stopInfo, stop);

    if (isDetached()) {
      log.debug("isDetached");
      return;
    }

    if (getActivity() == null) {
      log.debug("activity is null");
      return;
    }


    handler.sendEmptyMessageDelayed(1, 15000);

    if (stop != null)
      getActivity().setTitle(stop.getStopName() + " (" + stop.getStopCode() + ")");

    if (this.stopInfo != null && stopInfo.LastModified.equals(this.stopInfo.LastModified)) return;

    this.stopInfo = stopInfo;

    data.clear();
    data.add(new Object[]{TYPE_STOP});

    for (danbroid.busapp.db.wgtn.MetlinkLiveInfo.Notice notice : stopInfo.Notices) {
      data.add(new Object[]{TYPE_NOTICE, notice, true});
    }

    for (danbroid.busapp.db.wgtn.MetlinkLiveInfo.Notice notice : stopInfo.NoticesClosures) {
      data.add(new Object[]{TYPE_NOTICE, notice, false});
    }

    Date today = stopInfo.LastModified;

    if (stopInfo.NoticesClosures.length == 0) {
      for (danbroid.busapp.db.wgtn.MetlinkLiveInfo.ServiceInfo serviceInfo : stopInfo.Services) {
        if (serviceInfo.DisplayDeparture != null && serviceInfo.DisplayDeparture.getDay() !=
            today.getDay()) {
          today = serviceInfo.DisplayDeparture;
          data.add(new Object[]{TYPE_DAY_ROW, today});

        }
        data.add(new Object[]{TYPE_SERVICE, serviceInfo});
      }
    }

    adapter.notifyDataSetChanged();
    recyclerView.scrollToPosition(0);


  }


  @OptionsItem(R.id.menu_open_in_browser)
  public void openInBrowser() {
    if (stop != null) {
      try {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(
            "https://www.metlink.org.nz/stop/"
                + stop.getShortStopCode()));
        getActivity().startActivity(browserIntent);
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    }
  }


  @OptionsItem(R.id.menu_create_shortcut)
  public void createShortcut() {
    app.createShortcut(stop);
  }


  protected class Adapter extends RecyclerView.Adapter<ViewHolder> {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      switch (viewType) {
        case TYPE_STOP:
          return new StopViewHolder(parent);
        case TYPE_NOTICE:
          return new NoticeViewHolder(parent);
        case TYPE_SERVICE:
          return new ServiceViewHolder(parent);
        case TYPE_DAY_ROW:
          return new DayRowViewHolder(parent);
        default:
          throw new IllegalArgumentException("Invalid viewType: " + viewType);
      }
    }


    @Override
    public int getItemViewType(int position) {
      return (Integer) data.get(position)[0];
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
      holder.bind(position);
    }

    @Override
    public int getItemCount() {
      return data.size();
    }
  }

  protected abstract class ViewHolder extends RecyclerView.ViewHolder {
    public ViewHolder(View itemView) {
      super(itemView);
    }

    protected abstract void bind(int position);
  }

  protected class StopViewHolder extends ViewHolder {
    private final TextView stopCode;
    private final TextView stopName;
    private final TextView stopUpdated;
    private final ImageView stopImage;


    public StopViewHolder(View itemView) {
      super(LayoutInflater.from(itemView.getContext()).inflate(R.layout.stopinfo_stop,
          (ViewGroup) itemView, false));
      this.stopCode = super.itemView.findViewById(R.id.stop_code);
      this.stopName = super.itemView.findViewById(R.id.stop_name);
      this.stopUpdated = super.itemView.findViewById(R.id.stop_updated);
      this.stopImage = super.itemView.findViewById(R.id.stop_image);
    }

    @Override
    protected void bind(int position) {
      log.trace("bind()");

      String stopCodeText = "Stop ID: " + stopInfo.Stop.Sms;
      if (stopInfo.Stop.Farezone != null) stopCodeText += " Farezone: " + stopInfo.Stop.Farezone;
      stopCode.setText(stopCodeText);
      stopName.setText(stopInfo.Stop.Name);
      stopUpdated.setText("Updated: " + timeFormat.format(stopInfo.LastModified));

      int width, height;
      width = height = 100;

      String url = "https://maps.googleapis.com/maps/api/streetview?size=" + width + "x" +
          height +
          "&location=" + stopInfo.Stop.Lat + "," + stopInfo.Stop.Long + "&pitch=-0" +
          ".76&key=" + getString(R.string.google_street_view_key);

      if (TextUtils.isEmpty(stopInfo.Stop.Icon)) {
        stopImage.setVisibility(View.GONE);
      } else {
        stopImage.setVisibility(View.VISIBLE);

        Glide.with(itemView.getContext())
            .load(url)
            .into(stopImage);
      }
    }
  }

  protected class NoticeViewHolder extends ViewHolder {


    private final TextView noticeText;
    private boolean canHide;

    public NoticeViewHolder(View itemView) {
      super(LayoutInflater.from(itemView.getContext()).inflate(R.layout.stopinfo_notice,
          (ViewGroup) itemView, false));
      noticeText = super.itemView.findViewById(R.id.notice_text);

    }

    @Override
    protected void bind(int position) {
      Object[] info = data.get(position);
      danbroid.busapp.db.wgtn.MetlinkLiveInfo.Notice notice = (danbroid.busapp.db.wgtn.MetlinkLiveInfo.Notice) info[1];
      canHide = (boolean) info[2];
      noticeText.setText(notice.LineNote);
      ((CardView) itemView).setCardBackgroundColor(getResources().getColor(canHide ? R.color
          .primary_light : android.R.color.holo_red_dark));

      if (noticeView == null) {
        noticeView = noticeText;
      }
    }
  }

  protected class ServiceViewHolder extends ViewHolder implements View
      .OnClickListener {
    private final TextView serviceCode;
    private final TextView routeName;
    private final View vehicleFeature;
    private final TextView due;
    private danbroid.busapp.db.wgtn.MetlinkLiveInfo.ServiceInfo serviceInfo;

    public ServiceViewHolder(View itemView) {
      super(LayoutInflater.from(itemView.getContext()).inflate(R.layout.stopinfo_service,
          (ViewGroup) itemView, false));
      serviceCode = super.itemView.findViewById(R.id.service_code);
      routeName = super.itemView.findViewById(R.id.route_name);
      due = super.itemView.findViewById(R.id.due);
      vehicleFeature = super.itemView.findViewById(R.id.vehicle_feature);
      super.itemView.setOnClickListener(this);
    }

    @Override
    protected void bind(int position) {

      serviceInfo = (danbroid.busapp.db.wgtn.MetlinkLiveInfo.ServiceInfo) data.get(position)[1];

      String code = serviceInfo.Service.Code;
      try {
        code = String.valueOf(Integer.parseInt(serviceInfo.Service.Code));
      } catch (Exception e) {
      }
      serviceCode.setText(code);
      routeName.setText(serviceInfo
          .DestinationStopName);

      if (serviceInfo.DepartureStatus != null && serviceInfo.DepartureStatus.equals("cancelled")) {
        routeName.setText("Cancelled");
      }
      due.setText(getTime(stopInfo.LastModified.getTime(), serviceInfo
          .DisplayDeparture));
      vehicleFeature.setVisibility("lowFloor".equals(serviceInfo.VehicleFeature) ? View.VISIBLE :
          View.INVISIBLE);
    }


    public String getTime(long now, Date d) {
      if (d == null) return "null";
      //return String.format("%.02f", (d.getTime() - System.currentTimeMillis()) / 60000f);
      float minutes = (d.getTime() - now) / 60000f;
      if (minutes <= 2) return "Due";
      if (minutes <= 20) return (int) minutes + " mins";

      return timeFormat.format(d).toLowerCase(Locale.getDefault());

    }

    @Override
    public void onClick(View view) {
      ((MainActivity) getActivity()).showWebBrowser(serviceInfo.getTimetableURL());
    }
  }

  protected class DayRowViewHolder extends ViewHolder {

    private final TextView dateText;


    public DayRowViewHolder(View itemView) {
      super(LayoutInflater.from(itemView.getContext()).inflate(R.layout.stopinfo_dayrow,
          (ViewGroup) itemView, false));
      this.dateText = super.itemView.findViewById(R.id.date);
    }


    @Override
    protected void bind(int position) {
      Date date = (Date) data.get(position)[1];
      log.trace("date: " + date);

      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      int day = cal.get(Calendar.DAY_OF_MONTH);
      log.debug("day: " + day);
      String s = new SimpleDateFormat("EEE dd").format
          (date) + getDayOfMonthSuffix(day) + ' ';
      s += new SimpleDateFormat("MMM").format(date);
      dateText.setText(s);

    }
  }
}
