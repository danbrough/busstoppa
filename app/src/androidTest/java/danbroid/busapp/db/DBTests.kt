package danbroid.busapp.db

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import danbroid.busapp.data.BusStopDatabase
import danbroid.busapp.metlink
import org.junit.After
import org.junit.Before
import org.junit.Test


private val log =
  org.slf4j.LoggerFactory.getLogger(DBTests::class.java)


class DBTests {

  lateinit var stopDB: BusStopDatabase


  val context: Context by lazy {
    ApplicationProvider.getApplicationContext<Context>()
  }


  @Before
  fun createDB() {
    log.warn("createDB()")
    val context: Context = ApplicationProvider.getApplicationContext()
    //stopDB = Room.inMemoryDatabaseBuilder(context, BusStopDatabase::class.java).build()
    stopDB = BusStopDatabase.getInstance(context)


  }

  @Test
  fun test1() {
    log.info("test1() ")

    stopDB.stopDAO.getAllStops().value?.forEach {
      log.error("GOT STOP $it")
    }


    var res = context.metlink.stopInfo("1116").execute()
    res?.body()?.let {
      log.info("got stop $it")
      stopDB.stopDAO.addStop(it)
    }
  }

  @After
  fun closeDB() {
    log.warn("closeDB()")
    stopDB?.close()
  }
}



