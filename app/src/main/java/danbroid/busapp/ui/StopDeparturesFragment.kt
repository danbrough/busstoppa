package danbroid.busapp.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.format.DateFormat
import android.view.*
import android.widget.TextView
import androidx.annotation.IntDef
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.DrawableImageViewTarget
import danbroid.busapp.*
import danbroid.busapp.activities.MainActivityInterface
import danbroid.busapp.activities.appModel
import danbroid.busapp.activities.mainActivity
import danbroid.busapp.databinding.StopDeparturesBinding
import danbroid.busapp.databinding.ViewholderServiceBinding
import danbroid.busapp.databinding.ViewholderStopBinding
import danbroid.busapp.gtfs.Route
import danbroid.busapp.gtfs.Stop
import danbroid.busapp.metlinkold.StopDepartures
import danbroid.busapp.models.StopDeparturesModel
import danbroid.busapp.models.StopDeparturesModelFactory
import danbroid.busapp.utils.GlideApp
import danbroid.busapp.utils.ShortcutUtils
import danbroid.touchprompt.touchPrompt
import danbroid.util.resource.toResourceColour
import java.text.SimpleDateFormat
import java.util.*


private val log by lazy {
  org.slf4j.LoggerFactory.getLogger(StopDeparturesFragment::class.java)
}

@IntDef(STOP, NOTICE, SERVICE, DAY_START, NO_DEPARTURES)
@Retention(AnnotationRetention.SOURCE)
annotation class ViewType

const val STOP = 1
const val NOTICE = 2
const val SERVICE = 3
const val DAY_START = 4
const val NO_DEPARTURES = 5


class StopDeparturesFragment : Fragment() {

  private val stopCode: String by lazy {
    requireArguments().getString(NavGraph.arg.stopCode)!!
  }

