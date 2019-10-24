package hll.zpf.starttravel.common.database.entity

import androidx.room.*
import hll.zpf.starttravel.common.Utils
import java.util.*

@Entity(tableName = "detail_member_join",
        foreignKeys = [ForeignKey(entity = Member::class,
                            parentColumns = ["id","travel_id"],
                            childColumns = ["member_id","travel_id"]),
                      ForeignKey(entity = Detail::class,
                            parentColumns = ["id"],
                            childColumns = ["detail_id"])
        ])
data class DetailWithMember(
    @PrimaryKey
    var id: String,
    @ColumnInfo(name = "member_id")
    var memberId: String,
    @ColumnInfo(name = "member_type")
    var memberType: Int,//0:自己 1:伙伴
    @ColumnInfo(name = "detail_id")
    var detailId: String,
    @ColumnInfo(name = "money")
    var money: Float,
    @ColumnInfo(name = "travel_id")
    var travelId: String
){
    @Ignore
    var memberName :String = ""
    @Ignore
    var isSelected :Boolean = false

    companion object {
        private var oldDataStr = ""
        fun createDetailWithMember():DetailWithMember{
            val utils = Utils.instance()
            var dateString = utils.getDateStringByFormat("", null)
            if(oldDataStr == dateString){
                dateString = utils.dateCalculate(dateString,1,Calendar.MILLISECOND) ?: "20190711000000000"
            }
            oldDataStr = dateString
            val detailWithMemberId = "DM$dateString"
            return DetailWithMember(detailWithMemberId,"",0,"",0f,"")
        }
    }

    fun copyInitBy(name :String) : DetailWithMember{
        this.memberName = name
        return this
    }

}