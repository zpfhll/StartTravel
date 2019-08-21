package hll.zpf.starttravel.common

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import hll.zpf.starttravel.base.BaseApplication
import hll.zpf.starttravel.common.database.entity.User

class UserData {

    var sharedPreferences:SharedPreferences? = null
    val preferenceName:String = "UserData"

    private val currentUser = "CURRENT_USER"

    companion object{
        @SuppressLint("StaticFieldLeak")
        private  var userData:UserData? = null
            get() {
                if (field == null){
                    field = UserData()
                    BaseApplication.application?.let {
                        field?.sharedPreferences = it.applicationContext.getSharedPreferences(field?.preferenceName,Context.MODE_PRIVATE)
                    }
                }
                return field
            }
        @Synchronized
        fun instance():UserData{
            return userData!!
        }
    }


    /**
     * 存储当前用户的ID
     */
    fun saveLoginUser(userId: String) : Boolean{
        sharedPreferences?.let {
            val editor = it.edit()
            editor.putString(currentUser,userId)
            editor.apply()
            return true
        }
        return false
    }

    /**
     * 获取当前登录的用户ID
     */
    fun getLoginUserId() : String{
        var userId = ""
        sharedPreferences?.let {
            userId = it.getString(currentUser,"")
            return userId
        }
        return userId
    }
}