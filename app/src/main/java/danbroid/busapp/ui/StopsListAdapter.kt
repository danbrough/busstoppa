package danbroid.busapp.ui

import android.graphics.Color
import android.view.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import danbroid.busapp.data.BusStop
import danbroid.busapp.databinding.StopRowBinding

typealias StopSelectedListener = (BusStop) -> Unit
typealias StartDragListener = (StopsListAdapter.BusStopViewHolder) -> Unit
typealias OnCreateContextMenuListener = (StopsListAdapter.BusStopViewHolder, ContextMenu, View) -> Unit

class StopsListAdapter() :
    ListAdapter<BusStop, StopsListAdapter.BusStopViewHolder>(STOPS_DIFF_CALLBACK) {

  companion object {
    val STOPS_DIFF_CALLBACK =
        object : DiffUtil.ItemCallback<BusStop>() {

          override fun areItemsTheSame(oldItem: BusStop, newItem: BusStop) =
              oldItem.code == newItem.code

          override fun areContentsTheSame(oldItem: BusStop, newItem: BusStop) =
              oldItem == newItem
        }
  }

  var onStopSelected: StopSelectedListener? = null
  var onStartDragListener: StartDragListener? = null
  var onCreateContextMenuListener: OnCreateContextMenuListener? = null

  init {
    setHasStableIds(true)
  }


  override fun onBindViewHolder(holder: BusStopViewHolder, position: Int) =
    holder.bind(getItem(position))


  override fun getItemId(position: Int) = getItem(position).code.hashCode().toLong()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
      BusStopViewHolder(StopRowBinding.inflate(LayoutInflater.from(parent.context),parent,false))

  inner class BusStopViewHolder(val binding: StopRowBinding) : RecyclerView.ViewHolder(binding.root), View.OnCreateContextMenuListener {


    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
      onCreateContextMenuListener?.invoke(this, menu, v)
    }

    init {
      onCreateContextMenuListener?.run {
        itemView.setOnCreateContextMenuListener(this@BusStopViewHolder)
      }
    }


    lateinit var stop: BusStop

    fun bind(stop: BusStop) {
      this.stop = stop
      binding.title.text = stop.name
      binding.code.text = stop.code

      onStopSelected?.also { selector ->
        itemView.setOnClickListener {
          selector.invoke(stop)
        }
      }

      onStartDragListener?.also { listener ->
        binding.reorder.setOnTouchListener { _, event ->
          if (event.action == MotionEvent.ACTION_DOWN) listener.invoke(this)
          true
        }
      }

    }

    fun onSelected() {
      log.debug("onSelected()")
      itemView.setBackgroundColor(Color.LTGRAY)
    }

    fun onCleared() {
      log.debug("onCleared()")
      itemView.setBackgroundColor(Color.WHITE)
    }

  }

}


private val log = org.slf4j.LoggerFactory.getLogger(StopsListAdapter::class.java)
