package danbroid.busapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import danbroid.busapp.BusApp;
import danbroid.busapp.R;

public class AboutDialogHelper {
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AboutDialogHelper
      .class);
  private final Activity activity;

  public AboutDialogHelper(Activity activity) {
    super();
    this.activity = activity;
  }

  public void showAbout(final boolean showGooglePlayLink, final Runnable whenDone) {
    log.trace("showAbout()");

    activity.runOnUiThread(() -> {
      LayoutInflater inflater = LayoutInflater.from(activity);
      View contentView = inflater.inflate(R.layout.about_text, null);

      TextView textView = contentView.findViewById(R.id.text1);
      String text = textView.getText().toString();
      textView.setText(Html.fromHtml(text));
      textView.setMovementMethod(LinkMovementMethod.getInstance());

      AlertDialog.Builder builder = new AlertDialog.Builder(activity);

      builder.setCustomTitle(inflater.inflate(R.layout.about_title, null));
      builder.setCancelable(true);
      builder.setView(contentView);

      if (showGooglePlayLink) {
        builder.setNegativeButton(R.string.lbl_rate_app, (dialog, which) -> {
          Intent intent = new Intent(Intent.ACTION_VIEW);
          intent.setData(Uri.parse("market://details?id=" + BusApp.class.getPackage().getName
              ()));
          activity.startActivity(intent);
        });
      }

      builder.setPositiveButton(showGooglePlayLink ? android.R.string.cancel : android.R.string
              .ok,
          (dialog, which) -> {
            dialog.dismiss();
            if (whenDone != null)
              whenDone.run();
          });

      builder.show();
    });

  }

}
