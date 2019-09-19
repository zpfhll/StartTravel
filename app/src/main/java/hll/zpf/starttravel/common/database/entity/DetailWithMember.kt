package hll.zpf.starttravel.common.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

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
    var id: Long,
    @ColumnInfo(name = "member_id")
    var memberId: String,
    @ColumnInfo(name = "detail_id")
    var detailId: String
)