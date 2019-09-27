package hll.zpf.starttravel.common.database.dao

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
}