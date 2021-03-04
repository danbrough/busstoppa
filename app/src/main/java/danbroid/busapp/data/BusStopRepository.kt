package danbroid.busapp.data

import android.content.Context
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BusStopRepository(val context: Context) {

  val db by lazy { BusStopDatabase.getInstance(context) }

  suspend fun addStop(it: BusStop) = withContext(Dispatchers.IO) {
    db.stopDAO.addStop(it)
  }


  val allStopsPaged by lazy {
    LivePagedListBuilder(
      db.stopDAO.getAllStopsByCode(), PagedList.Config.Builder()
        .setPageSize(30)
        .setEnablePlaceholders(true)
        .build()
    ).build()
  }


}
