package danbroid.busapp.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import danbroid.busapp.gtfs.Stop
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable


@Entity(
  tableName = "t_stop",
  indices = [Index("code", unique = true)]
)

@Parcelize
@Serializable
data class BusStop(

  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "_id")
  val id: Long,

  @SerializedName("Name")
  var name: String,

  @SerializedName("Sms")
  val code: String,

  @SerializedName("Farezone")
  val farezone: String,

  @SerializedName("Lat")
  val lat: Double,

  @SerializedName("Long")
  val lng: Double,

  var order: Int

):Parcelable

fun Stop.toBusStop(): BusStop
= BusStop(id,name,code,this.zoneID,latitude,longitude,0)



