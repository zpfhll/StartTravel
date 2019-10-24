package hll.zpf.starttravel.common.database.entity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import hll.zpf.starttravel.common.Utils
import java.util.*

@Entity(tableName = "user")
data class User (
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id : String,
    @ColumnInfo(name ="mail_address")
    var mailAddress : String?,
    @ColumnInfo(name ="name")
    var name : String?,
    @ColumnInfo(name ="introduce")
    var introduce : String?,
    @ColumnInfo(name ="image")
    var image : ByteArray?,
    @ColumnInfo(name ="visitor")
    var  isVisitor : Boolean
) {

    companion object {
        private var oldDataStr = ""
        fun createUser():User{
            val utils = Utils.instance()
            var dateString = utils.getDateStringByFormat("", null)
            if(oldDataStr == dateString){
                dateString = utils.dateCalculate(dateString,1, Calendar.MILLISECOND) ?: "20190711000000000"
            }
            oldDataStr = dateString
            val userId = "U$dateString"
            return User(userId,null,null,null,null,true)
        }
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false
        if (mailAddress != other.mailAddress) return false
        if (name != other.name) return false
        if (introduce != other.introduce) return false
        if (isVisitor != other.isVisitor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + mailAddress.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + introduce.hashCode()
        result = 31 * result + (image?.contentHashCode() ?: 0)
        result = 31 * result + isVisitor.hashCode()
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
