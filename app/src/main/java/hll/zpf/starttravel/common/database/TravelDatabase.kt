package hll.zpf.starttravel.common.database

import androidx.room.Database
import androidx.room.RoomDatabase
import hll.zpf.starttravel.common.database.dao.*
import hll.zpf.starttravel.common.database.entity.*

@Database(entities = [User::class,Travel::class,Member::class,Detail::class,Step::class,DetailWithMember::class],version = 1)
abstract  class TravelDatabase:RoomDatabase() {
    abstract fun detailDao():DetailDao
    abstract fun userDao():UserDao
    abstract fun travelDao(): TravelDao
    abstract fun memberDao(): MemberDao
    abstract fun stepDao(): StepDao
}