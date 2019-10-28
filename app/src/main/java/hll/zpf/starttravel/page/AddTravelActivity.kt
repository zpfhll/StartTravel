package hll.zpf.starttravel.page

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import hll.zpf.starttravel.R
import hll.zpf.starttravel.base.BaseActivity
import hll.zpf.starttravel.common.EventBusMessage
import hll.zpf.starttravel.common.HLogger
import hll.zpf.starttravel.common.Utils
import hll.zpf.starttravel.common.components.CustomConstraintLayout
import hll.zpf.starttravel.common.components.CustomSwitchView
import hll.zpf.starttravel.common.components.MemberInfoDialog
import hll.zpf.starttravel.common.enums.TravelTypeEnum
import kotlinx.android.synthetic.main.activity_add_travel.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.SpannableString
import android.text.TextWatcher
import hll.zpf.starttravel.BuildConfig
import hll.zpf.starttravel.common.UserData
import hll.zpf.starttravel.common.database.DataManager
import hll.zpf.starttravel.common.database.entity.Detail
import hll.zpf.starttravel.common.database.entity.DetailWithMember
import hll.zpf.starttravel.common.enums.ActivityMoveEnum
import hll.zpf.starttravel.common.database.entity.Member
import hll.zpf.starttravel.common.database.entity.Travel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList


class AddTravelActivity : BaseActivity() {

    private lateinit var switch:CustomSwitchView

    private lateinit var addTravelPartnerPlatform:LinearLayout

    private lateinit var mScrollView:ScrollView

    private lateinit var mTravelPartnerTitle:TextView

    private lateinit var mRootView: ConstraintLayout

