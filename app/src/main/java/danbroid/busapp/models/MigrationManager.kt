package danbroid.busapp.models

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import danbroid.busapp.metlinkold.metlinkOld
import danbroid.busapp.repository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


internal class MigrationManager(val appModel: AppModel) {
  val context = appModel.context

  val oldDB = "busapp.db"
  val dbFile = context.getDatabasePath(oldDB)

  init {
    migrateData()
  }

  private fun migrateData() =
    GlobalScope.launch {
      if (dbFile.exists())
        object : SQLiteOpenHelper(context, oldDB, null, 1000) {
          override fun onCreate(db: SQLiteDatabase?) {
          }

          override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            peformUpdate(db)
          }
        }.writableDatabase
    }

  private  fun peformUpdate(db: SQLiteDatabase) = GlobalScope.launch {
    log.warn("performUpdate()")

    db.rawQuery("SELECT * from t_stop_attributes", null).also {
      while (it.moveToNext()) {
        var stopCode = it.getString(0)
        if (stopCode.startsWith("WN_")) stopCode = stopCode.substring(3)
        val accessCount = it.getInt(1)
        log.warn("adding favourite stop $stopCode")

        runBlocking {
          metlinkOld.stopInfo(stopCode).also {
            //it.accessCount = accessCount
            log.trace("adding stop to stopDAO")
            context.repository.db.stopDAO.addStop(it)
          }
        }
      }
    }.close()

    context.deleteDatabase(oldDB)
    log.warn("FINISHED MIGRATION stopCount: ${appModel.recentStops.value?.size}")


  }


}


private val log = org.slf4j.LoggerFactory.getLogger(MigrationManager::class.java)


