package danbroid.busapp.ui

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import danbroid.busapp.R
import danbroid.busapp.models.HasPrefs
import danbroid.busapp.models.IntPref
import danbroid.busapp.prefs


class AboutDialogHelper(private val activity: Activity) : HasPrefs {

  enum class AboutPrefKeys {
    about_version
  }
  override val prefs: SharedPreferences
    get() = activity.prefs

  private var aboutVersion by IntPref(AboutPrefKeys.about_version, 0)


  fun showAbout(showGooglePlayLink: Boolean = false, forceShow: Boolean = false, whenDone: (() -> Unit)? = null) {

    if (!forceShow) {
      val oldVersion = activity.resources.getInteger(R.integer.about_version)
      if (aboutVersion != oldVersion) {
        aboutVersion = oldVersion
      } else {
        whenDone?.invoke()
        return
      }
    }

    activity.runOnUiThread {
      val inflater = LayoutInflater.from(activity)
      val contentView = inflater.inflate(R.layout.about_text, null)

      val textView = contentView.findViewById(R.id.text1) as TextView
      val text = textView.text.toString()
      textView.text = Html.fromHtml(text)
      textView.movementMethod = LinkMovementMethod.getInstance()

      val builder = AlertDialog.Builder(activity)

      builder.setCustomTitle(inflater.inflate(R.layout.about_title, null))
      builder.setCancelable(true)
      builder.setView(contentView)

      if (showGooglePlayLink) {
        builder.setNegativeButton(R.string.lbl_rate_app) { _, _ ->
          val intent = Intent(Intent.ACTION_VIEW)
          intent.data = Uri.parse("market://details?id=${activity.applicationContext.packageName}")
          activity.startActivity(intent)
        }
      }

      builder.setPositiveButton(if (showGooglePlayLink)
        android.R.string.cancel
      else
        android.R.string
          .ok,
        { dialog, which ->
          dialog.dismiss()
          whenDone?.invoke()
        })

      builder.show()
    }

  }


}
