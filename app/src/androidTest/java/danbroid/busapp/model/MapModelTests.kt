package danbroid.busapp.model

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import danbroid.busapp.models.MapModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.Test

class MapModelTests {
  val context: Context by lazy {
    ApplicationProvider.getApplicationContext<Context>()
  }


  @Test
  fun test1() {
    log.debug("test1()")
    GlobalScope.launch(Dispatchers.Main) {
      log.debug("creating map model ..")
      val model = MapModel(context)
    }
  }
}


private val log = org.slf4j.LoggerFactory.getLogger(MapModelTests::class.java)


