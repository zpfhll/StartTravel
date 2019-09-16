package hll.zpf.starttravel.common.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hll.zpf.starttravel.common.database.entity.Detail

@Dao
interface DetailDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDetail(vararg details: Detail) : List<Long>

    @Query(value = "select * from detail")
    fun loadAllDetail(): List<Detail>?

    @Query(value = "select * from detail where travel_id = :travelId")
    fun getDetailByTravelId(travelId:String): List<Detail>?
}