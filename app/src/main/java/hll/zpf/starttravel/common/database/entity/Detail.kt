package hll.zpf.starttravel.common.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import hll.zpf.starttravel.common.Utils

@Entity(tableName = "detail")
data class Detail(
    @PrimaryKey
    var id: String,
    @ColumnInfo(name = "memo")
    var memo: String,
    @ColumnInfo(name = "date")
    var date: String,
    /**
     * 0:汇入 1:支出
     */
    @ColumnInfo(name = "type")
    var type: Int,
    @ColumnInfo(name = "money")
    var money: Float?,
    @ColumnInfo(name = "travel_id")
    var travelId: String
){



    companion object {
        fun createDetail():Detail{
            val dateString = Utils.instance().getDateStringByFormat("", null)
            val detailId = "D$dateString"
            return Detail(detailId,"",dateString,1,0f,"")
        }
    }
}

