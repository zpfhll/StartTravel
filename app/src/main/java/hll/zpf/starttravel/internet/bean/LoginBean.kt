package hll.zpf.starttravel.internet.bean

import hll.zpf.starttravel.common.HLLAny

class LoginBean:BaseInternetBean() {

     @field: HLLAny(isMust = true)
     var accessToken:String = ""
     @field: HLLAny(isMust = true)
     var userCode:String = ""

     override fun toString(): String {
          return "LoginBean(accessToken='$accessToken', userCode='$userCode')"
     }

     @HLLAny(isMust = true)
     fun gettest(){}


}