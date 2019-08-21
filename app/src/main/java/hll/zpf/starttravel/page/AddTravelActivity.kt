package hll.zpf.starttravel.page

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import hll.zpf.starttravel.R
import hll.zpf.starttravel.base.BaseActivity
import hll.zpf.starttravel.common.EventBusMessage
import hll.zpf.starttravel.common.HLogger
import hll.zpf.starttravel.common.Utils
import hll.zpf.starttravel.common.components.CustomSwitchView
import hll.zpf.starttravel.common.components.MemberInfoDialog
import hll.zpf.starttravel.common.database.entity.Member
import hll.zpf.starttravel.common.database.entity.Travel
import hll.zpf.starttravel.common.enums.TravelTypeEnum
import hll.zpf.starttravel.common.model.MemberModel
import hll.zpf.starttravel.common.model.TravelModel
import kotlinx.android.synthetic.main.activity_add_travel.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class AddTravelActivity : BaseActivity() {

    private lateinit var switch:CustomSwitchView

    private lateinit var addTravelPartnerPlatform:LinearLayout

    private lateinit var mScrollView:ScrollView

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

        EventBus.getDefault().register(this)

        switch = findViewById(R.id.travel_type_switch)
        addTravelPartnerPlatform = findViewById(R.id.travel_partner_platform)
        mScrollView = findViewById(R.id.add_travel_scrollView)
        mRootView = findViewById(R.id.root_view)
        mRootView.requestFocus()

        setTitle(
            getString(R.string.add_travel_001),
            true,
            getString(R.string.add_travel_002)){
            when(it.id){
                R.id.left_button -> {//返回
                    finish()
                }
                R.id.right_button -> {//提交
                    commitTravel()
                }
            }
        }


        partnerPlatformHeight =  Utils.instance().DPToPX(50f).toInt()
        val layoutPa = addTravelPartnerPlatform.layoutParams
        layoutPa.height = 0
        addTravelPartnerPlatform.layoutParams = layoutPa

        mTravelPartnerTitle = findViewById(R.id.travel_partner_title)
        setPartnerTitle(0,0f)
        val animator = ObjectAnimator.ofFloat(mTravelPartnerTitle, "translationX", 0f, -400f)
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
                   deleteTravelPartnerView(item)
                }
            }
        }
    }

    private fun setPartnerTitle(number:Int,money:Float){
        val moneyStr = Utils.instance().transMoneyToString(money)
        travel_partner_title.text = String.format(getString(R.string.add_travel_007),number,moneyStr)
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onGetMessage(message: EventBusMessage) {
        if (message.message.equals(TRAVEL_TYPE)){
            travelType = message.travelType
            when(travelType){
                TravelTypeEnum.FREE_TRAVEL ->{
                    travel_money_title.visibility = View.GONE
                    travel_money_layout.visibility = View.GONE
                }
                TravelTypeEnum.MONEY_TRAVEL ->{
                    travel_money_title.visibility = View.VISIBLE
                    travel_money_layout.visibility = View.VISIBLE
                }
            }
        }
    }

    fun clickAction(view: View) {
        super.baseClickAction(view)
        when(view.id){
            R.id.add_travel_partner_text,R.id.add_travel_partner_btn -> {//添加伙伴
                mRootView.requestFocus()
                val memberInfoDialog = MemberInfoDialog(context,travelType)
                val memberModel = ViewModelProviders.of(this).get(MemberModel::class.java)
                val member = Member()
                val imageId = Random().nextInt(100)
                when(imageId % 4){
                    0 -> {
                        member.imageBitmap = Utils.instance().drawableToBitmap(context.getDrawable(R.mipmap.user_image1))
                    }
                    1 -> {
                        member.imageBitmap = Utils.instance().drawableToBitmap(context.getDrawable(R.mipmap.user_image2))
                    }
                    2 -> {
                        member.imageBitmap = Utils.instance().drawableToBitmap(context.getDrawable(R.mipmap.user_image3))
                    }
                    3 -> {
                        member.imageBitmap = Utils.instance().drawableToBitmap(context.getDrawable(R.mipmap.user_image4))
                    }
                }
                memberModel.getMemberData().value = member
                memberInfoDialog.member = memberModel
                val memberView = addTravelPartnerView(memberModel,mAddTravelPartnerClick)
                memberInfoDialog.callBack = {view,commit ->
                    if (!commit){
                       deleteTravelPartnerView(memberView)
                    }
                }
                memberInfoDialog.setCancelable(false)
                memberInfoDialog.show()

                addTravelPartnerPlatform.addView(memberView,addTravelPartnerPlatform.childCount - 1)
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
     * 删除伙伴
     * view：伙伴的view
     */
    private fun deleteTravelPartnerView(view:View){
        mRootView.requestFocus()
        addTravelPartnerPlatform.removeView(view)
        val startValue = partnerPlatformHeight
        partnerPlatformHeight -= Utils.instance().DPToPX(50f).toInt()
        changeViewHeightByAnimation(addTravelPartnerPlatform,startValue,partnerPlatformHeight,200){
            mScrollView.post{
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN)
            }
        }
    }

    /**
     * 添加伙伴
     * member：伙伴的名字
     * click：删除按钮的回调
     */
    private fun addTravelPartnerView(member:MemberModel,click:((View,View) -> Unit)? = null) : View{
        val mView = LayoutInflater.from(this).inflate(R.layout.travel_partner_item_layout,addTravelPartnerPlatform,false);
        val mPartnerNameTextView:TextView = mView.findViewById(R.id.partner_name)
        val mPartnerMoneyView:TextView = mView.findViewById(R.id.partner_add_money)
        val mDeleteButton:TextView = mView.findViewById(R.id.delete_partner_bt)
        val mPartnerImage:ImageView = mView.findViewById(R.id.partner_image)
        member.getMemberData().observe(this, Observer {
            it.imageBitmap?.let { bitmap: Bitmap ->
                mPartnerImage.setImageBitmap(bitmap)
            }
            when(travelType){
                TravelTypeEnum.FREE_TRAVEL ->{
                    mPartnerMoneyView.visibility = View.GONE
                }
                TravelTypeEnum.MONEY_TRAVEL ->{
                    mPartnerMoneyView.visibility = View.VISIBLE
                    mPartnerMoneyView.text = String.format(getString(R.string.money_label),Utils.instance().transMoneyToString(it.money))
                }
            }
            mPartnerNameTextView.text = it.name
        })
        mDeleteButton.setOnClickListener {view ->
            click?.let{
                it(view,mView)
            }
        }
        mView.tag = member
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


    private fun commitTravel(){
        val travelName = travel_name_editText.text.toString()
        val travelMemo = travel_memo_editText.text.toString()
        val travelMoney = travel_money_editText.text.toString()
        val travelMemberType = travel_type_switch.isLeft
        if(travelName == null || travelName.equals("")){
            showMessageAlertDialog("",getString(R.string.add_travel_e01))
            return
        }else if(travelType == TravelTypeEnum.MONEY_TRAVEL && !Utils.instance().checkMoney(travelMoney.toString())){
            showMessageAlertDialog("",getString(R.string.add_travel_e02))
            return
        }else if(!travelMemberType && travel_partner_platform.childCount - 1 < 1){
            showMessageAlertDialog("",getString(R.string.add_travel_e03))
            return
        }

        val travel = Travel()
        travel.name = travelName
        if(travelType == TravelTypeEnum.MONEY_TRAVEL) {
            travel.money = travelMoney.toFloat()
        }else{
            travel.money = 0f
        }
        travel.memo = travelMemo
        if(travelMemberType){

        }


    }
    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


}
