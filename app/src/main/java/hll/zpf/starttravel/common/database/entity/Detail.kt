package hll.zpf.starttravel.common.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "detail")
data class Detail(
    @PrimaryKey
    var id: String,
    @ColumnInfo(name = "memo")
    var memo: String,
    @ColumnInfo(name = "date")
    var date: String,
    @ColumnInfo(name = "money")
    var money: Float?,
    @ColumnInfo(name = "travel_id")
    var travelId: String
)