package hll.zpf.starttravel.base

import android.app.Application
import hll.zpf.starttravel.common.database.DaoMaster
import hll.zpf.starttravel.common.database.DaoSession

class BaseApplication:Application() {

    val DB_NAME = "travel.db"

    lateinit var daoSession: DaoSession


    companion object {
        var application:BaseApplication? = null
    }

    override fun onCreate() {
        super.onCreate()
        initGreenDao()
    }

    private fun initGreenDao(){
        val helper = DaoMaster.DevOpenHelper(this, DB_NAME)
        val db = helper.writableDatabase
        val daoMaster =  DaoMaster(db)
        daoSession = daoMaster.newSession()
    }

    fun getmDaoSession() : DaoSession {
        return daoSession
    }
}