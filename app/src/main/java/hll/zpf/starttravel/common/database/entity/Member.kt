package hll.zpf.starttravel.common.database.entity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import hll.zpf.starttravel.common.Utils

@Entity(tableName = "member",
    foreignKeys = [ForeignKey(entity = Travel::class,
        parentColumns = ["id"],
        childColumns = ["travel_id"])])
data class Member(
    @PrimaryKey
    var id: String,
    @ColumnInfo(name = "name")
    var name: String?,
    @ColumnInfo(name = "money")
    var money: Float?,
    @ColumnInfo(name = "image")
    var image: ByteArray?,
    @ColumnInfo(name = "travel_id")
    var travelId: String?
) {

    companion object {
        fun createMember():Member{
            val memberId = "M${Utils.instance().getDateStringByFormat("", null)}"
            return Member(memberId,null,null,null,null)
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