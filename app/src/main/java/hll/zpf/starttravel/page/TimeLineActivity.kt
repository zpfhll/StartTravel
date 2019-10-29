package hll.zpf.starttravel.page

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import hll.zpf.starttravel.R
import hll.zpf.starttravel.base.BaseActivity
import hll.zpf.starttravel.common.EventBusMessage
import hll.zpf.starttravel.common.Utils
import hll.zpf.starttravel.common.database.entity.Step
import hll.zpf.starttravel.common.enums.ActivityMoveEnum
import kotlinx.android.synthetic.main.activity_time_line.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class TimeLineActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_line)
        EventBus.getDefault().register(this)

    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun initData(message: EventBusMessage){
        if(message.message == TIME_LINE){
            setTitleWithColor(
                message.travel?.getTravelData()?.value?.name ?: "",
                false,
                null,
                R.drawable.back_white_button_background,
                R.color.transparent,
                R.color.white,
                false){
                when(it.id){
                    R.id.left_button -> {//返回
                        onKeyCodeBackListener()
                    }
                }
            }
            message.travel?.getTravelData()?.value?.getImageBitmap()?.let {
                background_image.setImageBitmap(it)
            }
            val datas = mutableListOf<Step>()
            for (index in 0..2){
                datas.add(Step.createStep())
            }
            val stepAdapter = TimeLineAdapter(this, datas){type,index ->
                when(type){
                    TimeLineAdapter.TIME_LINE_ADD -> {
                        val addTimeLineIntent = Intent(this,AddTimeLineActivity::class.java)
                        baseStartActivity(addTimeLineIntent, ActivityMoveEnum.START_FROM_RIGHT)
                    }
                }
            }
            time_line.adapter = stepAdapter
            val linearLayoutManager = LinearLayoutManager(context)
            linearLayoutManager.stackFromEnd = true
            linearLayoutManager.reverseLayout = true//布局反向

            time_line.layoutManager = linearLayoutManager
        }
    }

    override fun onKeyCodeBackListener() {
        super.onKeyCodeBackListener()
        finish()
        baseStartActivity(null, ActivityMoveEnum.BACK_FROM_LEFT)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}