  val model by viewModels<StopDeparturesModel> {
    log.debug("creating stop departures model for $stopCode")
    StopDeparturesModelFactory(this@StopDeparturesFragment, stopCode)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.stop_departures, menu)
    super.onCreateOptionsMenu(menu, inflater)
  }

  override fun onOptionsItemSelected(item: MenuItem) =
    when (item.itemId) {
      R.id.action_shortcut -> {
        model.stopDepartures.value?.first?.also {
          ShortcutUtils.createShortcut(requireActivity(), it)
        }
        true
      }
      R.id.action_browser -> {
        mainActivity.showBrowser("https://www.metlink.org.nz/stop/${stopCode}")
        true
      }
      R.id.action_map -> {
        findNavController().showMap(stopCode)
        true
      }
      else -> super.onOptionsItemSelected(item)
    }


  private var _binding: StopDeparturesBinding? = null
  private val binding: StopDeparturesBinding
    get() = _binding!!

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ) = StopDeparturesBinding.inflate(inflater, container, false).let {
    _binding = it
    it.root
  }

  lateinit var stopAdapter: StopDeparturesAdapter

  val departuresObserver = Observer<Pair<Stop, StopDepartures>> {
    log.error("stopDepatures for $stopCode modified in state: ${lifecycle.currentState}")
    stopAdapter.setDepartures(it.first, it.second, appModel.routes.value)
  }

  override fun onResume() {
    log.trace("onResume() $stopCode")
    super.onResume()
    model.stopDepartures.observeForever(departuresObserver)
  }

  override fun onStop() {
    log.trace("onStop()  $stopCode")
    super.onStop()
  }

  override fun onPause() {
    log.trace("onPause() ${stopCode}")
    super.onPause()
    model.stopDepartures.removeObserver(departuresObserver)
  }

  override fun onStart() {
    log.trace("onStart() $stopCode")
    super.onStart()
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    log.debug("onViewCreated()")

    stopAdapter = StopDeparturesAdapter(stopCode, mainActivity, onStopClicked = {
      mainActivity.showBrowser("https://www.metlink.org.nz/stop/${stopCode}")
    }, onServiceClicked = {
      mainActivity.showBrowser("https://www.metlink.org.nz/service/$it?stop=$stopCode")
    })

    binding.recyclerView.adapter = stopAdapter
    binding.recyclerView.layoutManager = LinearLayoutManager(context)

    binding.swipeRefreshLayout.setColorSchemeColors(
      *arrayOf(
        R.color.colorPrimaryBright,
        R.color.colorAccent,
        R.color.colorPrimaryLight
      ).map { color ->
        ResourcesCompat.getColor(resources, color, requireActivity().theme)
      }.toIntArray()
    )

    binding.swipeRefreshLayout.setOnRefreshListener {
      model.refreshData()
      binding.swipeRefreshLayout.isRefreshing = false
    }


    // model.stopDepartures.observe(viewLifecycleOwner, departuresObserver)

    model.updateInProgress.observe(viewLifecycleOwner) {
      view.findViewById<View>(R.id.progress_bar).visibility = if (it) View.VISIBLE else View.GONE
    }


    touchPrompt(HelpCodes.VIEW_SERVICE_IN_BROWSER) {
      primaryTextID = R.string.msg_view_service_in_browser
      initialDelay = 500

    }


    touchPrompt(HelpCodes.VIEW_STOP_IN_BROWSER) {
      primaryTextID = R.string.msg_tap_here_for_stop_website
      targetID = R.id.action_browser
    }


  }


  abstract class BaseViewHolder<T : DataEntry>(itemView: ViewGroup) :
    RecyclerView.ViewHolder(itemView) {
    abstract fun onBind(data: T)
  }

  sealed class DataEntry(@ViewType val type: Int) {
    data class Stop(val stop: danbroid.busapp.gtfs.Stop, val fareZone: String) :
      DataEntry(STOP)

    data class Notice(val notice: danbroid.busapp.metlinkold.Notice) : DataEntry(NOTICE)
    data class Service(val service: StopDepartures.Departure) : DataEntry(SERVICE)
    data class DayStart(val date: Date) : DataEntry(DAY_START)
    object NoDepartures : DataEntry(NO_DEPARTURES)
  }

  class StopDeparturesAdapter(
    val stopCode: String,
    val mainActivity: MainActivityInterface,
    val onStopClicked: () -> Unit,
    var onServiceClicked: (serviceID: String) -> Unit
  ) :
    RecyclerView.Adapter<BaseViewHolder<*>>() {

    var data: MutableList<DataEntry> = mutableListOf()
    val routes = mainActivity.appModel.routes

    override fun getItemCount() = data.size

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) =
      (holder as BaseViewHolder<DataEntry>).onBind(data[position])


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> =
      when (@ViewType viewType) {
        STOP -> StopViewHolder(
          ViewholderStopBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
          )
        ).also {
          it.itemView.setOnClickListener {
            onStopClicked.invoke()
          }
        }

        NOTICE -> NoticeViewHolder(parent)
        SERVICE -> ServiceViewHolder(
          ViewholderServiceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
          ),
          routes
        ).apply {
          itemView.setOnClickListener {
            onServiceClicked.invoke(departure.serviceID)
          }
        }
        DAY_START -> DayStartViewHolder(parent)
        NO_DEPARTURES -> NoDeparturesViewHolder(parent)
        else -> throw Exception("Invalid viewType: $viewType")
      }

    override fun getItemViewType(position: Int) = data[position].type

    private var lastUpdate: String? = null

    fun setDepartures(stop: Stop, departures: StopDepartures, routes: List<Route>?) {
      log.trace("setDepartures() routeCount: ${routes?.size}")
      if (departures.etag == lastUpdate) {
        log.debug("ignoring update for ${stopCode}")
        return
      }

      lastUpdate = departures.etag

      val newData = mutableListOf<DataEntry>()
      newData.add(DataEntry.Stop(stop, departures.fareZone))
/*TODO       departures.notices?.forEach {
        newData.add(DataEntry.Notice(it))
      }*/

      if (departures.departures.isNullOrEmpty()) {
        newData.add(DataEntry.NoDepartures)
      } else {

        val dueDateCalendar = Calendar.getInstance()
        var prevDay = dueDateCalendar.get(Calendar.DAY_OF_YEAR)

        departures.departures.forEach { departure ->
          //log.info("departure: $departure")
          val dueDate =
            departure.arrival.expected ?: departure.arrival.aimed ?: departure.departure.expected
            ?: departure.departure.aimed!!


          val dueDay = dueDateCalendar.let {
            it.time = dueDate
            it.get(Calendar.DAY_OF_YEAR)
          }

          if (dueDay != prevDay) {
            newData.add(DataEntry.DayStart(dueDate))
            prevDay = dueDay
          }


          newData.add(DataEntry.Service(departure))
        }
      }


      object : DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
          data[oldItemPosition] == newData[newItemPosition]

        override fun getOldListSize() = data.size

        override fun getNewListSize() = newData.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
          data[oldItemPosition] == newData[newItemPosition]

      }.also {

        val result = DiffUtil.calculateDiff(it)
        data = newData
        result.dispatchUpdatesTo(this@StopDeparturesAdapter)
      }

    }

  }

  class DayStartViewHolder(parent: ViewGroup) :
    StopDeparturesFragment.BaseViewHolder<DataEntry.DayStart>(
      LayoutInflater.from(parent.context).inflate(
        R.layout.viewholder_day_start,
        parent,
        false
      ) as ViewGroup
    ) {


    @SuppressLint("SimpleDateFormat")
    override fun onBind(data: DataEntry.DayStart) {
      val departureDate = data.date
      val cal = Calendar.getInstance()
      cal.time = departureDate
      val day = cal.get(Calendar.DAY_OF_MONTH)

      val daySuffix: ((n: Int) -> String) = { n ->
        if (n >= 11 && n <= 13) {
          "th"
        } else
          when (n % 10) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
          }
      }


      var s = SimpleDateFormat("EEE d").format(departureDate) + daySuffix(day) + ' '.toString()
      s += SimpleDateFormat("MMM").format(departureDate)
      itemView.findViewById<TextView>(R.id.date).text = s
    }


  }


  class NoDeparturesViewHolder(parent: ViewGroup) :
    StopDeparturesFragment.BaseViewHolder<DataEntry.NoDepartures>(
      LayoutInflater.from(parent.context).inflate(
        R.layout.viewholder_day_start,
        parent,
        false
      ) as ViewGroup
    ) {

    override fun onBind(data: DataEntry.NoDepartures) {
      itemView.findViewById<TextView>(R.id.date).text =
        itemView.context.getString(R.string.msg_no_departures)
    }
  }

  class StopViewHolder(val binding: ViewholderStopBinding) :
    StopDeparturesFragment.BaseViewHolder<DataEntry.Stop>(binding.root) {


    private lateinit var imageURL: String
    override fun onBind(data: DataEntry.Stop) {
      val stop = data.stop


      binding.stopUpdated.visibility = View.GONE
      /*    "Updated: ${
            itemView.context.timeFormat.format(departures.lastModified)
              .toLowerCase(Locale.getDefault())
          }"
  */


      binding.stopName.text = stop.name
      binding.stopDetail.text =
        itemView.context.getString(R.string.lbl_stop_detail, stop.code, data.fareZone)

//      val size = 120

      val imageScale = 0.6
      val imageX = (itemView.resources.displayMetrics.xdpi*imageScale).toInt()
      val imageY = (itemView.resources.displayMetrics.ydpi*imageScale).toInt()

      log.trace("image dimensions: ${imageX}x${imageY}")
      imageURL =
        "https://maps.googleapis.com/maps/api/streetview?size=${imageX}x${imageY}&location=" +
            "${stop.latitude},${stop.longitude}&pitch=-0.76&key=${itemView.context.getString(R.string.google_street_view_key)}"

      if (BuildConfig.DEBUG) {
        log.debug("imageURL $imageURL")
      }

      binding.stopImage.visibility = View.VISIBLE

      GlideApp.with(itemView.context)
        .load(imageURL)
        .transition(DrawableTransitionOptions.withCrossFade())
        .apply(RequestOptions.bitmapTransform(RoundedCorners(8)).apply {
          //placeholder(R.drawable.ic_bus_blue)
          fallback(R.drawable.ic_bus_blue)
        })
        .into(object : DrawableImageViewTarget(binding.stopImage) {
          override fun onLoadFailed(errorDrawable: Drawable?) {
            super.onLoadFailed(errorDrawable)
            binding.stopImage.visibility = View.GONE
          }
        })

    }


  }


  private class NoticeViewHolder(parent: ViewGroup) :
    StopDeparturesFragment.BaseViewHolder<DataEntry.Notice>(
      LayoutInflater.from(parent.context).inflate(
        R.layout.viewholder_notice,
        parent,
        false
      ) as ViewGroup
    ) {


    override fun onBind(data: DataEntry.Notice) {
      var msg = data.notice.lineNote
      val i = msg.indexOf("See metlink")
      if (i > -1) msg = msg.substring(0, i)
      itemView.findViewById<TextView>(R.id.notice_text).text = msg
    }

  }

  private class ServiceViewHolder(
    val binding: ViewholderServiceBinding,
    val routes: LiveData<List<Route>>
  ) :
    StopDeparturesFragment.BaseViewHolder<DataEntry.Service>(binding.root) {

    private val timeFormat = DateFormat.getTimeFormat(itemView.context).also {
      it.timeZone = TimeZone.getTimeZone("NZ")
    }

    lateinit var timetableURL: String
    lateinit var departure: StopDepartures.Departure


    override fun onBind(data: DataEntry.Service) {
      departure = data.service

      itemView.findViewById<TextView>(R.id.service_code).text = departure.stopID
      timetableURL = "https://metlink.co.nz/stop/${departure.stopID}"
      //log.debug("service: $service ${timetableURL}")


      itemView.findViewById<View>(R.id.vehicle_feature).visibility =
        when (departure.wheelChairAccessible) {
          true -> View.VISIBLE
          else -> View.INVISIBLE
        }

      val cancelled = departure.status == StopDepartures.DepartureStatus.CANCELLED


      binding.serviceCode.text = departure.serviceID


      binding.routeName.text =
        when (departure.status) {
          StopDepartures.DepartureStatus.CANCELLED -> "${departure.destination.name} Cancelled"
          else -> departure.destination.name
        }



      binding.serviceRow.alpha = if (cancelled) 0.4f else 1f

      binding.serviceCode.setBackgroundColor(R.color.colorPrimaryLight.toResourceColour(itemView.context))
      binding.serviceCode.setTextColor(Color.WHITE)


      routes.value?.firstOrNull { it.routeShortName == data.service.serviceID }?.also {
        it.routeColor?.also {
          runCatching {
            Color.parseColor("#$it").also {
              binding.serviceCode.setBackgroundColor(it)
            }
          }
        }

        it.routeTextColor?.also {
          runCatching {
            Color.parseColor("#$it").also {
              binding.serviceCode.setTextColor(it)
            }
          }
        }
      }

      val dueDate =
        departure.arrival.expected ?: departure.arrival.aimed ?: departure.departure.expected
        ?: departure.departure.aimed!!

      val now = System.currentTimeMillis()

      val minutes = (dueDate.time - now) / 60000.0
      //TODO  ((service.displayDeparture.time - model.stopDepartures.value!!.lastModified.time) / 60000f).toInt()

      val isDue = minutes <= 2
/*      if (isDue) {//service.isRealtime) {
        binding.due.setBackgroundColor(
          ResourceUtils.getThemeColour(
            itemView.context,
            R.attr.colorAccent
          )
        )
//        due.layoutParams = LinearLayout.LayoutParams(80, LinearLayout.LayoutParams.MATCH_PARENT)
      } else {
        *//*val b = TypedValue()
        activity.theme.resolveAttribute(android.R.attr.windowBackground,b,true)
        due.background = ResourcesCompat.getDrawable(activity.resources,b.resourceId,activity.theme)*//*
        *//*     binding.due.layoutParams = LinearLayout.LayoutParams(
               ViewGroup.LayoutParams.WRAP_CONTENT,
               ViewGroup.LayoutParams.MATCH_PARENT
             )*//*
        binding.due.setBackgroundColor(0)
      }*/

      binding.due.text =
        when(departure.status){
          StopDepartures.DepartureStatus.CANCELLED -> "Cancelled"
          StopDepartures.DepartureStatus.NOSTATUS -> timeFormat.format(dueDate).toLowerCase(Locale.getDefault())
          else ->if (isDue) "Due" else if (minutes <= 30) "${minutes.toInt()} mins" else timeFormat.format(dueDate).toLowerCase(Locale.getDefault())
        }

    }


  }

}

