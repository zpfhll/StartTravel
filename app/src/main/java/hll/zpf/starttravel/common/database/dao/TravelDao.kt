package hll.zpf.starttravel.common.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hll.zpf.starttravel.common.database.entity.Travel

@Dao
interface TravelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTravel(vararg travels: Travel) : List<Long>

    @Query("select * from travel")
    fun loadAllTravel(): List<Travel>?

    /**
     * 获取未完成的旅行
     */
    @Query("select * from travel where user_id = :userId and state in (0,1,2) ORDER BY id DESC")
    fun getNotEndTravel(userId : String):List<Travel>?
}