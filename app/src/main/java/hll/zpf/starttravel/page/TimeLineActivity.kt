package hll.zpf.starttravel.page

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import hll.zpf.starttravel.BuildConfig
import hll.zpf.starttravel.R
import hll.zpf.starttravel.base.BaseActivity
import hll.zpf.starttravel.common.EventBusMessage
import hll.zpf.starttravel.common.HLogger
import hll.zpf.starttravel.common.Utils
import hll.zpf.starttravel.common.database.DataManager
import hll.zpf.starttravel.common.database.entity.Step
import hll.zpf.starttravel.common.enums.ActivityMoveEnum
import kotlinx.android.synthetic.main.activity_time_line.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class TimeLineActivity : BaseActivity() {

    private var data = mutableListOf<Step>()

    private var stepAdapter:TimeLineAdapter? = null

    private var travelId:String? = null

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
            data.add(Step.createStep())
            stepAdapter = TimeLineAdapter(this, data){type,index,handler ->
                HLogger.instance()
                    .e("TimeLineAdapter", "type: $type index:$index")
                when(type){
                    TimeLineAdapter.TIME_LINE_ADD -> {
                        val addTimeLineIntent = Intent(this,AddTimeLineActivity::class.java)
                        addTimeLineIntent.putExtra("travelId",travelId)
                        baseStartActivity(addTimeLineIntent, ActivityMoveEnum.START_FROM_RIGHT)
                    }
                    TimeLineAdapter.TIME_LINE_DETAIL -> {
                        showTimeLineDetail(index,handler)
                    }
                    TimeLineAdapter.TIME_LINE_MODIFY -> {
                        val modifyTimeLineIntent = Intent(this,AddTimeLineActivity::class.java)
                        modifyTimeLineIntent.putExtra("stepId",data[index].id)
                        baseStartActivity(modifyTimeLineIntent, ActivityMoveEnum.START_FROM_RIGHT)
                    }
                }
            }
            time_line.adapter = stepAdapter
            val linearLayoutManager = LinearLayoutManager(context)
            linearLayoutManager.stackFromEnd = true
            linearLayoutManager.reverseLayout = true//布局反向

            time_line.layoutManager = linearLayoutManager
            dataManager = DataManager()
            travelId = message.travel?.getTravelData()?.value?.id
            refreshTimeLine(travelId)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBusRefresh(message: EventBusMessage){
        if(message.message == TIME_LINE_REFRESH) {
            refreshTimeLine(travelId)
        }
    }

    /**
     * 刷新页面数
     */
    private fun refreshTimeLine(travelId:String?){
        dataManager?.getStepByTravelId(travelId){resultCode, resultData ->
            if(resultCode == BuildConfig.NORMAL_CODE) {
                data = resultData
                if(resultData.isNullOrEmpty()){
                    data.add(Step.createStep())
                }
                stepAdapter?.refreshData(resultData)
                time_line.scrollToPosition(data.size-1)
            }else{
                showMessageAlertDialog("","${getString(R.string.DATABASE_ERROR)}($resultCode)"){_,_ ->
                    onKeyCodeBackListener()
                }
            }
        }
    }

    /**
     * タイムラインの詳細を表示
     *
     * @param index データフラグ
     * @param handler ビューの容器
     */
    private fun showTimeLineDetail(index:Int,handler:TimeLineAdapter.StepItemViewHandler?){
        handler?.let {
            val location = intArrayOf(0,0)
            val rootLocation = intArrayOf(0,0)
            root_view.getLocationInWindow(rootLocation)
            val copyView = LayoutInflater.from(this).inflate(R.layout.time_line_item_copy,null)
            if(handler.leftTimeLineItemBackground.visibility == View.VISIBLE) {
                handler.leftTimeLineItemBackground.getLocationInWindow(location)
            }else{
                handler.rightTimeLineItemBackground.getLocationInWindow(location)
            }
            val layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.WRAP_CONTENT)
            layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams.marginStart = location[0]
            layoutParams.topMargin = location[1] - rootLocation[1]
            data[index].getImageBitmap()?.let {
                copyView.findViewById<ImageView>(R.id.time_line_image).setImageBitmap(data[index].getImageBitmap())
            }
            copyView.findViewById<TextView>(R.id.time_line_name).text = data[index].name
            copyView.findViewById<TextView>(R.id.time_line_time).text = Utils.instance().getDateStringByFormatAndDateString(data[index].startDate,getString(R.string.time_line_001))
            var textColor = R.color.baseColor
            val itemBackgroundThemeId = when (index % 4) {
                0 -> {
                    textColor = R.color.baseColor
                    R.drawable.circle_round_edge_green_15
                }
                1 -> {
                    textColor = R.color.orange
                    R.drawable.circle_round_edge_orange_15
                }
                2 -> {
                    textColor = R.color.title_color
                    R.drawable.circle_round_edge_dark_gray_15
                }
                3 -> {
                    textColor = R.color.red
                    R.drawable.circle_round_edge_red_15
                }
                else ->  R.drawable.circle_round_edge_green_15
            }
            copyView.findViewById<View>(R.id.time_line_item_background).setBackgroundResource(itemBackgroundThemeId)
            copyView.findViewById<TextView>(R.id.time_line_time).setTextColor(getColor(textColor))
            root_view.addView(copyView,layoutParams)





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
