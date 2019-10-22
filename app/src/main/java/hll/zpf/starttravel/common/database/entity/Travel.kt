package hll.zpf.starttravel.common.database.entity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.*
import hll.zpf.starttravel.common.Utils

@Entity(tableName = "travel",
        foreignKeys = [ForeignKey(entity = User::class,
                                  parentColumns = ["id"],
                                  childColumns = ["user_id"])])
data class Travel (
    @PrimaryKey
    @ColumnInfo(name ="id")
    var id : String,
    @ColumnInfo(name ="name")
    var name : String?,
    @ColumnInfo(name ="memo")
    var memo : String?,
    @ColumnInfo(name ="start_date")
    var startDate : String?,
    @ColumnInfo(name ="end_date")
    var endDate : String?,
    /**
     * 0:未启程 1：旅途中 2：结束未清算完成 3：结束并且清算完成
     */
    @ColumnInfo(name ="state")
    var state : Int,
    @ColumnInfo(name ="image")
    var image : ByteArray?,
    /**
     * タイプ　０：记录花销　1: 随心旅行 2：情報なし
     */
    @ColumnInfo(name ="type")
    var type :Int,
    @ColumnInfo(name ="user_id")
    var userId : String?,
    @Ignore
    var memberCount:Int?,
    @Ignore
    var outMoney:Float?,
    @Ignore
    var inMoney:Float?
) {
    constructor():this("",null,null,null,null,0,null,0,null,0,0f,0f)

    companion object {
        fun createTravel():Travel{
            val userId = "T${Utils.instance().getDateStringByFormat("", null)}"
            return Travel(userId,null,null,null,null,0,null,0,null,0,0f,0f)
        }
    }

    fun getImageBitmap(): Bitmap? {

        return if (image == null) {
            null
        } else BitmapFactory.decodeByteArray(image, 0, image!!.size) ?: return null
    }

    fun setImageBitmap(icon: Bitmap) {
        this.image = Utils().bitmapToBytes(icon)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Travel

        if (id != other.id) return false
        if (name != other.name) return false
        if (memo != other.memo) return false
        if (startDate != other.startDate) return false
        if (endDate != other.endDate) return false
        if (state != other.state) return false
        if (type != other.type) return false
        if (userId != other.userId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (memo?.hashCode() ?: 0)
        result = 31 * result + (startDate?.hashCode() ?: 0)
        result = 31 * result + (endDate?.hashCode() ?: 0)
        result = 31 * result + state
        result = 31 * result + (image?.contentHashCode() ?: 0)
        result = 31 * result + type
        result = 31 * result + (userId?.hashCode() ?: 0)
        return result
    }
}