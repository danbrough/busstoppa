package danbroid.busapp.ui

import android.content.Context
import android.graphics.Canvas
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import danbroid.busapp.HelpCodes
import danbroid.busapp.R
import danbroid.busapp.activities.MainActivity
import danbroid.busapp.activities.appModel
import danbroid.busapp.activities.displayToolbar
import danbroid.busapp.activities.mainActivity
import danbroid.busapp.data.BusStop
import danbroid.busapp.databinding.RecyclerViewBinding
import danbroid.busapp.models.AppModel
import danbroid.busapp.showDepartures
import danbroid.busapp.utils.ShortcutUtils
import danbroid.touchprompt.findViewRecursive
import danbroid.touchprompt.touchPrompt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


/**
 * A placeholder fragment containing a simple view.
 */
class StopsHistoryFragment : Fragment() {

  private var _binding: RecyclerViewBinding? = null
  private val binding: RecyclerViewBinding
    get() = _binding!!

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ) = RecyclerViewBinding.inflate(inflater, container, false).let {
    _binding = it
    it.root
  }


  lateinit var adapter: StopsListAdapter
  lateinit var itemTouchHelper: ItemTouchHelper

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    setHasOptionsMenu(true)
  }


  private var stops: MutableList<BusStop> = mutableListOf()

  private fun loadStops(stops: List<BusStop>) {
    log.info("loadStops() count: ${stops.size}")

    this.stops = stops.toMutableList()
    adapter.submitList(stops)
    binding.recyclerView.visibility = if (stops.isEmpty()) View.GONE else View.VISIBLE
    binding.emptyText.visibility = if (stops.isEmpty()) View.VISIBLE else View.GONE
    // model.recentStops.removeObserver(stopsObserver)

  }


  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    log.debug("onViewCreated()")


    binding.emptyText.text = getString(R.string.msg_stops_history_empty)

    binding.recyclerView.layoutManager = LinearLayoutManager(context)

    adapter = StopsListAdapter().also {
      it.onStopSelected = { findNavController().showDepartures(it.code) }
      it.onStartDragListener = {
        itemTouchHelper.startDrag(it)
      }
      it.onCreateContextMenuListener = { stopHolder, menu, v ->
        menu.add(R.string.msg_rename_stop).setOnMenuItemClickListener {
          renameStop(stopHolder)
          true
        }
        menu.add(
          requireContext().getString(
            R.string.msg_create_shortcut_short,
            stopHolder.stop.code
          )
        )
          .setOnMenuItemClickListener {
            ShortcutUtils.createShortcut(requireActivity(), stopHolder.stop)
            true
          }
      }
    }

    binding.recyclerView.adapter = adapter

    appModel.recentStops.observe(viewLifecycleOwner, this::loadStops)

    itemTouchHelper =
      ItemTouchHelper(BusItemTouchHelperCallback()).also { it.attachToRecyclerView(binding.recyclerView) }

    touchPrompt(HelpCodes.LONG_PRESS_HERE_TO_RENAME) {
      primaryTextID = R.string.msg_long_press_to_rename
      onBeforeShow = {
        findViewRecursive(binding.recyclerView) {
          it.id == R.id.title
        }?.run {
          target = this
        }
      }
    }

    touchPrompt(HelpCodes.DRAG_TO_REORDER_LIST) {
      primaryTextID = R.string.msg_drag_this_to_reorder
      onBeforeShow = {
        findViewRecursive(binding.recyclerView) {
          it is ImageView && it.id == R.id.reorder
        }?.run {
          target = this
        }
      }
      initialDelay = 1000
      onDismissed = {
        touchPrompt(HelpCodes.SWIPE_TO_REMOVE) {
          primaryTextID = R.string.msg_swipe_to_remove
          onBeforeShow = {
            findViewRecursive(binding.recyclerView) {
              it is TextView && it.id == R.id.title
            }?.run {
              target = this
            }
          }
          initialDelay = 500
        }
      }
    }
  }


  fun renameStop(holder: StopsListAdapter.BusStopViewHolder) {
    log.info("renameStop() ${holder.stop}")
    val inputManager =
      requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    holder.itemView.startActionMode(object : ActionMode.Callback {

      override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        log.debug("onActionItemClicked() mode: $mode")
        saveChanges(mode)
        return true
      }

      fun saveChanges(mode: ActionMode) {


        val newName = holder.itemView.findViewById<TextView>(R.id.edit_title).text.toString().trim()

        if (newName.isEmpty()) {
          showSnackBar(getString(R.string.msg_stop_name_empty))
          return
        }

        mode.finish()

        val oldName = holder.stop.name

        if (oldName == newName) return

        holder.stop.name = newName
        appModel.updateStop(holder.stop)
        holder.itemView.findViewById<TextView>(R.id.title).text = newName

        showSnackBar(getString(R.string.msg_renamed_stop, newName), Snackbar.LENGTH_SHORT) {
          setAction(R.string.lbl_undo) {
            holder.stop.name = oldName
            appModel.updateStop(holder.stop)
          }
        }

      }

      override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        log.debug("onCreateActionMode()")
        mode.title = "Rename ${holder.stop.name}"

        val saveMenuItem = menu.add(R.string.lbl_save)

        activity.displayToolbar(false)
        val edit_title = holder.itemView.findViewById<TextView>(R.id.edit_title)
        edit_title.setOnEditorActionListener { v, actionId, event ->

          if (actionId == EditorInfo.IME_ACTION_DONE) {
            saveChanges(mode)
          }
          true
        }

        edit_title.addTextChangedListener(object : TextWatcher {
          override fun afterTextChanged(s: Editable?) {
            touchPrompt(HelpCodes.EDIT_STOP_NAME) {
              targetID = saveMenuItem.itemId
              primaryTextID = R.string.msg_tap_here_to_save
            }
          }

          override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
          }

          override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
          }
        })


        edit_title.text = holder.itemView.findViewById<TextView>(R.id.title).text
        holder.itemView.findViewById<View>(R.id.title).visibility = View.GONE
        edit_title.visibility = View.VISIBLE
        holder.itemView.findViewById<View>(R.id.code).visibility = View.GONE

        edit_title.requestFocus()
        inputManager.toggleSoftInput(0, 0)

        return true
      }

      override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        log.debug("onPrepareActionMode()")


        return false
      }

      override fun onDestroyActionMode(mode: ActionMode?) {
        log.debug("onDestroyActionMode()")

        activity.displayToolbar(true)
        val edit_title = holder.itemView.findViewById<TextView>(R.id.edit_title)
        val title = holder.itemView.findViewById<TextView>(R.id.title)

        title.visibility = View.VISIBLE
        edit_title.visibility = View.GONE
        holder.itemView.findViewById<View>(R.id.code).visibility = View.VISIBLE

        inputManager.hideSoftInputFromWindow(edit_title.windowToken, 0)
      }

    })

  }

  inner class BusItemTouchHelperCallback : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.UP or ItemTouchHelper.DOWN,
    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
  ) {
    override fun onMove(
      recyclerView: RecyclerView,
      viewHolder: RecyclerView.ViewHolder,
      target: RecyclerView.ViewHolder
    ): Boolean {
      return true
    }

    override fun isLongPressDragEnabled() = false

    override fun onMoved(
      recyclerView: RecyclerView,
      viewHolder: RecyclerView.ViewHolder,
      fromPos: Int,
      target: RecyclerView.ViewHolder,
      toPos: Int,
      x: Int,
      y: Int
    ) {

      Collections.swap(stops, fromPos, toPos)
      adapter.notifyItemMoved(fromPos, toPos)
      val srcStop = (viewHolder as StopsListAdapter.BusStopViewHolder).stop
      val destStop = (target as StopsListAdapter.BusStopViewHolder).stop
      appModel.moveStop(srcStop, destStop)

      super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
      super.clearView(recyclerView, viewHolder)
      log.debug("clearView()")

      if (viewHolder is StopsListAdapter.BusStopViewHolder) {
        //viewHolder.itemView.alpha = 1.0f
        viewHolder.onCleared()
      }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

      lifecycleScope.launch {
        val pos = viewHolder.adapterPosition
        log.debug("onSwiped() pos:$pos")
        val stop = stops.get(pos)
        //adapter.notifyItemRemoved(pos)
        appModel.removeStop(stop)
        withContext(Dispatchers.Main) {

          this@StopsHistoryFragment.showSnackBar(
            getString(
              R.string.msg_stop_was_removed,
              stop.code
            ), Snackbar.LENGTH_LONG
          ) {
            setAction(R.string.lbl_undo) {
              log.debug("undoing swipe")
              //    stops.add(pos, stop)
              appModel.addStop(stop, true)
              // adapter.notifyItemInserted(pos)
            }
          }
        }
      }


    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
      if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
        if (viewHolder is StopsListAdapter.BusStopViewHolder)
          viewHolder.onSelected()
      }
      super.onSelectedChanged(viewHolder, actionState)
    }


    override fun onChildDraw(
      c: Canvas,
      recyclerView: RecyclerView,
      viewHolder: RecyclerView.ViewHolder,
      dX: Float,
      dY: Float,
      actionState: Int,
      isCurrentlyActive: Boolean
    ) {
      if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
        viewHolder.itemView.alpha = 1.0f - Math.abs(dX) / viewHolder.itemView.width
        viewHolder.itemView.translationX = dX
      } else {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
      }
    }
  }


  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.history, menu)
    super.onCreateOptionsMenu(menu, inflater)
    menu.findItem(R.id.action_search)?.also {
      it.actionView = (activity as MainActivity).searchView
    }
  }

  override fun onOptionsItemSelected(item: MenuItem) =
    when (item.itemId) {
      R.id.action_map -> {
        mainActivity.showMap()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }

}

private val log =
  org.slf4j.LoggerFactory.getLogger(StopsHistoryFragment::class.java)
