package danbroid.busapp.utils

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.google.android.material.snackbar.Snackbar
import danbroid.busapp.R
import danbroid.busapp.activities.MainActivity
import danbroid.busapp.data.BusStop
import danbroid.busapp.gtfs.Stop


object ShortcutUtils {
  fun createShortcut(activity: Activity, stop: Stop) {

    val intent = Intent(Intent.ACTION_VIEW)
      .setComponent(ComponentName(activity.packageName, MainActivity::class.java.name))
      .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
      .putExtra(danbroid.busapp.INTENT_EXTRA_STOP_CODE, stop.code)

    val info = ShortcutInfoCompat.Builder(activity, stop.code)
      .setShortLabel(stop.name)
      .setLongLabel(stop.name)
      .setIcon(IconCompat.createWithResource(activity, R.mipmap.ic_launcher))
      .setIntent(intent)
      .build()


    if (ShortcutManagerCompat.requestPinShortcut(activity, info, null)) {

      Snackbar.make(
        activity.findViewById(android.R.id.content),
        activity.getString(R.string.msg_shortcut_created, stop.name),
        Snackbar.LENGTH_SHORT
      ).show()
    }


  }

  /**
   * Create a home-screen shortcut that displays the live departure information for the specified
   * stop.
   *
   * @param stop
   */
  @Deprecated("Migrate to danbroid.busapp.gtfs.Stop")
  fun createShortcut(activity: Activity, stop: BusStop) {

    val intent = Intent(Intent.ACTION_VIEW)
      .setComponent(ComponentName(activity.packageName, MainActivity::class.java.name))
      .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
      .putExtra(danbroid.busapp.INTENT_EXTRA_STOP_CODE, stop.code)

    val info = ShortcutInfoCompat.Builder(activity, stop.code)
      .setShortLabel(stop.name)
      .setLongLabel(stop.name)
      .setIcon(IconCompat.createWithResource(activity, R.mipmap.ic_launcher))
      .setIntent(intent)
      .build()

    if (ShortcutManagerCompat.requestPinShortcut(activity, info, null)) {
      Snackbar.make(
        activity.findViewById(android.R.id.content),
        activity.getString(R.string.msg_shortcut_created, stop.name),
        Snackbar.LENGTH_SHORT
      ).show()
    }


  }
}
