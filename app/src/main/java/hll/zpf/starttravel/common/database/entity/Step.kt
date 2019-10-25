package hll.zpf.starttravel.common.database.entity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import hll.zpf.starttravel.common.Utils
import java.util.*

@Entity(tableName = "step",
    foreignKeys = [ForeignKey(entity = Travel::class,
        parentColumns = ["id"],
        childColumns = ["travel_id"])])
data class Step (
    @PrimaryKey
    @ColumnInfo(name = "id")
    var  id :String,
    @ColumnInfo(name = "name")
    var  name :String,
    @ColumnInfo(name = "memo")
    var  memo :String?,
    @ColumnInfo(name = "start_date")
    var  startDate :String,
    @ColumnInfo(name = "latitude")
    var latitude:Float,
    @ColumnInfo(name = "longitude")
    var longitude:Float,
    @ColumnInfo(name = "image")
    var image : ByteArray?,
    @ColumnInfo(name = "travel_id")
    var  travelId :String
) {

    companion object {
        private var oldDataStr = ""
        fun createStep():Step{
            val utils = Utils.instance()
            var dateString = utils.getDateStringByFormat("", null)
            if(oldDataStr == dateString){
                dateString = utils.dateCalculate(dateString,1, Calendar.MILLISECOND) ?: "20190711000000000"
            }
            oldDataStr = dateString
            val steoId = "M$dateString"
            return Step(steoId,"",null,"",0f,0f,null,"")
        }
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Step
        if (id != other.id) return false
        if (name != other.name) return false
        if (memo != other.memo) return false
        if (startDate != other.startDate) return false
        if (latitude != other.latitude) return false
        if (longitude != other.longitude) return false
        if (travelId != other.travelId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (memo?.hashCode() ?: 0)
        result = 31 * result + startDate.hashCode()
        result = 31 * result + latitude.hashCode()
        result = 31 * result + longitude.hashCode()
        result = 31 * result + (image?.contentHashCode() ?: 0)
        result = 31 * result + travelId.hashCode()
        return result
    }

    fun getImageBitmap(): Bitmap? {

        return if (image == null) {
            null
        } else BitmapFactory.decodeByteArray(image, 0, image!!.size) ?: return null
    }

    fun setImageBitmap(icon: Bitmap) {
        this.image = Utils().bitmapToBytes(icon)
    }
}