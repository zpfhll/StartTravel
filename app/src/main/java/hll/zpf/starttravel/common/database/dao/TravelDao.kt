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
    @Query("SELECT " +
            "id," +
            "name," +
            "memo," +
            "start_date," +
            "end_date," +
            "money," +
            "state," +
            "image," +
            "type," +
            "user_id," +
            "COUNT(member_id) AS memberCount," +
            "out_money AS outMoney " +
            "FROM travel " +
            "LEFT JOIN (SELECT " +
            "     id AS member_id," +
            "     out_money," +
            "     detail_travel_id " +
            "     FROM" +
            "     member " +
            "     LEFT JOIN (" +
            "          SELECT " +
            "          SUM(detail.[money]) AS out_money," +
            "          detail.travel_id AS detail_travel_id " +
            "          FROM detail " +
            "           WHERE detail.[type] = 1 " +
            "           GROUP BY travel_id) " +
            "     ON member.travel_id = detail_travel_id ) " +
            "ON travel.id = detail_travel_id " +
            "WHERE user_id = :userId AND state IN (0,1,2) " +
            "GROUP BY travel.id " +
            "ORDER BY id DESC")
    fun getNotEndTravel(userId : String):List<Travel>?
}