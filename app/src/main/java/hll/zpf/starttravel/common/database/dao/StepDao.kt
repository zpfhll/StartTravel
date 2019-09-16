package hll.zpf.starttravel.common.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hll.zpf.starttravel.common.database.entity.Step

@Dao
interface StepDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStep(vararg steps: Step) : List<Long>

    @Query(value = "select * from step")
    fun loadAllStep(): List<Step>?

    @Query(value = "select * from step where travel_id = :travelId")
    fun getStepByTravelId(travelId:String): List<Step>?
}