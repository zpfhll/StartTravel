package hll.zpf.starttravel.base

import android.app.Application
import androidx.room.Room
import hll.zpf.starttravel.common.database.TravelDatabase

class BaseApplication:Application() {

    val DB_NAME = "travel.db"

    lateinit var travelDatabase:TravelDatabase


    companion object {
        var application:BaseApplication? = null
    }

    override fun onCreate() {
        super.onCreate()
        initGreenDao()
    }

    private fun initGreenDao(){
        travelDatabase = Room.databaseBuilder(this, TravelDatabase::class.java, "travel_room.db").build()
    }

}