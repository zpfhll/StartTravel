package hll.zpf.starttravel.common.database

import hll.zpf.starttravel.base.BaseApplication
import hll.zpf.starttravel.common.HLogger
import hll.zpf.starttravel.common.UserData
import hll.zpf.starttravel.common.database.entity.*
import java.lang.Exception

class DataManager {

    //-------------  user -------------

    fun insertUser(user: User):Long{
        val daoSession = BaseApplication.application?.travelDatabase?.userDao()
        daoSession?.let {
            var result = -1L
            try {
                result = it.insertUser(user)[0]
                HLogger.instance().e("insertUser","$result")
            }catch (e:Exception){
                HLogger.instance().e("insertUser","insert user fail : ${e.message}")
            }
            return result
        }
        return -1L
    }

    fun getUserByID(userId:String) : User?{
        val daoSession = BaseApplication.application?.travelDatabase?.userDao()
        var result: User? = null
        daoSession?.let {
            try {
                result = it.getUserByID(userId)
                HLogger.instance().e("getUserByID","$result")
            }catch (e:Exception){
                HLogger.instance().e("getUserByID","get user fail : ${e.message}")
            }
            return result
        }
        return result
    }

    //-------------  travel -------------

    /**
     * 获取未完成的旅行
     */
    fun getNotEndTravel():MutableList<Travel>?{
        val daoSession = BaseApplication.application?.travelDatabase?.travelDao()
        val detailDao = BaseApplication.application?.travelDatabase?.detailDao()
        val memberDao = BaseApplication.application?.travelDatabase?.memberDao()
        val stepDao = BaseApplication.application?.travelDatabase?.stepDao()
        var travels:MutableList<Travel>? =  null
        daoSession?.let {
            val list = it.getNotEndTravel(UserData.instance().getLoginUserId())
            list?.let {selectList ->
                travels = mutableListOf()
                for (travel in list){
                    stepDao?.let {
                        travel.stepList = stepDao.getStepByTravelId(travel.id)
                    }
                    detailDao?.let {
                        travel.detailList = detailDao.getDetailByTravelId(travel.id)
                    }
                    memberDao?.let {
                        travel.memberList = memberDao.getMemberByTravelId(travel.id)
                    }
                    travels?.add(travel)
                }

            }
        }
        return travels
    }

    /**
     * 插入旅行数据
     */
    fun insertOrReplaceTravel(travel: Travel):Long{
        val daoSession = BaseApplication.application?.travelDatabase?.travelDao()
        daoSession?.let {
            var result = -1L
            try {
                result = it.insertTravel(travel)[0]
                HLogger.instance().e("insertOrReplaceTravel","$result")
            }catch (e:Exception){
                HLogger.instance().e("insertOrReplaceTravel","insert travel fail : ${e.message}")
            }
            return result
        }
        return -1L
    }


    //-------------  member -------------

    /**
     * 批量插入partner
     */
    fun insertMembers(members: List<Member>):Long{
        if(members.isEmpty()){
            return 0
        }
        val daoSession = BaseApplication.application?.travelDatabase?.memberDao()
        daoSession?.let {
            var result = -1L
            try {

                result = if(members.size > 1) {
                    it.insertMember(*members.toTypedArray())[0]
                }else{
                    it.insertMember(members[0])[0]
                }
                HLogger.instance().e("insertMembers","$result")
            }catch (e:Exception){
                HLogger.instance().e("insertMembers","insert members fail : ${e.message}")
            }
            return result
        }
        return -1L
    }

    /**
     * 查询partner
     */
    fun getPartnerByTravelId(travelId : String?) : MutableList<Member>{
        val members = mutableListOf<Member>()
        if(travelId == null){
            return members
        }
        val daoSession = BaseApplication.application?.travelDatabase?.memberDao()
        daoSession?.let {
            try {
                val result = it.getMemberByTravelId(travelId)
                if(result != null){
                    members.addAll(result)
                }
                HLogger.instance().e("getPartnerByTravelId","${result?.size}")
            }catch (e:Exception){
                HLogger.instance().e("getPartnerByTravelId","get members fail : ${e.message}")
            }
        }
        return members
    }

    //-------------  DetailWithMember -------------
    /**
     * 明细人员关联插入partner
     */
    fun insertDetailWithMember(detailWithMembers: List<DetailWithMember>):Long{
        if(detailWithMembers.isEmpty()){
            return 0
        }
        val daoSession = BaseApplication.application?.travelDatabase?.detailWithMemberDao()
        daoSession?.let {
            var result = -1L
            try {
                result = if(detailWithMembers.size > 1) {
                    it.insertDM(*detailWithMembers.toTypedArray())[0]
                }else{
                    it.insertDM(detailWithMembers[0])[0]
                }
                HLogger.instance().e("insertDetailWithMembers","$result")
            }catch (e:Exception){
                HLogger.instance().e("insertDetailWithMembers","insert DetailWithMembers fail : ${e.message}")
            }
            return result
        }
        return -1L
    }
    //-------------  Detail -------------
    /**
     * 明细插入partner
     */
    fun insertDetail(details: List<Detail>):Long{
        if(details.isEmpty()){
            return 0
        }
        val daoSession = BaseApplication.application?.travelDatabase?.detailDao()
        daoSession?.let {
            var result = -1L
            try {

                result = if(details.size > 1) {
                    it.insertDetail(*details.toTypedArray())[0]
                }else{
                    it.insertDetail(details[0])[0]
                }
                HLogger.instance().e("insertDetails","$result")
            }catch (e:Exception){
                HLogger.instance().e("insertDetails","insert details fail : ${e.message}")
            }
            return result
        }
        return -1L
    }



}