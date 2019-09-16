package hll.zpf.starttravel.common.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hll.zpf.starttravel.common.database.entity.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(vararg users: User) : List<Long>

    @Query("select * from user")
    fun loadAllUser(): List<User>?

    @Query("select * from user where id = :userId")
    fun getUserByID(userId:String) : User?
}