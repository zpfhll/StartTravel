package hll.zpf.starttravel.common.database

import hll.zpf.starttravel.base.BaseActivity
import hll.zpf.starttravel.base.BaseApplication
import hll.zpf.starttravel.common.HLogger
import hll.zpf.starttravel.common.database.entity.Member
import hll.zpf.starttravel.common.database.entity.Travel
import hll.zpf.starttravel.common.database.entity.User
import java.lang.Exception

class DataManager {

    fun getUserCount():Long{
        return if(BaseApplication.application == null ) 0 else BaseApplication.application!!.daoSession.userDao.count()
    }

    fun insertUser(user: User):Long{
        val daoSession = BaseApplication.application?.daoSession
        daoSession?.let {
            var result = -1L
            try {
                result = it.userDao.insert(user)
                HLogger.instance().e("insertUser","$result")
            }catch (e:Exception){
                HLogger.instance().e("insertUser","insert user fail : ${e.message}")
            }
            return result
        }
        return -1L
    }

    fun getUserByID(userId:String) : User?{
        val daoSession = BaseApplication.application?.daoSession
        var result:User? = null
        daoSession?.let {
            try {
                result = it.userDao.queryRaw("where id = ?",userId)[0]
                HLogger.instance().e("getUserByID","$result")
            }catch (e:Exception){
                HLogger.instance().e("getUserByID","get user fail : ${e.message}")
            }
            return result
        }
        return result
    }


    /**
     * 获取未完成的旅行
     */
    fun getNotEndTravel():MutableList<Travel>?{
        val daoSession = BaseApplication.application?.daoSession
        var travels:MutableList<Travel>? =  null
        daoSession?.let {
            travels = it.travelDao.queryRaw("WHERE state IN (?,?,?) ORDER BY id DESC","0","1","2")
        }
        return travels
    }

    /**
     * 插入旅行数据
     */
    fun insertOrReplaceTravel(travel: Travel):Long{
        val daoSession = BaseApplication.application?.daoSession
        daoSession?.let {
            var result = -1L
            try {
                result = it.travelDao.insertOrReplace(travel)
                HLogger.instance().e("insertOrReplaceTravel","$result")
            }catch (e:Exception){
                HLogger.instance().e("insertOrReplaceTravel","insert travel fail : ${e.message}")
            }
            return result
        }
        return -1L
    }

    /**
     * 批量插入partner
     */
    fun insertMembers(members: List<Member>):Long{
        if(members.size < 1){
            return 0
        }
        val daoSession = BaseApplication.application?.daoSession
        daoSession?.let {
            var result = -1L
            try {
                if(members.size > 1) {
                    it.memberDao.insertInTx(members)
                    result = 0
                }else{
                    result = it.memberDao.insert(members.get(0))
                }
                HLogger.instance().e("insertMembers","$result")
            }catch (e:Exception){
                HLogger.instance().e("insertMembers","insert members fail : ${e.message}")
            }
            return result
        }
        return -1L
    }



}