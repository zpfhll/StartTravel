package hll.zpf.starttravel.common.database.entity

import androidx.room.*
import hll.zpf.starttravel.common.Utils

@Entity(tableName = "detail_member_join",
        foreignKeys = [ForeignKey(entity = Member::class,
                            parentColumns = ["id"],
                            childColumns = ["member_id"]),
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
        fun createDetailWithMember():DetailWithMember{
            val dateString = Utils.instance().getDateStringByFormat("", null)
            val detailWithMemberId = "DM$dateString"
            return DetailWithMember(detailWithMemberId,"",0,"",0f,"")
        }
    }
}