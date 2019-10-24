package hll.zpf.starttravel.common.database.entity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.*
import hll.zpf.starttravel.common.Utils
import java.util.*

@Entity(tableName = "member",
    primaryKeys = ["id","travel_id"],
    foreignKeys = [ForeignKey(entity = Travel::class,
        parentColumns = ["id"],
        childColumns = ["travel_id"])])
data class Member(
    var id: String,
    @ColumnInfo(name = "name")
    var name: String?,
    @ColumnInfo(name = "image")
    var image: ByteArray?,
    @ColumnInfo(name = "travel_id")
    var travelId: String
) {
    @Ignore
    var money: Float? = 0f
    companion object {
        private var oldDataStr = ""
        fun createMember():Member{
            val utils = Utils.instance()
            var dateString = utils.getDateStringByFormat("", null)
            if(oldDataStr == dateString){
                dateString = utils.dateCalculate(dateString,1, Calendar.MILLISECOND) ?: "20190711000000000"
            }
            oldDataStr = dateString
            val memberId = "M$dateString"
            return Member(memberId,null,null,"")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Member

        if (id != other.id) return false
        if (name != other.name) return false
        if (money != other.money) return false
        if (travelId != other.travelId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (money?.hashCode() ?: 0)
        result = 31 * result + (image?.contentHashCode() ?: 0)
        result = 31 * result + travelId.hashCode()
        return result
    }

    fun getImageBitmap(): Bitmap? {

        return if (image == null) {
            null
        } else BitmapFactory.decodeByteArray(image, 0, image!!.size) ?: return null
    }

    fun setImageBitmap(icon: Bitmap?) {
        icon?.let {
            this.image = Utils().bitmapToBytes(it)
        }

    }
}