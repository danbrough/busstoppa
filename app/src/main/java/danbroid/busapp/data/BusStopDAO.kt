package danbroid.busapp.data

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*


@Dao
abstract class BusStopDAO {

  @Insert
  abstract fun _insert(busStop: BusStop): Long

  @Update(onConflict = OnConflictStrategy.REPLACE)
  abstract fun update(busStop: BusStop)

  @Update
  abstract fun update(stops: List<BusStop>)

  @Delete
  abstract fun delete(busStop: BusStop)

  @Query("SELECT * from t_stop ORDER BY `order`")
  abstract fun getAllStops(): LiveData<List<BusStop>>

  @Query("SELECT * FROM t_stop ORDER BY code DESC")
  abstract fun getAllStopsByCode(): DataSource.Factory<Int, BusStop>

  @Query("SELECT * FROM t_stop WHERE code = :code")
  abstract fun getStopByCode(code: String): BusStop?

  @Query("SELECT max(`order`) + 1 FROM t_stop")
  abstract fun getMaxStopOrder(): Int

  @Transaction
  open fun addStop(stop: BusStop, retainOrder: Boolean = false) {
    log.trace("addStop() $stop")

    getStopByCode(stop.code)?.let {
      log.debug("found existing stop $this shall update")
      // it.accessCount++
      update(it)
    } ?: run {
      if (!retainOrder)
        stop.order = getMaxStopOrder()
      _insert(stop)
    }
  }

  @Query("DELETE FROM t_stop")
  abstract fun deleteAllStops()

  @Insert
  abstract fun insert(stops: List<BusStop>)

  @Query("UPDATE t_stop SET `order` = `order` + 1 WHERE `order` >= :startOrder")
  abstract fun incrementOrdersAbove(startOrder: Int)

  @Transaction
  open fun moveStop(src: BusStop, dest: BusStop) {
    log.debug("moveStop() $src -> $dest")

    if (src.order < dest.order) {
      src.order = dest.order + 1
      incrementOrdersAbove(dest.order + 1)
    } else {
      src.order = dest.order
      incrementOrdersAbove(dest.order)
    }

    update(src)
  }

}

private val log = org.slf4j.LoggerFactory.getLogger(BusStopDAO::class.java)




