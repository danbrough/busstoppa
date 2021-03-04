package danbroid.busapp

import android.content.Context
import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.activity
import androidx.navigation.createGraph
import androidx.navigation.fragment.fragment
import danbroid.busapp.activities.MapActivity
import danbroid.busapp.ui.DeparturesFragment
import danbroid.busapp.ui.GoogleMapFragment
import danbroid.busapp.ui.StopsHistoryFragment

object NavGraph {


  private var id = 1
  private fun nextID() = id++

  object dest {
    val map = nextID()
    val map_fragment = nextID()

    val home = nextID()
    val departures = nextID()
  }

  object arg {
    const val stopCode = "stopCode"
  }

  object action {
    val showMap = nextID()
  }


}

/*
      if (isMap) {
        toolbarAnim = binding.appbar.animate().run {
          duration = 500
          alpha(0.5f)
          start()
          this
        }
      } else {
        //appbar.alpha = 1f
        toolbarAnim = binding.appbar.animate().run {
          duration = 500
          alpha(1f)
          start()
          this
        }
      }

 */
fun NavController.createBusAppNavGraph(context: Context,@IdRes  startDestination:Int = NavGraph.dest.home )=
  createGraph(startDestination = startDestination) {

    fragment<StopsHistoryFragment>(NavGraph.dest.home) {
      label = context.getString(R.string.lbl_history)
    }

/*    activity(NavGraph.dest.map){
      activityClass = MapActivity::class
    }*/
/*    fragment<StopDeparturesFragment>(NavGraph.dest.departures) {
      label =context.getString(R.string.title_departures)
      argument(NavGraph.arg.stopCode) {
        nullable = false
        type = NavType.StringType
      }
    }*/

   fragment<GoogleMapFragment>(NavGraph.dest.map) {
      argument(NavGraph.arg.stopCode) {
        nullable = true
        type = NavType.StringType
      }
    }

    fragment<DeparturesFragment>(NavGraph.dest.departures) {
      label =context.getString(R.string.title_departures)
      argument(NavGraph.arg.stopCode) {
        nullable = false
        type = NavType.StringType
      }
    }
  }

fun NavController.showDepartures(stopCode:String) = navigate(NavGraph.dest.departures, bundleOf(NavGraph.arg.stopCode to stopCode))
fun NavController.showMap(stopCode:String? = null) =
  navigate(NavGraph.dest.map, bundleOf(NavGraph.arg.stopCode to stopCode))



/**
setAction(R.string.lbl_undo) {
log.debug("undoing swipe")
//    stops.add(pos, stop)
model.addStop(stop, true)
// adapter.notifyItemInserted(pos)
}
 */
private val log = org.slf4j.LoggerFactory.getLogger(NavGraph::class.java)
