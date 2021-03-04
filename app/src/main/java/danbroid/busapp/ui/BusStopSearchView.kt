package danbroid.busapp.ui

import android.content.Context
import android.content.res.ColorStateList
import android.database.Cursor
import android.database.MatrixCursor
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.ImageViewCompat
import androidx.cursoradapter.widget.CursorAdapter
import danbroid.busapp.R
import danbroid.busapp.metlinkold.BusStopInfo

private val log by lazy {
  org.slf4j.LoggerFactory.getLogger(BusStopSearchView::class.java)
}

class BusStopSearchView(context: Context, attributeSet: AttributeSet? = null) :
  SearchView(context, attributeSet) {


  init {
    setIconifiedByDefault(true)
    isSubmitButtonEnabled = false
    suggestionsAdapter = BusStopSuggestionsAdapter(context)

    setOnSuggestionListener(object : OnSuggestionListener {
      override fun onSuggestionSelect(position: Int): Boolean = false

      override fun onSuggestionClick(position: Int): Boolean {
        onStopSelectListener?.let {
          with(suggestionsAdapter.cursor!!) {
            if (moveToPosition(position)) {
              it.invoke(BusStopInfo(getString(1), getString(2), getString(3)))
            }
          }
        }
        return true
      }
    })
  }


  var onStopSelectListener: ((stopInfo: BusStopInfo) -> Unit)? = null
  var onActionViewExpandedListener: ((expanded: Boolean) -> Unit)? = null

  var results: List<BusStopInfo>? = null
    set(value) {
      displaySuggestions(value)
    }

  private fun displaySuggestions(value: List<BusStopInfo>?) {
    val suggestions = value ?: return
    var id = 0
    val cursor = MatrixCursor(arrayOf("_id", "stopID", "stopCode", "stopName"))
    suggestions.forEach {
      cursor.addRow(arrayOf(id++, it.id, it.code, it.name))
    }
    suggestionsAdapter.swapCursor(cursor)
  }


  override fun onActionViewExpanded() {
    super.onActionViewExpanded()
    expanded = true
    onActionViewExpandedListener?.invoke(true)
  }

  override fun dispatchKeyEventPreIme(event: KeyEvent): Boolean {
    log.trace("dispatchKeyEventPreIme() $event")
    if (event.keyCode == KeyEvent.KEYCODE_BACK &&
      event.action == KeyEvent.ACTION_UP
    ) {
      log.trace("closing search view")
      onActionViewCollapsed()
    }
    return super.dispatchKeyEventPreIme(event)
  }

  var expanded: Boolean = false

  fun closeSearchView(): Boolean {
    if (expanded) {
      onActionViewCollapsed()
      return true
    }
    return false
  }

  override fun onActionViewCollapsed() {
    super.onActionViewCollapsed()
    expanded = false
    onActionViewExpandedListener?.invoke(false)
  }


}


class BusStopSuggestionsAdapter(context: Context) : CursorAdapter(context, null, 0) {

  private val inflater =
    LayoutInflater.from(context)


  override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?) =
    inflater.inflate(
      androidx.appcompat.R.layout.abc_search_dropdown_item_icons_2line,
      parent,
      false
    )

  override fun bindView(view: View, context: Context, cursor: Cursor) {

    view.findViewById<TextView>(android.R.id.text1).apply {
      text = cursor.getString(3)?.let {
        val i = it.indexOf(" - ")
        if (i > -1) it.substring(i + 3) else it
      }
      visibility = View.VISIBLE
    }

    view.findViewById<TextView>(android.R.id.text2).apply {
      text = "Code: ${cursor.getString(2)}"
      visibility = View.VISIBLE
    }

    view.findViewById<ImageView>(android.R.id.icon1)?.apply {
      setImageResource(R.drawable.ic_bus)
      visibility = View.VISIBLE


      ImageViewCompat.setImageTintList(
        this,
        ColorStateList.valueOf(
          ResourcesCompat.getColor(
            resources,
            R.color.colorPrimaryLight,
            context.theme
          )
        )
      )
    }
  }
}