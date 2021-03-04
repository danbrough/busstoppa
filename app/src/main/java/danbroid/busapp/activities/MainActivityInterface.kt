package danbroid.busapp.activities

import android.content.Context
import danbroid.busapp.models.AppModel

interface MainActivityInterface {
  fun showBrowser(url:String)
  fun showMap(stopCode:String? = null)
  fun addToHistoryAndShowDepartures(code: String)
  val context:Context
  val appModel:AppModel
}