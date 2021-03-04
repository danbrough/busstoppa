package danbroid.busapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(entities = [BusStop::class], exportSchema = true, version = 1)
abstract class BusStopDatabase : RoomDatabase() {

  abstract val stopDAO: BusStopDAO

  companion object {

    @Volatile
    private var instance: BusStopDatabase? = null

    fun getInstance(context: Context): BusStopDatabase {
      return instance ?: synchronized(this) {
        instance ?: buildDatabase(context).also { instance = it }
      }
    }


    private fun buildDatabase(context: Context) =
      Room.databaseBuilder(context, BusStopDatabase::class.java, "busstoppa.db")
        .addMigrations(object : Migration(0, 1) {
          override fun migrate(database: SupportSQLiteDatabase) {
            log.error("MIGRATING DATABASE!@@@@")
          }

        })
        .build()


    /*
     private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>().build()
                            WorkManager.getInstance().enqueue(request)
                        }
                    })
                    .build()
        }
     */

  }
}

private val log = org.slf4j.LoggerFactory.getLogger(BusStopDatabase::class.java)
