package hll.zpf.starttravel.common

import hll.zpf.starttravel.common.database.entity.Detail
import hll.zpf.starttravel.common.enums.TravelTypeEnum
import hll.zpf.starttravel.common.model.TravelModel

class EventBusMessage {

    var message:String = ""

    var travelType: TravelTypeEnum = TravelTypeEnum.MONEY_TRAVEL

    var travel: TravelModel? = null

    var detail: Detail? = null

    companion object{
        @Synchronized
        fun instance(message:String):EventBusMessage{
            val eventBusMessage = EventBusMessage()
            eventBusMessage.message = message
            return eventBusMessage
        }
    }
}