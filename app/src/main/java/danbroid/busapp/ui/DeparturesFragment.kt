package danbroid.busapp.ui

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import danbroid.busapp.NavGraph
import danbroid.busapp.R
import danbroid.busapp.activities.appModel
import danbroid.busapp.data.BusStop
import danbroid.busapp.databinding.DeparturesFragmentBinding
import danbroid.util.resource.toResourceColour
import kotlinx.coroutines.launch


class DeparturesFragment : Fragment() {

  companion object {
    fun create(stopCode: String) = DeparturesFragment().also {
      it.arguments = bundleOf(NavGraph.arg.stopCode to stopCode)
    }
  }

  val stopCode: String
    get() = requireArguments().getString(NavGraph.arg.stopCode)!!

  private lateinit var pagerAdapter: DeparturesPagerAdapter

  private var _binding: DeparturesFragmentBinding? = null
  private val binding: DeparturesFragmentBinding
    get() = _binding!!


  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ) = DeparturesFragmentBinding.inflate(inflater, container, false).let {
    _binding = it
    it.root
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.departures, menu)
  }

  fun removeCurrentStop() {
    val stop = pagerAdapter.stops.get(binding.pager.currentItem)
    log.info("removeCurrentStop() $stop")
    appModel.removeStop(stop)
    showSnackBar("Stop ${stop.code} removed") {
      setActionTextColor(R.color.colorAccent.toResourceColour(requireContext()))
      setAction(R.string.lbl_undo) {
        appModel.addStop(stop)
      }
    }
  }

  override fun onOptionsItemSelected(item: MenuItem) =
    when (item.itemId) {
      R.id.action_remove -> {
        removeCurrentStop()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    log.info("onViewCreated() ${stopCode}")
    pagerAdapter = DeparturesPagerAdapter(this)
    binding.pager.adapter = pagerAdapter


    TabLayoutMediator(binding.tabs, binding.pager) { tab, position ->
      //tab.text = "OBJECT ${(position + 1)}"
      tab.text = pagerAdapter.getStopTitle(position)
    }.attach()


    appModel.recentStops.observe(viewLifecycleOwner, { recentStops ->

      val stopIndex = recentStops.indexOfFirst { it.code == stopCode }
      log.trace("recent stops changed: index: $stopIndex code: $stopCode size: ${recentStops.size}")
      if (stopIndex == -1) {
        log.error("stop $stopCode not found in recent stops")
      } else {
        pagerAdapter.setData(recentStops)
        view.post {
          binding.pager.currentItem = stopIndex
        }
      }

    })


    binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
      override fun onPageSelected(position: Int) {
        log.trace("onPageSelected() $position")
      }
    })
  }


}

class DeparturesPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

  var stops: List<BusStop> = emptyList()

  class DiffCallback(val oldData: List<BusStop>, val newData: List<BusStop>) : DiffUtil.Callback() {
    override fun getOldListSize() = oldData.size

    override fun getNewListSize() = newData.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
      oldData[oldItemPosition].id == newData[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
      areItemsTheSame(oldItemPosition, newItemPosition)
  }

  fun getStopTitle(position: Int) = stops.get(position).name

  fun setData(newStops: List<BusStop>) {
    log.trace("setData() oldCount: ${stops.size} newCount: ${newStops.size}")
    val diff = DiffCallback(stops, newStops)
    stops = newStops
    DiffUtil.calculateDiff(diff, false).dispatchUpdatesTo(this)
  }

  //override fun getPageTitle(position: Int) = stops[position].name
  override fun getItemCount() = stops.size

  override fun createFragment(position: Int): Fragment {
    val stop = stops[position]
    log.trace("creating StopDeparturesFragment position $position code: ${stop.code}")
    return StopDeparturesFragment().also {
      it.arguments = bundleOf(NavGraph.arg.stopCode to stop.code)
      log.trace("created stopdeparturesfragment with stopCode: ${it.arguments?.get(NavGraph.arg.stopCode)}")
    }
  }


}

private val log = org.slf4j.LoggerFactory.getLogger(DeparturesPagerAdapter::class.java)