package danbroid.busapp.ui

import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import danbroid.busapp.R

fun Fragment.showSnackBar(
  msg: CharSequence, @BaseTransientBottomBar.Duration duration: Int = Snackbar.LENGTH_SHORT,
  builder: (Snackbar.() -> Unit)? = null
) =
  Snackbar.make(requireView(), msg, duration).also {
    it.setActionTextColor(ResourcesCompat.getColor(resources, R.color.colorAccent, null))
    builder?.invoke(it)
  }.show()