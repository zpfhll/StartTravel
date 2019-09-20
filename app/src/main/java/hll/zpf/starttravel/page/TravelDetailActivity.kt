package hll.zpf.starttravel.page

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import hll.zpf.starttravel.R
import hll.zpf.starttravel.base.BaseActivity
import hll.zpf.starttravel.common.EventBusMessage
import hll.zpf.starttravel.common.HLogger
import hll.zpf.starttravel.common.database.entity.Detail
import hll.zpf.starttravel.common.enums.ActivityMoveEnum
import hll.zpf.starttravel.common.model.TravelModel
import kotlinx.android.synthetic.main.activity_travel_detail.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class TravelDetailActivity : BaseActivity() {

    private var travelModel:TravelModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_travel_detail)
        EventBus.getDefault().register(this)


        var detail = Detail("D10200020120120","明细1","20191203190303345",0,1234f,"T1209382093804982")
        var detail2 = Detail("D10200020120120","明细2","20191203190303345",0,1234f,"T1209382093804982")
        var detail3 = Detail("D10200020120120","明细3","20191203190303345",0,1234f,"T1209382093804982")


        val detailAdapter = TravelDetailAdapter(this, arrayListOf(detail,detail2,detail3)){
            HLogger.instance().e("TravelDetailAdapter--->","item $it")
        }
        detail_list.adapter = detailAdapter
        detail_list.layoutManager = LinearLayoutManager(context)

    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun initData(message: EventBusMessage){
        if(message.message.equals(TRAVEL_DETAIL)){
            travelModel =  message.travel
            setTitle(
                travelModel!!.getTravelData().value!!.name!!,
                true,
                getString(R.string.travel_detail_001),
                R.drawable.back_button_background){
                when(it.id){
                    R.id.left_button -> {//返回
                        finish()
                        baseStartActivity(null, ActivityMoveEnum.BACK_FROM_LEFT)
                    }
                    R.id.right_button -> {//添加
//                    commitTravel()
                        val event = EventBusMessage.instance(ADD_DETAIL)
                        event.travel = travelModel
                        EventBus.getDefault().postSticky(event)
                        val moneyIntent = Intent(this,AddDetailActivity::class.java)
                        baseStartActivity(moneyIntent, ActivityMoveEnum.START_FROM_RIGHT)
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