    private var mAddTravelPartnerClick:((View, View) -> Unit)? = null

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
            getString(R.string.add_travel_002),
            R.drawable.back_button_background){
            when(it.id){
                R.id.left_button -> {//返回
                    onKeyCodeBackListener()
                }
                R.id.right_button -> {//提交
                    commitTravel()
                }
            }
        }

        travel_money_editText.addTextChangedListener(object : TextWatcher {
            var beforeText = ""
            override fun afterTextChanged(s: Editable?) {
                var text = s.toString().replace(",","")
                var isTwo = false
                if(text.isEmpty() || text.startsWith(".")){
                    text = "0"
                }else if(text.split(".").size > 1 && text.split(".")[1].length > 1){
                    text = text.substring(0,text.length - 1)
                    isTwo = true
                }
                if(text.toFloat() > BuildConfig.MAX_MONEY){
                    text = beforeText.replace(",","")
                    isTwo = true
                }
                val money = Utils.instance().transMoneyToString(text.toFloat())
                HLogger.instance().e("afterTextChanged",money)
                HLogger.instance().e("afterTextChanged beforeText",beforeText)
                if((!beforeText.equals(money) && beforeText.length <= money.length) || isTwo || beforeText.length > money.length) {
                    travel_money_editText.setText(money)
                    travel_money_editText.setSelection(travel_money_editText.text.length)
                }
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                beforeText = s.toString()
                HLogger.instance().e("beforeTextChanged",beforeText)
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
            }
        })


        partnerPlatformHeight =  Utils.instance().DPToPX(50f).toInt()
        val layoutPa = addTravelPartnerPlatform.layoutParams
        layoutPa.height = 0
        addTravelPartnerPlatform.layoutParams = layoutPa

        mTravelPartnerTitle = findViewById(R.id.travel_partner_title)
        setPartnerTitle()
        val animator = ObjectAnimator.ofFloat(mTravelPartnerTitle, "translationX", 0f, -450f)
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

        //partner的view中的点击事件
        mAddTravelPartnerClick = {view,item ->
            val member = item.tag as Member
            when (view.id) {
                R.id.delete_partner_bt -> {//删除partner
                    HLogger.instance().e("mAddTravelPartnerClick--->","delete partner ${member.id}")
                   deleteTravelPartnerView(item)
                }
                R.id.partner_root_view -> {//修改partner
                    HLogger.instance().e("mAddTravelPartnerClick--->","modify partner ${member.id}")
                    showMemberInfoDialog(member) { _, newMember ->
                        if (newMember != null){
                            EventBus.getDefault().post(newMember)
                        }
                    }
                }
            }
        }
    }

    /**
     * 设定所有partner的情报
     */
    private fun setPartnerTitle(){
        var number:Int = 0
        var money:Float = 0f
        for (i in 0 until  addTravelPartnerPlatform.childCount){
            if(addTravelPartnerPlatform.getChildAt(i).tag != null && addTravelPartnerPlatform.getChildAt(i).tag is Member){
                number += 1
                money += (addTravelPartnerPlatform.getChildAt(i).tag as Member).money ?: 0f
            }
        }
        val moneyStr = Utils.instance().transMoneyToString(money)
        var showString= when(travelType){
            TravelTypeEnum.MONEY_TRAVEL -> String.format(getString(R.string.add_travel_007),number,"  ",moneyStr)
            TravelTypeEnum.FREE_TRAVEL -> String.format(getString(R.string.add_travel_007_1),number)
        }
        val spannableString = SpannableString(showString)
        val colorSpan = ForegroundColorSpan(getColor(R.color.light_red))
        spannableString.setSpan(colorSpan, 4, spannableString.length - 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        travel_partner_title.text = spannableString
    }


    /**
     * 接受EVentBus消息
     */
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
                val member = Member.createMember()
                val imageId = Random().nextInt(100)
                when(imageId % 4){
                    0 -> {
                        member.setImageBitmap(Utils.instance().drawableToBitmap(context.getDrawable(R.mipmap.user_image1)))
                    }
                    1 -> {
                        member.setImageBitmap(Utils.instance().drawableToBitmap(context.getDrawable(R.mipmap.user_image2)))
                    }
                    2 -> {
                        member.setImageBitmap(Utils.instance().drawableToBitmap(context.getDrawable(R.mipmap.user_image3)))
                    }
                    3 -> {
                        member.setImageBitmap(Utils.instance().drawableToBitmap(context.getDrawable(R.mipmap.user_image4)))
                    }
                }
                showMemberInfoDialog(member) {view,member ->
                    if (member != null){
                        val memberView = addTravelPartnerView(context,member,travelType,addTravelPartnerPlatform,mAddTravelPartnerClick)
                        addTravelPartnerView(memberView)
                    }
                }
            }
        }
    }

    /**
     * 显示人员信息弹框
     */
    private fun showMemberInfoDialog(member: Member,callBack:((View,Member?) -> Unit)){
        val memberInfoDialog = MemberInfoDialog(context,travelType)
        memberInfoDialog.member = member
        memberInfoDialog.callBack = callBack
        memberInfoDialog.setCancelable(false)
        memberInfoDialog.show()
    }


    /**
     * 删除伙伴
     * view：伙伴的view
     */
    private fun deleteTravelPartnerView(view:View){
        mRootView.requestFocus()
        val subView = view as CustomConstraintLayout
        subView.unRegistEventBus()
        addTravelPartnerPlatform.removeView(subView)
        setPartnerTitle()
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
     * view：伙伴的view
     */
    private fun addTravelPartnerView(view:View){
        addTravelPartnerPlatform.addView(view,addTravelPartnerPlatform.childCount - 1)
        setPartnerTitle()
        val startValue = partnerPlatformHeight
        partnerPlatformHeight += Utils.instance().DPToPX(50f).toInt()
        changeViewHeightByAnimation(addTravelPartnerPlatform,startValue,partnerPlatformHeight,400){
            mScrollView.post{
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN)
            }
        }
    }

    /**
     * 生成伙伴item
     * context：页面上下文
     * member：伙伴的名字
     * type：旅行模式
     * root：根VIEW
     * click：删除按钮的回调
     */
    private fun addTravelPartnerView(context: Context, member:Member,type:TravelTypeEnum,root:ViewGroup,click:((View, View) -> Unit)? = null) : View{
        val mView = LayoutInflater.from(context).inflate(R.layout.travel_partner_item_layout,root,false) as CustomConstraintLayout
        val mPartnerNameTextView:TextView = mView.findViewById(R.id.partner_name)
        val mPartnerMoneyView:TextView = mView.findViewById(R.id.partner_add_money)
        val mDeleteButton:TextView = mView.findViewById(R.id.delete_partner_bt)
        val mPartnerImage:ImageView = mView.findViewById(R.id.travel_image)

        member.getImageBitmap()?.let {
            mPartnerImage.setImageBitmap(it)
        }
        when(type){
            TravelTypeEnum.FREE_TRAVEL ->{
                mPartnerMoneyView.visibility = View.GONE
            }
            TravelTypeEnum.MONEY_TRAVEL ->{
                mPartnerMoneyView.visibility = View.VISIBLE
                mPartnerMoneyView.text = String.format(getString(R.string.money_label),Utils.instance().transMoneyToString(member.money!!))
            }
        }
        member.name?.let {
            mPartnerNameTextView.text = it
        }

        mDeleteButton.setOnClickListener {view ->
            click?.let{
                it(view,mView)
            }
        }
        mView.setOnClickListener{view ->
            click?.let{
                it(view,mView)
            }
        }


        mView.registEventBus()
        mView.update = {updateMember ->
            updateMember.getImageBitmap()?.let {
                mPartnerImage.setImageBitmap(it)
            }
            when(type){
                TravelTypeEnum.FREE_TRAVEL ->{
                    mPartnerMoneyView.visibility = View.GONE
                }
                TravelTypeEnum.MONEY_TRAVEL ->{
                    mPartnerMoneyView.visibility = View.VISIBLE
                    mPartnerMoneyView.text = String.format(getString(R.string.money_label),Utils.instance().transMoneyToString(updateMember.money ?: 0f))
                }
            }
            updateMember.name?.let {
                mPartnerNameTextView.text = it
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


    /**
     * 提交
     */
    private fun commitTravel(){
        val travelName = travel_name_editText.text.toString()
        val travelMemo = travel_memo_editText.text.toString()
        val travelMoney = travel_money_editText.text.toString()
        val travelMemberType = travel_type_switch.isLeft
        if(travelName.isBlank()){
            showMessageAlertDialog("",getString(R.string.add_travel_e01))
            return
        }else if(travelType == TravelTypeEnum.MONEY_TRAVEL && !Utils.instance().checkMoney(travelMoney.replace(",",""))){
            showMessageAlertDialog("",getString(R.string.add_travel_e02))
            return
        }else if(!travelMemberType && travel_partner_platform.childCount - 1 < 1){
            showMessageAlertDialog("",getString(R.string.add_travel_e03))
            return
        }
        val travel = Travel.createTravel()
        travel.name = travelName
        travel.memo = travelMemo
        travel.userId = UserData.instance().getLoginUserId()
        val members = ArrayList<Member>()
        val details = ArrayList<Detail>()
        val detailWithMembers = ArrayList<DetailWithMember>()
        for (i in 0 until  addTravelPartnerPlatform.childCount){
            addTravelPartnerPlatform.getChildAt(i).tag?.let {
                if(it is Member){
                    it.travelId = travel.id

                    if(travelType == TravelTypeEnum.MONEY_TRAVEL && (it.money ?: 0f) > 0f){
                            val detail = Detail.createDetail()
                            detail.memo = it.name ?: ""
                            detail.money = it.money
                            detail.type = 0
                            detail.travelId = travel.id
                            val detailWithMember =  DetailWithMember.createDetailWithMember()
                            detailWithMember.detailId  = detail.id
                            detailWithMember.memberId = it.id
                            detailWithMember.travelId = travel.id
                            detailWithMember.memberType = 1
                            detailWithMember.money = it.money ?: 0f

                            details.add(detail)
                            detailWithMembers.add(detailWithMember)
                    }
                    members.add(it)
                }
            }
        }
        val self = Member.createMember()
        self.id = UserData.instance().getLoginUserId()
        self.name = getString(R.string.add_detail_013)
        self.travelId = travel.id
        if(travelType == TravelTypeEnum.MONEY_TRAVEL){
            self.money = travelMoney.replace(",","").toFloat()
            if((self.money ?: 0f) > 0f){
                val detail = Detail.createDetail()
                detail.memo = self.name ?: ""
                detail.money = self.money
                detail.type = 0
                detail.travelId = travel.id
                val detailWithMember =  DetailWithMember.createDetailWithMember()
                detailWithMember.detailId  = detail.id
                detailWithMember.memberId = self.id
                detailWithMember.travelId = travel.id
                detailWithMember.memberType = 1
                detailWithMember.money = self.money ?: 0f
                details.add(0,detail)
                detailWithMembers.add(0,detailWithMember)
            }
        }
        members.add(self)
        travel.type = when(travelType){
            TravelTypeEnum.MONEY_TRAVEL -> 0
            TravelTypeEnum.FREE_TRAVEL -> 1
        }
        val dataManager = DataManager()
        dataManager.insertOrReplaceTravel(travel,members){
            if(it == BuildConfig.NORMAL_CODE){
                if(travelType == TravelTypeEnum.MONEY_TRAVEL){
                    dataManager.insertDetail(details,detailWithMembers){resultCode ->
                        if(resultCode == BuildConfig.NORMAL_CODE) {
                            val message = EventBusMessage()
                            message.message = REFRESH_TRAVEL_DATA
                            EventBus.getDefault().post(message)
                            finish()
                            baseStartActivity(null,ActivityMoveEnum.BACK_FROM_LEFT)
                        }else {
                            showMessageAlertDialog("","${getString(R.string.DATABASE_ERROR)}($resultCode)")
                        }
                    }
                }else{
                    val message = EventBusMessage()
                    message.message = REFRESH_TRAVEL_DATA
                    EventBus.getDefault().post(message)
                    finish()
                    baseStartActivity(null,ActivityMoveEnum.BACK_FROM_LEFT)
                }
            }else{
                showMessageAlertDialog("","${getString(R.string.DATABASE_ERROR)}($it)")
            }
        }
    }

    override fun onKeyCodeBackListener() {
        super.onKeyCodeBackListener()
        finish()
        baseStartActivity(null,ActivityMoveEnum.BACK_FROM_LEFT)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


}
