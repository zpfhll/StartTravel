package hll.zpf.starttravel.page

import android.os.Bundle
import hll.zpf.starttravel.R
import hll.zpf.starttravel.base.BaseActivity
import hll.zpf.starttravel.common.EventBusMessage
import hll.zpf.starttravel.common.enums.ActivityMoveEnum
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class AddDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_detail)
        EventBus.getDefault().register(this)
    }


    @Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN, sticky = true)
    fun initData(message: EventBusMessage){
        if(message.message.equals(ADD_DETAIL)){
            val detail = message.detail
            val title = if(detail == null) getString(R.string.add_detail_001) else getString(R.string.add_detail_002)
            setTitle(
                title,
                false,
                "",
                R.drawable.back_button_background){
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
