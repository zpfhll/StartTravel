package hll.zpf.starttravel.page

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import android.view.View
import hll.zpf.starttravel.R
import hll.zpf.starttravel.base.BaseActivity
import hll.zpf.starttravel.common.EventBusMessage
import hll.zpf.starttravel.common.HLogger
import hll.zpf.starttravel.common.Utils
import hll.zpf.starttravel.common.bean.StepBean
import hll.zpf.starttravel.common.components.ITButton
import hll.zpf.starttravel.common.enums.ActivityMoveEnum
import hll.zpf.starttravel.common.enums.TravelTypeEnum
import hll.zpf.starttravel.page.fragment.HistoryFragment
import hll.zpf.starttravel.page.fragment.MapFragment
import hll.zpf.starttravel.page.fragment.MeFragment
import hll.zpf.starttravel.page.fragment.TravelFragment
import org.greenrobot.eventbus.EventBus


class HomeActivity : BaseActivity() {

    private lateinit var travelButton:ITButton
    private lateinit var mapButton:ITButton
    private lateinit var historyButton:ITButton
    private lateinit var selfButton:ITButton
    private lateinit var bottomButtons:List<ITButton>
    private lateinit var selectedView:View
    private lateinit var mFragmentManager:FragmentManager
    private lateinit var mAddTravelPlatform:ConstraintLayout
    private lateinit var mDragView: ConstraintLayout
    private lateinit var mAddTravelBackground:View

    /**
     * 旅途Fragment
     */
    private var travelFragment:Fragment? = null
    /**
     * 标记Fragment
     */
    private var mapFragment:Fragment? = null
    /**
     * 足迹Fragment
     */
    private var historyFragment:Fragment? = null
    /**
     * 我Fragment
     */
    private var meFragment:Fragment? = null
    /**
     * 滑动开始位置
     */
    private  var addPlatformMoveStart:Float = 0f
    /**
     * 添加区域的高度
     */
    private var addPlatformHeight:Float = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setStatusBarColor(R.color.gray)

        selectedView = findViewById(R.id.selected_view)
        travelButton = findViewById(R.id.travel_bt)
        mapButton = findViewById(R.id.map_bt)
        historyButton = findViewById(R.id.history_bt)
        selfButton = findViewById(R.id.self_bt)
        bottomButtons = listOf(travelButton,mapButton,historyButton,selfButton)

        mAddTravelPlatform = findViewById(R.id.add_travel_platform)
        mDragView = findViewById(R.id.drag_view)
        mAddTravelBackground = findViewById(R.id.add_travel_background)

        val currentY = mAddTravelPlatform.translationY
        addPlatformHeight = mAddTravelPlatform.layoutParams.height.toFloat()
        val animator = ObjectAnimator.ofFloat(mAddTravelPlatform, "translationY", currentY, addPlatformHeight)
        animator.duration = 300
        animator.start()
        mAddTravelBackground.visibility = View.GONE
        mAddTravelPlatform.visibility = View.GONE

        mFragmentManager = supportFragmentManager
        val mTransaction = mFragmentManager.beginTransaction()
        travelFragment = TravelFragment()
        mTransaction.add(R.id.home_page_fly,travelFragment!!)
        mTransaction.commitNow()

        //设置选择标签的初始位置
        val selectedViewWidth = selectedView.layoutParams.width
        val buttonWidth = Utils.instance().getScreenWidth() / 5
        var selectedLayoutPa = selectedView.layoutParams as ConstraintLayout.LayoutParams
        selectedLayoutPa.marginStart = ( buttonWidth - selectedViewWidth ) / 2
        selectedView.layoutParams = selectedLayoutPa


