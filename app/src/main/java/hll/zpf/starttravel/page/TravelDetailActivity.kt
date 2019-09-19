package hll.zpf.starttravel.page

import android.os.Bundle
import hll.zpf.starttravel.R
import hll.zpf.starttravel.base.BaseActivity
import hll.zpf.starttravel.common.EventBusMessage
import hll.zpf.starttravel.common.enums.ActivityMoveEnum
import hll.zpf.starttravel.common.model.TravelModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class TravelDetailActivity : BaseActivity() {

    private var travelModel:TravelModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_travel_detail)
        EventBus.getDefault().register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun initData(message: EventBusMessage){
        if(message.message.equals(TRAVEL_DETAIL)){
            travelModel =  message.travel
            setTitle(
                travelModel!!.getTravelData().value!!.name!!,
                true,
                getString(R.string.travel_detail_001)){
                when(it.id){
                    R.id.left_button -> {//返回
                        finish()
                        baseStartActivity(null, ActivityMoveEnum.BACK_FROM_LEFT)
                    }
                    R.id.right_button -> {//添加
//                    commitTravel()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}
