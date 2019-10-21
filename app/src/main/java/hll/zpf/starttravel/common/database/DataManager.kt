package hll.zpf.starttravel.common.database

import hll.zpf.starttravel.BuildConfig
import hll.zpf.starttravel.base.BaseApplication
import hll.zpf.starttravel.common.HLogger
import hll.zpf.starttravel.common.UserData
import hll.zpf.starttravel.common.database.entity.*
import kotlinx.coroutines.*
import java.lang.Exception

class DataManager : CoroutineScope by MainScope(){



    data class ResultData(val resultCode:String,val data:Any? = null)

    /**
     * 异步处理
     */
    private fun runTaskByAsyn(taskName:String,task:(()->ResultData),callBack: ((ResultData) -> Unit)){
        HLogger.instance().e(taskName,"start")
        launch(Dispatchers.IO){
            val result = task()
            HLogger.instance().e(taskName,"$result end")
            withContext(Dispatchers.Main){
                callBack(result)
            }
        }
    }

    //-------------  user -------------
    fun insertUser(user: User , callBack:(resultCode:String) -> Unit){
        runTaskByAsyn("insertUser",task = {
            val daoSession = BaseApplication.application?.travelDatabase?.userDao()
            var errorCode = BuildConfig.NORMAL_CODE

            daoSession?.let {
                try {
                    it.insertUser(user)
                }catch (e:Exception){
                    errorCode = "E00004"
                    HLogger.instance().e("insertUser","insert user fail : ${e.message}")
                }
            }
            ResultData(errorCode)
        },callBack = {
            callBack(it.resultCode)
        })
    }



    fun getUserByID(userId:String,callBack:(resultCode:String,data:User?) -> Unit){
        runTaskByAsyn("getUserByID",task = {
            val daoSession = BaseApplication.application?.travelDatabase?.userDao()
            var result: User? = null
            var errorCode = BuildConfig.NORMAL_CODE
            daoSession?.let {
                try {
                    result = it.getUserByID(userId)
                }catch (e:Exception){
                    errorCode = "E00005"
                    HLogger.instance().e("getUserByID","get user fail : ${e.message}")
                }
            }
            ResultData(errorCode,result)
        },callBack = {
            callBack(it.resultCode,it.data as? User)
        })

    }

    //-------------  travel -------------

    /**
     * 获取未完成的旅行
     */
    fun getNotEndTravel(callBack:(resultCode:String,data:MutableList<Travel>?) -> Unit){
        runTaskByAsyn("getNotEndTravel",task = {
            val daoSession = BaseApplication.application?.travelDatabase?.travelDao()
            var travels:MutableList<Travel>? =  null
            var errorCode = BuildConfig.NORMAL_CODE
            daoSession?.let {
                try {
                    val cursor = it.getNotEndTravel(UserData.instance().getLoginUserId())
                    travels = mutableListOf()
                    while (cursor.moveToNext()){
                        val travel = Travel()
                        travel.id = cursor.getString(cursor.getColumnIndex("id"))
                        travel.name = cursor.getString(cursor.getColumnIndex("name"))
                        travel.startDate = cursor.getString(cursor.getColumnIndex("start_date"))
                        travel.endDate = cursor.getString(cursor.getColumnIndex("end_date"))
                        travel.endDate = cursor.getString(cursor.getColumnIndex("end_date"))
                        travel.money = cursor.getFloat(cursor.getColumnIndex("money"))
                        travel.state = cursor.getInt(cursor.getColumnIndex("state"))
                        travel.image = cursor.getBlob(cursor.getColumnIndex("image"))
                        travel.type = cursor.getInt(cursor.getColumnIndex("type"))
                        travel.userId = cursor.getString(cursor.getColumnIndex("user_id"))
                        travel.memberCount = cursor.getInt(cursor.getColumnIndex("memberCount"))
                        travel.outMoney = cursor.getFloat(cursor.getColumnIndex("outMoney"))
                        travels?.add(travel)
                    }
                }catch (e:Exception){
                    errorCode = "E00006"
                    HLogger.instance().e("getNotEndTravel","get NotEndTravel fail : ${e.message}")
                }
            }
            ResultData(errorCode,travels)
        },callBack = {
            callBack(it.resultCode,it.data as? MutableList<Travel>)
        })
    }

    /**
     * 插入旅行数据
     */
    fun insertOrReplaceTravel(travel: Travel,members: List<Member>? = null,callBack:(resultCode:String) -> Unit){
        runTaskByAsyn("insertOrReplaceTravel And insertMembers",task = {
            val daoSession = BaseApplication.application?.travelDatabase?.travelDao()
            var errorCode = BuildConfig.NORMAL_CODE
            daoSession?.let {
                try {
                    it.insertTravel(travel)
                }catch (e:Exception){
                    errorCode = "E00007"
                    HLogger.instance().e("insertOrReplaceTravel","insert travel fail : ${e.message}")
                }
            }

            if (errorCode.equals(BuildConfig.NORMAL_CODE) && !members.isNullOrEmpty()){
                val memberDaoSession = BaseApplication.application?.travelDatabase?.memberDao()
                memberDaoSession?.let {
                    try {
                        if(members.size > 1) {
                            it.insertMember(*members.toTypedArray())
                        }else{
                            it.insertMember(members[0])
                        }
                    }catch (e:Exception){
                        errorCode = "E00008"
                        HLogger.instance().e("insertMembers","insert members fail : ${e.message}")
                    }
                }
            }
            ResultData(errorCode)
        },callBack = {
            callBack(it.resultCode)
        })

    }



