package danbroid.busapp.db;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.io.IOException;

import danbroid.busapp.BusApp;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by dan on 19/02/18.
 */
@EBean(scope = EBean.Scope.Singleton)
public class Test {
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Test.class);

  @Bean
  BusStopDB stopDB;

  @App
  BusApp app;

  @Background
  public void test() {
    log.error("test()");
    OkHttpClient client = app.getHttpClient();
    Request request = new Request.Builder().url("https://www.metlink.org.nz/api/v1/StopList/").build();
    Call call = client.newCall(request);
    try {
      Response response = call.execute();
      log.error("network response: " + response.networkResponse());
      log.error("cache response: " + response.cacheResponse());
      Headers headers = response.headers();
      for (String header : headers.names()) {
        log.trace("header: {} -> {}",header, headers.get(header));
      }
      response.body().close();

    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }


  }
}
