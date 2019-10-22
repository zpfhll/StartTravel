package hll.zpf.starttravel.common.database.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hll.zpf.starttravel.common.database.entity.Detail
import hll.zpf.starttravel.common.database.entity.DetailWithMember

@Dao
interface DetailWithMemberDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDM(vararg detailMembers: DetailWithMember) : List<Long>

    @Query(value = "select * from detail_member_join")
    fun loadAllDetailMember(): List<DetailWithMember>?

    @Query(value = "SELECT  " +
            " detail_member_join.id,  " +
            " detail_member_join.member_id,  " +
            "detail_member_join.member_type, " +
            "detail_member_join.money, " +
            "detail_member_join.travel_id, " +
            "detail_member_join.detail_id, " +
            "member.name AS memberName " +
            "FROM " +
            "detail_member_join " +
            "LEFT JOIN member ON member_id = member.id  AND detail_member_join.travel_id = member.travel_id " +
            "WHERE detail_id = :detailId ORDER BY member_type ASC")
    fun loadDetailMemberByDetail( detailId : String) : Cursor
}