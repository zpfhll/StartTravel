package hll.zpf.starttravel.page

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
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
import kotlin.math.absoluteValue

class TimeLineActivity : BaseActivity() {

    private var data = mutableListOf<Step>()

    private var stepAdapter:TimeLineAdapter? = null

    private var travelId:String? = null

    private var screenWidth:Int = 0

    private var screenHeight:Int = 0

    private lateinit var utils:Utils

    private var isShowingDetail = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_line)
        utils = Utils.instance()
        EventBus.getDefault().register(this)
        screenWidth = utils.getScreenWidth()
        screenHeight = utils.getScreenHeight()
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
            isShowingDetail = true
            val location = intArrayOf(0,0)
            val rootLocation = intArrayOf(0,0)
            root_view.getLocationInWindow(rootLocation)
            val copyView = LayoutInflater.from(this).inflate(R.layout.time_line_item_copy,null)
            if(handler.isLeft) {
                handler.leftTimeLineItemBackground.getLocationInWindow(location)
            }else{
                handler.rightTimeLineItemBackground.getLocationInWindow(location)
            }
            val layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.WRAP_CONTENT)
            layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams.marginStart = location[0]
            layoutParams.topMargin = location[1] - rootLocation[1]
            val timeLineImage = copyView.findViewById<ImageView>(R.id.time_line_image)
            data[index].getImageBitmap()?.let {
                timeLineImage.setImageBitmap(data[index].getImageBitmap())
            }
            val timeLineName =  copyView.findViewById<TextView>(R.id.time_line_name)
            timeLineName.text = data[index].name
            val timeLineBackground = copyView.findViewById<View>(R.id.time_line_item_background)
            val timeLineTime = copyView.findViewById<TextView>(R.id.time_line_time)
            val timeLineDetailButton = copyView.findViewById<Button>(R.id.time_line_detail_button)
            val timeLineLocationImage = copyView.findViewById<ImageView>(R.id.time_line_location)
            val timeLineLocationName = copyView.findViewById<TextView>(R.id.time_line_location_name)
            val locationName = data[index].locationName
            if(!locationName.isBlank()){
                timeLineLocationName.text = locationName
            }else{
                timeLineLocationName.text = getString(R.string.time_line_002)
            }
            val timeLineMemo = copyView.findViewById<TextView>(R.id.time_line_memo)
            val memo = data[index].memo
            if(!memo.isNullOrBlank()){
                timeLineMemo.text = memo
            }else{
                timeLineMemo.text = getString(R.string.time_line_003)
            }
            timeLineTime.text = utils.getDateStringByFormatAndDateString(data[index].startDate,getString(R.string.time_line_001))
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

            timeLineBackground.setBackgroundResource(itemBackgroundThemeId)
            timeLineTime.setTextColor(getColor(textColor))
            root_view.addView(copyView,layoutParams)
            val timeLineTimeLayout = timeLineTime.layoutParams as ConstraintLayout.LayoutParams
            val imageHeight = timeLineImage.layoutParams.height
            val copyHeight = timeLineBackground.layoutParams.height
            val copyWidth = timeLineBackground.layoutParams.width

            handler.setVisibility(View.INVISIBLE)

            val x:Int
            val y:Int
            if(handler.isLeft){
                x = screenWidth  / 5 / 2 - location[0] //copyview移动的X距离 放大后的宽度是屏幕的3/4
                y = (screenHeight - screenWidth / 2) / 2  - location[1] //copyview移动的Y距离 放大后的高度是屏幕宽度
            }else{
                x = -(location[0] - screenWidth / 5 / 2)
                y = (screenHeight - screenWidth / 2) / 2  - location[1]
//                x = -(location[0] - screenWidth / 2  +  handler.rightTimeLineItemBackground.width / 2)
//                y = screenHeight / 2 - location[1] - handler.rightTimeLineItemBackground.height / 2
            }
            val animatorX = ObjectAnimator.ofFloat(copyView, "translationX", 0f, x.toFloat())
            animatorX.duration = 500
            val animatorY = ObjectAnimator.ofFloat(copyView, "translationY", 0f, y.toFloat())
            animatorY.duration = 500
            timeLineLocationImage.visibility = View.VISIBLE
            animatorX.addUpdateListener {
                val scale = ((it.animatedValue as Float) / x).absoluteValue
                show_background.alpha = scale / 2 + 0.3f
                //缓慢放大
                timeLineName.textSize = 12 + 5 * scale

                timeLineLocationImage.layoutParams.height = (utils.DPToPX(15f) * scale).toInt()
                timeLineLocationImage.layoutParams.width = (utils.DPToPX(15f) * scale).toInt()

                timeLineMemo.layoutParams.height =  ((screenWidth / 5) * scale).toInt()

                timeLineTimeLayout.horizontalBias = 0.5f + (0.4f * scale)
                timeLineTimeLayout.bottomMargin = (utils.DPToPX(21f) * scale).toInt()
                if(scale > 0.3){
                    timeLineTime.setTextColor(getColor(R.color.white))
                }
                timeLineDetailButton.alpha = 1 - scale
                timeLineTime.layoutParams = timeLineTimeLayout

                timeLineImage.layoutParams.width = (imageHeight + (imageHeight / 2) * scale).toInt()
                timeLineImage.layoutParams.height =  timeLineImage.layoutParams.width

                timeLineBackground.layoutParams.height = copyHeight + ((screenWidth / 2 - copyHeight) * scale).toInt()
                timeLineBackground.layoutParams.width = copyWidth + (((screenWidth / 5) * 4 - copyWidth) * scale).toInt()
            }
            show_background.visibility  = View.VISIBLE
            var isClicked = false //copyview 是否点击
            show_background.setOnClickListener{
                if(!isClicked) {
                    isClicked = true
                    val xBack = copyView.translationX
                    val animatorBackX =
                        ObjectAnimator.ofFloat(copyView, "translationX", copyView.translationX, 0f)
                    animatorBackX.duration = 500
                    val animatorBackY =
                        ObjectAnimator.ofFloat(copyView, "translationY", copyView.translationY, 0f)
                    animatorBackY.duration = 500
                    val backgroundAlpha = show_background.alpha
                    animatorBackX.addUpdateListener {
                        val scale = ((it.animatedValue as Float) / xBack).absoluteValue
                        show_background.alpha = scale * backgroundAlpha
                        //缓慢缩小
                        timeLineName.textSize = 12 + 5 * scale

                        timeLineLocationImage.layoutParams.height = (utils.DPToPX(15f) * scale).toInt()
                        timeLineLocationImage.layoutParams.width = (utils.DPToPX(15f) * scale).toInt()

                        timeLineMemo.layoutParams.height =  ((screenWidth / 5) * scale).toInt()

                        timeLineTimeLayout.horizontalBias = 0.5f + (0.4f * scale)
                        timeLineTimeLayout.bottomMargin = (utils.DPToPX(21f).toInt() * scale).toInt()
                        if(scale < 0.3){
                            timeLineTime.setTextColor(getColor(textColor))
                        }
                        timeLineDetailButton.alpha = 1 - scale
                        timeLineTime.layoutParams = timeLineTimeLayout

                        timeLineImage.layoutParams.width = (imageHeight + (imageHeight / 2) * scale).toInt()
                        timeLineImage.layoutParams.height =  timeLineImage.layoutParams.width

                        timeLineBackground.layoutParams.height = copyHeight + ((screenWidth  / 2 - copyHeight) * scale).toInt()
                        timeLineBackground.layoutParams.width = copyWidth + (((screenWidth / 5) * 4 - copyWidth) * scale).toInt()
                        if (scale == 0f) {
                            handler.setVisibility(View.VISIBLE)
                            show_background.visibility = View.GONE
                            root_view.removeView(copyView)
                            isClicked = false
                            isShowingDetail = false
                        }
                    }
                    animatorBackX.start()
                    animatorBackY.start()
                }
            }
            animatorX.start()
            animatorY.start()
        }
    }

    override fun onKeyCodeBackListener() {
        if(!isShowingDetail) {
            super.onKeyCodeBackListener()
            finish()
            baseStartActivity(null, ActivityMoveEnum.BACK_FROM_LEFT)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}