        //拖动添加区域
        mDragView.setOnTouchListener { _, event ->
            when(event!!.action){
                MotionEvent.ACTION_DOWN -> {
                    addPlatformMoveStart = event.rawY
                }
                MotionEvent.ACTION_MOVE ->{
                    val distance = event.rawY - addPlatformMoveStart
                    if(distance >= 0){//DOWN
                        val currentY = mAddTravelPlatform.translationY
                        val animator =
                            ObjectAnimator.ofFloat(mAddTravelPlatform, "translationY", currentY, distance)
                        animator.duration = 0
                        animator.start()
                        val animatorAlpha = ObjectAnimator.ofFloat(mAddTravelBackground, "alpha",mAddTravelBackground.alpha , 1 - (distance / addPlatformHeight))
                        animatorAlpha.duration = 0
                        animatorAlpha.start()
                    }
                }
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                    val distance = event.rawY - addPlatformMoveStart
                    if (distance <= 0 ){//滑动距离为负时，什么也不做
                        true
                    }
                    if(distance / addPlatformHeight >= 0.5){//滑动距离大于一半的时候，隐藏
                        val animator = ObjectAnimator.ofFloat(mAddTravelPlatform, "translationY",  mAddTravelPlatform.translationY, addPlatformHeight)
                        animator.duration = 300
                        animator.start()
                        val animatorAlpha = ObjectAnimator.ofFloat(mAddTravelBackground, "alpha", mAddTravelBackground.alpha, 0f)
                        animatorAlpha.addListener(object : AnimatorListenerAdapter(){
                            override fun onAnimationEnd(animation: Animator?) {
                                mAddTravelBackground.visibility = View.GONE
                                mAddTravelPlatform.visibility = View.GONE
                            }
                        })
                        animatorAlpha.duration = 300
                        animatorAlpha.start()
                    }else{//滑动距离小于一半的时候，恢复原状
                        val animator = ObjectAnimator.ofFloat(mAddTravelPlatform, "translationY", mAddTravelPlatform.translationY, 0f)
                        animator.duration = 300
                        animator.start()
                        val animatorAlpha = ObjectAnimator.ofFloat(mAddTravelBackground, "alpha",mAddTravelBackground.alpha , 1f)
                        animatorAlpha.duration = 300
                        animatorAlpha.start()
                    }
                    addPlatformMoveStart = 0f
                }
            }
            true
        }

    }


    fun clickAction(view: View) {
        super.baseClickAction(view)
        when (view.id) {
            R.id.travel_bt -> {
                HLogger.instance().e("clickAction", "旅途")
                selectPage(view,travelFragment,1)
            }
            R.id.map_bt -> {
                HLogger.instance().e("clickAction", "标记")
                selectPage(view,mapFragment,2)
            }
            R.id.history_bt -> {
                HLogger.instance().e("clickAction", "足迹")
                selectPage(view,historyFragment,3)
            }
            R.id.self_bt -> {
                HLogger.instance().e("clickAction", "我")
                selectPage(view,meFragment,4)
            }
            R.id.add_travel_btn -> {//添加旅行
                mAddTravelPlatform.visibility = View.VISIBLE
                val animator = ObjectAnimator.ofFloat(mAddTravelPlatform, "translationY", mAddTravelPlatform.translationY, 0f)
                val animatorAlpha = ObjectAnimator.ofFloat(mAddTravelBackground, "alpha", 0f, 1f)
                animatorAlpha.addListener(object : AnimatorListenerAdapter(){
                    override fun onAnimationStart(animation: Animator?) {
                        mAddTravelBackground.visibility = View.VISIBLE
                    }
                })
                animator.duration = 300
                animatorAlpha.duration = 300
                animator.start()
                animatorAlpha.start()
            }
            R.id.close_btn -> {//关闭添加区域
               closeAddPlatform()
            }
            R.id.travel_money_btn -> {//记录花销
                val event = EventBusMessage.instance(TRAVEL_TYPE)
                event.travelType = TravelTypeEnum.MONEY_TRAVEL
                EventBus.getDefault().postSticky(event)
                val moneyIntent = Intent(this,AddTravelActivity::class.java)
                baseStartActivity(moneyIntent, ActivityMoveEnum.START_FROM_RIGHT)
                closeAddPlatform()
            }
            R.id.travel_only_btn -> {//随心旅行
                val event = EventBusMessage.instance(TRAVEL_TYPE)
                event.travelType = TravelTypeEnum.FREE_TRAVEL
                EventBus.getDefault().postSticky(event)
                val freeIntent = Intent(this,AddTravelActivity::class.java)
                baseStartActivity(freeIntent, ActivityMoveEnum.START_FROM_RIGHT)
                closeAddPlatform()
            }
        }

    }

    /**
     * 关闭添加区域
     */
    private fun closeAddPlatform(){
        val animator = ObjectAnimator.ofFloat(mAddTravelPlatform, "translationY",  mAddTravelPlatform.translationY, addPlatformHeight)
        animator.duration = 300
        animator.start()
        val animatorAlpha = ObjectAnimator.ofFloat(mAddTravelBackground, "alpha", 1f, 0f)
        animatorAlpha.addListener(object : AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                mAddTravelBackground.visibility = View.GONE
                mAddTravelPlatform.visibility = View.GONE
            }
        })
        animatorAlpha.duration = 300
        animatorAlpha.start()
    }



    private fun selectPage(view:View,fragment:Fragment?,type:Int ){
        transBottomButton(view.id)
        val mTransaction = mFragmentManager.beginTransaction()
        hideAllFragment(mTransaction)

        if (fragment == null){
            when(type){
                1 -> {
                    travelFragment = TravelFragment()
                    mTransaction.add(R.id.home_page_fly,travelFragment!!)
                }
                2 -> {
                    mapFragment = MapFragment()

                    var steps = ArrayList<StepBean>()
                    var step1 = StepBean()
                    step1.latitude =28.112444
                    step1.longitude = 112.98381
                    step1.stepName   = "湖南省长沙市"
                    step1.stepMemo = "湖南省长沙市"
                    var step2 = StepBean()
                    step2.latitude =30.651651
                    step2.longitude = 104.075931
                    step2.stepName  = "四川省成都市"
                    step2.stepMemo = "四川省成都市"
                    var step3 = StepBean()
                    step3.latitude =34.265472
                    step3.longitude = 108.954239
                    step3.stepName  = "陕西省西安市"
                    step3.stepMemo = "陕西省西安市"
                    var step4 = StepBean()
                    step4.latitude =36.620901
                    step4.longitude = 101.780199
                    step4.stepName  = "青海省西宁市"
                    step4.stepMemo = "青海省西宁市"
                    var step5 = StepBean()
                    step5.latitude =39.630867
                    step5.longitude = 118.180193
                    step5.stepName  = "河北省唐山市"
                    step5.stepMemo = "河北省唐山市"
                    var step6 = StepBean()
                    step6.latitude =32.916287
                    step6.longitude = 117.389719
                    step6.stepName  = "安徽省蚌埠市"
                    step6.stepMemo = "安徽省蚌埠市"
                    var step7 = StepBean()
                    step7.latitude =32.061707
                    step7.longitude = 118.763232
                    step7.stepName  = "江苏省南京市"
                    step7.stepMemo = "江苏省南京市"
                    var step8 = StepBean()
                    step8.latitude =28.112444
                    step8.longitude = 112.98381
                    step8.stepName   = "湖南省长沙市"
                    step8.stepMemo = "湖南省长沙市"
                    var step9 = StepBean()
                    step9.latitude =30.546498
                    step9.longitude = 114.341861
                    step9.stepName  = "湖北省武汉市"
                    step9.stepMemo = "湖北省武汉市"
                    var step10 = StepBean()
                    step10.latitude =29.874556
                    step10.longitude = 121.550357
                    step10.stepName = "浙江省宁波市"
                    step10.stepMemo = "浙江省宁波市"
                    steps.add(step1)
                    steps.add(step2)
                    steps.add(step3)
                    steps.add(step4)
                    steps.add(step5)
                    steps.add(step6)
                    steps.add(step7)
                    steps.add(step8)
                    steps.add(step9)
                    steps.add(step10)

                    (mapFragment as MapFragment).steps = steps


                    mTransaction.add(R.id.home_page_fly,mapFragment!!)
                }
                3 -> {
                    historyFragment = HistoryFragment()
                    mTransaction.add(R.id.home_page_fly,historyFragment!!)
                }
                4 -> {
                    meFragment = MeFragment()
                    mTransaction.add(R.id.home_page_fly,meFragment!!)
                }
            }

        }else{
            mTransaction.show(fragment!!)
        }
        mTransaction.commitNow()
    }

    /**
     * 底部菜单按钮的切换 和 选中标志的移动
     */
    private fun transBottomButton(viewId:Int){
        var translationIndex = 0
        for ((index,item) in bottomButtons.withIndex()){
            if (item.id == viewId) {
                item.setSelect(true)
                translationIndex = index
            }else if(item.id != viewId && item.isSelect()){
                item.setSelect(false)
            }
        }
        val buttonWidth = Utils.instance().getScreenWidth() / 5
        if(translationIndex > 1){
            translationIndex += 1
        }
        val currentX = selectedView.translationX
        val translationX = (buttonWidth * translationIndex).toFloat()
        val animator = ObjectAnimator.ofFloat(selectedView, "translationX", currentX, translationX)
        animator.duration = 300
        animator.start()
    }

    /**
     * 全部Fragmentが非表示になる
     */
    private fun hideAllFragment(trasaction:FragmentTransaction){
        if (travelFragment != null){
           trasaction.hide(travelFragment!!)
        }
        if (historyFragment != null){
            trasaction.hide(historyFragment!!)
        }
        if (mapFragment != null){
            trasaction.hide(mapFragment!!)
        }
        if (meFragment != null){
            trasaction.hide(meFragment!!)
        }
    }
}