    //-------------  member -------------
//    /**
//     * 批量插入partner
//     */
//    fun insertMembers(members: List<Member>):Long{
//        if(members.isEmpty()){
//            return 0
//        }
//        val daoSession = BaseApplication.application?.travelDatabase?.memberDao()
//        daoSession?.let {
//            var result = -1L
//            try {
//
//                result = if(members.size > 1) {
//                    it.insertMember(*members.toTypedArray())[0]
//                }else{
//                    it.insertMember(members[0])[0]
//                }
//                HLogger.instance().e("insertMembers","$result")
//            }catch (e:Exception){
//                HLogger.instance().e("insertMembers","insert members fail : ${e.message}")
//            }
//            return result
//        }
//        return -1L
//    }

    /**
     * 查询partner
     */
    fun getPartnerByTravelId(travelId : String?,callBack: (resultCode:String,data:MutableList<Member>) -> Unit){
        runTaskByAsyn("getPartnerByTravelId",task = {
            val members = mutableListOf<Member>()
            var errorCode = BuildConfig.NORMAL_CODE
            if(travelId != null) {
                val daoSession = BaseApplication.application?.travelDatabase?.memberDao()
                daoSession?.let {
                    try {
                        val result = it.getMemberByTravelId(travelId)
                        if (result != null) {
                            members.addAll(result)
                        }
                    } catch (e: Exception) {
                        errorCode = "E00009"
                        HLogger.instance().e("getPartnerByTravelId", "get members fail : ${e.message}")
                    }
                }
            }
            ResultData(errorCode,members)
        },callBack = {
            callBack(it.resultCode,it.data as MutableList<Member>)
        })
    }

    //-------------  DetailWithMember -------------
//    /**
//     * 明细人员关联插入partner
//     */
//    fun insertDetailWithMember(detailWithMembers: List<DetailWithMember>,callBack: (Long) -> Unit){
//        runTaskByAsyn("insertDetailWithMember",task = {
//            var result = -1L
//            if(detailWithMembers.isEmpty()){
//                result =  0
//            }else {
//                val daoSession = BaseApplication.application?.travelDatabase?.detailWithMemberDao()
//                daoSession?.let {
//                    try {
//                        result = if (detailWithMembers.size > 1) {
//                            it.insertDM(*detailWithMembers.toTypedArray())[0]
//                        } else {
//                            it.insertDM(detailWithMembers[0])[0]
//                        }
//                    } catch (e: Exception) {
//                        HLogger.instance().e(
//                            "insertDetailWithMembers",
//                            "insert DetailWithMembers fail : ${e.message}"
//                        )
//                    }
//                }
//            }
//            result
//        },callBack = {
//            callBack(it as Long)
//        })
//    }


    //-------------  Detail -------------
    /**
     * 明细插入
     */
    fun insertDetail(details: List<Detail>,detailWithMembers: List<DetailWithMember>? = null,callBack: (resultCode:String) -> Unit){
        runTaskByAsyn("insertDetail And insertDetailWithMember",task = {
            var errorCode = BuildConfig.NORMAL_CODE
            if(!details.isNullOrEmpty()){
                val daoSession = BaseApplication.application?.travelDatabase?.detailDao()
                daoSession?.let {
                    try {
                        if (details.size > 1) {
                            it.insertDetail(*details.toTypedArray())
                        } else {
                            it.insertDetail(details[0])
                        }
                    } catch (e: Exception) {
                        errorCode = "E00010"
                        HLogger.instance().e("insertDetails", "insert details fail : ${e.message}")
                    }
                }

                if(errorCode.equals(BuildConfig.NORMAL_CODE) && !detailWithMembers.isNullOrEmpty()){
                    val detailWithMemberDaoSession = BaseApplication.application?.travelDatabase?.detailWithMemberDao()
                    detailWithMemberDaoSession?.let {
                        try {
                            if (detailWithMembers.size > 1) {
                                it.insertDM(*detailWithMembers.toTypedArray())
                            } else {
                                it.insertDM(detailWithMembers[0])
                            }
                        } catch (e: Exception) {
                            errorCode = "E00011"
                            HLogger.instance().e(
                                "insertDetailWithMembers",
                                "insert DetailWithMembers fail : ${e.message}"
                            )
                        }
                    }
                }
            }
            ResultData(errorCode)
        },callBack = {
            callBack(it.resultCode)
        })
    }

    /**
     * 明细查询
     */
    fun getDetailByTravelId(travelId : String?,callBack: (resultCode:String,data:MutableList<Detail>) -> Unit){
        runTaskByAsyn("getDetailByTravelId",task = {
            val details = mutableListOf<Detail>()
            var errorCode = BuildConfig.NORMAL_CODE
            if(travelId != null) {
                val daoSession = BaseApplication.application?.travelDatabase?.detailDao()
                daoSession?.let {
                    try {
                        val result = it.getDetailByTravelId(travelId)
                        if (result != null) {
                            details.addAll(result)
                        }
                    } catch (e: Exception) {
                        errorCode  = "E00012"
                        HLogger.instance()
                            .e("getDetailByTravelId", "get details fail : ${e.message}")
                    }
                }
            }
            ResultData(errorCode,details)
        },callBack = {
            callBack(it.resultCode,it.data as MutableList<Detail>)
        })
    }
}