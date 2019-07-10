package hll.zpf.starttravel.page

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import hll.zpf.starttravel.R
import hll.zpf.starttravel.base.BaseActivity
import hll.zpf.starttravel.common.HLogger
import hll.zpf.starttravel.common.Utils
import hll.zpf.starttravel.common.components.CustomSwitchView
import hll.zpf.starttravel.common.enums.TravelTypeEnum

class AddTravelActivity : BaseActivity() {

    private lateinit var switch:CustomSwitchView

    private lateinit var addTravelPartnerPlatform:LinearLayout

    private lateinit var mScrollView:ScrollView

    private lateinit var mAddTravelPartner:LinearLayout

    private lateinit var mModifyTravelPartner:TextView

    private lateinit var mTravelPartnerTitle:TextView

    private lateinit var mRootView: ConstraintLayout

    var mAddTravelPartnerClick:((View,View) -> Unit)? = null

    private var partnerPlatformHeight:Int = 0

    private var travelType:TravelTypeEnum = TravelTypeEnum.MONEY_TRAVEL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_travel)
        setStatusBarColor(R.color.gray)
        switch = findViewById(R.id.travel_type_switch)
        addTravelPartnerPlatform = findViewById(R.id.travel_partner_platform)
        mScrollView = findViewById(R.id.add_travel_scrollView)
        mRootView = findViewById(R.id.root_view)
        mRootView.requestFocus()

        travelType = this.intent.getSerializableExtra("travelType") as TravelTypeEnum

        when(travelType){
            TravelTypeEnum.FREE_TRAVEL ->{
                findViewById<TextView>(R.id.travel_money_title).visibility = View.GONE
                findViewById<LinearLayout>(R.id.travel_money_layout).visibility = View.GONE
            }
            TravelTypeEnum.MONEY_TRAVEL ->{
                findViewById<TextView>(R.id.travel_money_title).visibility = View.VISIBLE
                findViewById<LinearLayout>(R.id.travel_money_layout).visibility = View.VISIBLE
            }
        }

        setTitle(
            getString(R.string.add_travel_001),
            true,
            getString(R.string.add_travel_002)){
            when(it.id){
                R.id.left_button -> {//返回
                    finish()
                }
                R.id.right_button -> {//提交

                }
            }
        }


        partnerPlatformHeight =  Utils.instance().DPToPX(50f).toInt()
        val layoutPa = addTravelPartnerPlatform.layoutParams
        layoutPa.height = 0
        addTravelPartnerPlatform.layoutParams = layoutPa

        mTravelPartnerTitle = findViewById(R.id.travel_partner_title)
        val animator = ObjectAnimator.ofFloat(mTravelPartnerTitle, "translationX", 0f, -200f)
        animator.duration = 0
        animator.start()


        switch.switchAction = {

            val titleAnimator:ObjectAnimator = if(!it) {
                ObjectAnimator.ofFloat(
                    mTravelPartnerTitle,
                    "translationX",
                    mTravelPartnerTitle.translationX,
                    0f
                )
            }else {
                ObjectAnimator.ofFloat(
                    mTravelPartnerTitle,
                    "translationX",
                    0f,
                    -(mTravelPartnerTitle.width + Utils.instance().DPToPX(16f))
                )
            }
            titleAnimator.duration = 600
            titleAnimator.start()

            HLogger.instance().e("--->","switch left : $it")
            val valueStart  = if (!it) 0 else partnerPlatformHeight
            val valueEnd = if (!it) partnerPlatformHeight else 0
            changeViewHeightByAnimation(addTravelPartnerPlatform,valueStart,valueEnd,500){
                mScrollView.post{
                    mScrollView.fullScroll(ScrollView.FOCUS_DOWN)
                }
            }
        }

        mModifyTravelPartner = findViewById(R.id.modify_travel_partner)
        mModifyTravelPartner.setOnClickListener {
            clickAction(it)
        }

        mAddTravelPartnerClick = {view,item ->
            HLogger.instance().e("mAddTravelPartnerClick--->","view: ${view.id}")
            when (view.id) {
                R.id.delete_partner_bt -> {
                    mRootView.requestFocus()
                    addTravelPartnerPlatform.removeView(item)
                    val startValue = partnerPlatformHeight
                    partnerPlatformHeight -= Utils.instance().DPToPX(50f).toInt()
                    changeViewHeightByAnimation(addTravelPartnerPlatform,startValue,partnerPlatformHeight,200){
                        mScrollView.post{
                            mScrollView.fullScroll(ScrollView.FOCUS_DOWN)
                        }
                    }
                }
            }
        }
    }

    fun clickAction(view: View) {
        super.baseClickAction(view)
        when(view.id){
            R.id.add_travel_partner_text,R.id.add_travel_partner_btn -> {//添加伙伴
                mRootView.requestFocus()
                addTravelPartnerPlatform.addView(addTravelPartnerView("玲莉","+1,234元${addTravelPartnerPlatform.childCount - 1}",null,mAddTravelPartnerClick),addTravelPartnerPlatform.childCount - 1)
                val startValue = partnerPlatformHeight
                partnerPlatformHeight += Utils.instance().DPToPX(50f).toInt()
                changeViewHeightByAnimation(addTravelPartnerPlatform,startValue,partnerPlatformHeight,400){
                    mScrollView.post{
                        mScrollView.fullScroll(ScrollView.FOCUS_DOWN)
                    }
                }
            }
            R.id.modify_travel_partner -> {//编辑伙伴  功能暂缓

            }
        }

    }

    /**
     * 添加伙伴
     * name：伙伴的名字
     * money：伙伴贡献的金额
     * partnerImage：伙伴的头像
     * click：删除按钮的回调
     */
    private fun addTravelPartnerView(name:String, money:String, partnerImage: Drawable? = null,click:((View,View) -> Unit)? = null) : View{
        val mView = LayoutInflater.from(this).inflate(R.layout.travel_partner_item_layout,addTravelPartnerPlatform,false);
        val mPartnerNameTextView:TextView = mView.findViewById(R.id.partner_name)
        val mPartnerMoneyView:TextView = mView.findViewById(R.id.partner_add_money)
        val mDeleteButton:TextView = mView.findViewById(R.id.delete_partner_bt)
        val mPartnerImage:ImageView = mView.findViewById(R.id.partner_image)
        when(travelType){
            TravelTypeEnum.FREE_TRAVEL ->{
                mPartnerMoneyView.visibility = View.GONE
            }
            TravelTypeEnum.MONEY_TRAVEL ->{
                mPartnerMoneyView.visibility = View.VISIBLE
            }
        }
        partnerImage?.let {
            mPartnerImage.setImageDrawable(it)
        }
        mPartnerMoneyView.text = money
        mPartnerNameTextView.text = name
        mDeleteButton.setOnClickListener {view ->
            click?.let{
                it(view,mView)
            }
        }
        return mView
    }

    /**
     * 调整view的高度
     * view:变更对象
     * valueStart：开始高度
     * valueEnd：目标高度
     * duration：动画时间，0时为无动画
     * onAnimationEndCallBack：移动后的回调函数
     */
    private fun changeViewHeightByAnimation(view:View,valueStart:Int,valueEnd:Int,duration:Long,onAnimationEndCallBack:(() -> Unit) ? = null){
        val valueAnimator = ValueAnimator.ofInt(valueStart, valueEnd)
        valueAnimator.duration = duration
        valueAnimator.addUpdateListener { animation ->
            val layoutPa = view.layoutParams
            layoutPa.height = animation.animatedValue as Int
            view.layoutParams = layoutPa
        }
        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {
                onAnimationEndCallBack?.let {
                    it()
                }
            }

            override fun onAnimationRepeat(animation: Animator?) {}

            override fun onAnimationCancel(animation: Animator?) {}
        })
        valueAnimator.start()
    }


}
