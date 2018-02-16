package danbroid.busapp.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.io.File;

import danbroid.busapp.R;
import danbroid.busapp.interfaces.HandlesBackButton;
import danbroid.busapp.interfaces.MainView;
import danbroid.busapp.interfaces.SwipeRefreshable;
import danbroid.busapp.util.HelpCodes;
import danbroid.touchprompt.TouchPrompt;

@EFragment(R.layout.webbrowser)
@OptionsMenu(R.menu.webbrowser)
public class WebBrowser extends Fragment implements SwipeRefreshable, HandlesBackButton {
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger
      (WebBrowser.class);

  protected static final String ARG_URL = "url";

  @ViewById(R.id.browser)
  WebView webView;

  private String url = null;


  public static WebBrowser getInstance(String url) {
    return WebBrowser_.builder().arg(ARG_URL, url).build();
  }

  @OptionsItem(R.id.menu_open_in_external_browser)
  void showInExternalBrowser() {
    try {
      getActivity().startActivity(new Intent(Intent.ACTION_VIEW)
          .setData(Uri.parse(webView.getUrl())));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  MainView getMainView() {
    return (MainView) getActivity();
  }

  @AfterViews
  void init() {
    log.debug("init()");


    WebSettings settings = webView.getSettings();
    settings.setJavaScriptCanOpenWindowsAutomatically(false);
    settings.setJavaScriptEnabled(true);
    settings.setCacheMode(WebSettings.LOAD_DEFAULT);
    settings.setAppCacheEnabled(true);
    settings.setAppCachePath(new File(getActivity().getCacheDir(), "www").getAbsolutePath());

    webView.setWebViewClient(new WebViewClient() {

      @Override
      public void onPageStarted(WebView view, String url, Bitmap favicon) {
        log.trace("onPageStarted() {} title: {}", url, view.getTitle());
        super.onPageStarted(view, url, favicon);
        setRefreshing(true);
      }

      @Override
      public void onPageFinished(WebView view, String url) {
        log.trace("onPageFinished() {} : {}", url, view.getTitle());
        super.onPageFinished(view, url);

        if (!isResumed()) return;
        String title = view.getTitle();
        if (!"about:blank".equals(title))
          getActivity().setTitle(view.getTitle());


        setRefreshing(false);

        new TouchPrompt(getActivity())
            .setTarget(R.id.menu_open_in_external_browser)
            .setSingleShotID(HelpCodes.WEBBROWSER_OPEN_EXTERNAL)
            .setPrimaryText(R.string.lbl_open_in_browser)
            .setSecondaryText(R.string.msg_open_in_external_browser)
            .show();
      }

      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        log.trace("shouldOverrideUrlLoading {}", url);

        if (!url.contains("metlink.org.nz"))
          return true;

        if (url.contains("metlink.org.nz/favourites/"))
          return false;

        return false;
      }
    });


    if (url != null)
      webView.loadUrl(url);
  }


  @Override
  public void onStart() {
    log.trace("onStart()");
    super.onStart();

    setRefreshing(true);
  }

  @Override
  public void onResume() {
    log.trace("onResume()");
    super.onResume();

    Bundle args = getArguments();

    if (args == null) return;

    String url = args.getString(ARG_URL, null);

    if (webView == null) {
      log.error("webView is null");
      return;
    }


    if (url != null) {
      log.trace("loading url: {}", url);
      webView.loadUrl(url);
    }
  }


  @Override
  public boolean onBackButton() {
    log.trace("onBackButton()");
    if (webView != null && webView.canGoBack()) {
      webView.goBack();
      return true;
    }
    return false;
  }


  public void setRefreshing(boolean refreshing) {
    getMainView().setRefreshing(refreshing);
  }

  public void refresh() {
    webView.reload();
  }
}
