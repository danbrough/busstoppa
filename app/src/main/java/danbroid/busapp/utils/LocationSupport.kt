package danbroid.busapp.utils

import android.content.Context
import android.location.Location
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent

class LocationSupport(
  private val context: Context,
  private val lifecycle: Lifecycle,
  private val callback: ((Location) -> Unit)
) {

  var enabled: Boolean = false
    set(value) {
      field = value
      if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
        if (field) start() else stop()
      }
    }

  @OnLifecycleEvent(Lifecycle.Event.ON_START)
  private fun start() {
    log.error("START enabled: $enabled !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ")
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
  private fun stop() {
    log.error("STOP enabled: $enabled  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
  }


}

private val log = org.slf4j.LoggerFactory.getLogger(LocationSupport::class.java)