package hll.zpf.starttravel.internet.bean

class InternetResponse {
    var internetBean:BaseInternetBean? = null
    var errorMessage:String? = null
    var errorCode:String? = null

    override fun toString(): String {
        return "InternetResponse(internetBean=$internetBean, errorMessage=$errorMessage, errorCode=$errorCode)"
    }


}