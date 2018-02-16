package danbroid.busapp.db.wgtn;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.io.IOException;

import danbroid.busapp.BusApp;
import danbroid.busapp.activities.MainActivity;
import danbroid.busapp.db.Provider;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@EBean
public class MetlinkStopInfoProvider implements Provider<String, MetlinkLiveInfo> {
  public static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MetlinkStopInfoProvider.class);

  public static final Gson gson = new GsonBuilder()
      .serializeNulls()
      .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();


  @App
  BusApp app;

  @RootContext
  MainActivity activity;


  @Override
  @Background
  public void process(String stopCode, Provider<MetlinkLiveInfo, ?> processor) {
    log.trace("process() stopCode:{}", stopCode);

    try {
      activity.setRefreshing(true);

      long time = System.currentTimeMillis();

      String url = MetlinkLiveInfo.getDepartureInfoURL(stopCode);

      OkHttpClient client = app.getHttpClient();
      Request request = new Request.Builder().url(url).build();

      Call call = client.newCall(request);

      Response response = call.execute();
      if (response.isSuccessful()) {
        MetlinkLiveInfo stopInfo = gson.fromJson(response.body().charStream(), MetlinkLiveInfo.class);
        log.trace("request took {}", System.currentTimeMillis() - time);
        processor.process(stopInfo, null);
      } else {
        processor.onError(response.message() + " url: " + url, new IOException(response.message()));
      }
    } catch (IOException e) {
      processor.onError(e.getMessage(), e);
    } finally {
      activity.setRefreshing(false);
    }
  }
}
