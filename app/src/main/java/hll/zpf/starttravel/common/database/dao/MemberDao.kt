package hll.zpf.starttravel.common.database.dao

import androidx.room.*
import hll.zpf.starttravel.common.database.entity.Member

@Dao
interface MemberDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMember(vararg members: Member) : List<Long>

    @Query(value = "select * from member")
    fun loadAllMember(): List<Member>?

    @Query(value = "select * from member where travel_id = :travelId")
    fun getMemberByTravelId(travelId:String): List<Member>?

}