package hll.zpf.starttravel.common.bean

import android.media.Image
import hll.zpf.starttravel.common.enums.TravelStateEnum

class TravelBean() {
    var travelId:String = ""
    var travelName:String = ""
    var travelMemo:String = ""
    var travelDate:String = ""
    var travelPersonNumber:Int = 0
    var travelMoney:Float = 0f
    var travelImage:Image? = null
    var travelState:TravelStateEnum = TravelStateEnum.NOT_START
}