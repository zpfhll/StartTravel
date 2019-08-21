package hll.zpf.starttravel.common

import hll.zpf.starttravel.common.enums.TravelTypeEnum

class EventBusMessage {

    var message:String = ""

    var travelType: TravelTypeEnum = TravelTypeEnum.MONEY_TRAVEL

    companion object{
        @Synchronized
        fun instance(message:String):EventBusMessage{
            val eventBusMessage = EventBusMessage()
            eventBusMessage.message = message
            return eventBusMessage
        }
    }
}