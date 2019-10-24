package hll.zpf.starttravel.page

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import hll.zpf.starttravel.BuildConfig
import hll.zpf.starttravel.R
import hll.zpf.starttravel.base.BaseActivity
import hll.zpf.starttravel.common.EventBusMessage
import hll.zpf.starttravel.common.HLogger
import hll.zpf.starttravel.common.UserData
import hll.zpf.starttravel.common.Utils
import hll.zpf.starttravel.common.database.DataManager
import hll.zpf.starttravel.common.database.entity.Detail
import hll.zpf.starttravel.common.database.entity.DetailWithMember
import hll.zpf.starttravel.common.database.entity.Member
import hll.zpf.starttravel.common.enums.ActivityMoveEnum
import hll.zpf.starttravel.common.model.TravelModel
import kotlinx.android.synthetic.main.activity_add_detail.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

@SuppressLint("SetTextI18n")
class AddDetailActivity : BaseActivity() {
    private lateinit var inDetailMembers:MutableList<DetailWithMember>
    private lateinit var outDetailMembers:MutableList<DetailWithMember>
    private lateinit var mInMemberAdapter:TravelDetailMemberAdapter
    private lateinit var detail:Detail

    private val EVENTBUS_MESSAGE_SWITCH_SPLIT = "SWITCH_SPLIT"

    private var inputMoney:Float = 0f

    var notAssignMoney:Float = 0f
    var assignMoney:Float = 0f

    private var travelModel: TravelModel? = null

    var walletMoney:Float = 0f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_detail)
        EventBus.getDefault().register(this)
        in_checkbox.checkCallback = {
            if(it && in_checkbox.isChecked){
                out_checkbox.check(!it)
                changeMemberPlatform(true)
            }
        }
        out_checkbox.checkCallback = {
            if(it && out_checkbox.isChecked){
                in_checkbox.check(!it)
                changeMemberPlatform(false)
            }
        }

        money_title.text = "${getString(R.string.add_detail_005)}${getString(R.string.add_detail_003)}"

        split_type_switch.switchAction = {
            not_assign_money.visibility = if(it) View.GONE else View.VISIBLE
            switchSplit(it)
        }

        not_assign_money.visibility = if(split_type_switch.isLeft) View.GONE else View.VISIBLE

        detail_money.addTextChangedListener(object : TextWatcher {
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

                val bound = if(in_checkbox.isChecked) BuildConfig.MAX_MONEY - walletMoney else walletMoney

                if(text.toFloat() > bound){
                    text = beforeText.replace(",","")
                    isTwo = true
                    showMessageAlertDialog("","${if(in_checkbox.isChecked) getString(R.string.add_detail_E08) else getString(R.string.add_detail_E07)}")
                }
                val money = Utils.instance().transMoneyToString(text.toFloat())
                HLogger.instance().e("detail_money afterTextChanged",money)
                HLogger.instance().e("detail_money afterTextChanged beforeText",beforeText)
                if((!beforeText.equals(money) && beforeText.length <= money.length) || isTwo || beforeText.length > money.length) {
                    detail_money.setText(money)
                    detail_money.setSelection(detail_money.text.length)
                }

                inputMoney = text.toFloat()
                detail.money = inputMoney
                switchSplit(split_type_switch.isLeft)

                notAssignMoney = inputMoney - assignMoney
                val notAssignStr = Utils.instance().transMoneyToString(notAssignMoney)
                not_assign_money.text =
                    String.format(getString(R.string.add_detail_012),notAssignStr)
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                beforeText = s.toString()
                HLogger.instance().e("detail_money beforeTextChanged",beforeText)
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
            }
        })

        detail_memo.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                detail.memo = detail_memo.text.toString()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })
    }

    private fun switchSplit(isSplit:Boolean){
        var checkedNum = 0

        for (member in outDetailMembers){
            checkedNum = if (member.isSelected) checkedNum + 1 else checkedNum
        }
        if (checkedNum == 0){
            return
        }

        val eventBusMessage = EventBusMessage()
        eventBusMessage.message = EVENTBUS_MESSAGE_SWITCH_SPLIT
        eventBusMessage.memberIsSplit = isSplit
        eventBusMessage.memberSplitMoney = Utils.instance().transMoneyToString(inputMoney/checkedNum)
        EventBus.getDefault().post(eventBusMessage)
    }

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN, sticky = true)
    fun initData(message: EventBusMessage){

        when(message.message){
            ADD_DETAIL -> {
                travelModel = message.travel
                var isIn = false
                in_checkbox.check(false)
                out_checkbox.check(true)
                walletMoney = (travelModel?.getTravelData()?.value?.inMoney ?: 0f) - (travelModel?.getTravelData()?.value?.outMoney ?: 0f)
                wallet_money_text.text = "(${getString(R.string.add_detail_014)}$walletMoney)"

                initPlatform(isIn,travelModel?.getTravelData()?.value?.id)
                val title = travelModel?.getTravelData()?.value?.name ?: ""
                setTitle(
                    title,
                    true,
                    getString(R.string.add_detail_011),
                    R.drawable.back_button_background){
                    when(it.id){
                        R.id.left_button -> {//返回
                            onKeyCodeBackListener()
                        }
                        R.id.right_button -> {//添加
                            commit()
                        }
                    }
                }
            }
            SHOW_DETAIL -> {
                val detail = message.detail
                travelModel = message.travel
                setTitle(
                    travelModel?.getTravelData()?.value?.name ?: "",
                    false,
                    "",
                    R.drawable.back_button_background){
                    when(it.id){
                        R.id.left_button -> {//返回
                            onKeyCodeBackListener()
                        }
                    }
                }
                wallet_money_text.visibility = View.GONE
                initShowPlatform(detail!!)
            }
        }
    }

    private fun commit(){

        if(detail_money.text.isEmpty() || detail_money.text.toString().equals("0")){
            showMessageAlertDialog("",getString(R.string.add_detail_E03))
            return
        }else if(!in_checkbox.isChecked && detail_memo.text.isEmpty()){
            showMessageAlertDialog("",getString(R.string.add_detail_E04))
            return
        }
        val travel = travelModel?.getTravelData()?.value
        val detailWithMembers = mutableListOf<DetailWithMember>()
        if(in_checkbox.isChecked){
            var isChecked = false
            detail.type = 0
            for (member in inDetailMembers){
                if (member.isSelected){
                    isChecked = true
                    detail.memo = member.memberName
                    detailWithMembers.add(member)
                }
            }
            if(!isChecked){
                showMessageAlertDialog("",getString(R.string.add_detail_E02))
                return
            }
            travel?.inMoney = (travel?.inMoney ?: 0f) + inputMoney
        }else{
            var isChecked = false
            detail.type = 1
            for (member in outDetailMembers){
                if (member.isSelected){
                    isChecked = true
                    detailWithMembers.add(member)
                }
            }

            if(walletMoney < inputMoney) {
                showMessageAlertDialog("",getString(R.string.add_detail_E07))
            }

            if(!split_type_switch.isLeft && notAssignMoney != 0f){
                showMessageAlertDialog("",getString(R.string.add_detail_E01))
                return
            }else if (split_type_switch.isLeft){
                if(!isChecked){
                    showMessageAlertDialog("",getString(R.string.add_detail_E01))
                    return
                }
            }
            travel?.outMoney = (travel?.outMoney ?: 0f) + inputMoney
        }
       dataManager?.insertDetail(listOf(detail),detailWithMembers){
           if(it == BuildConfig.NORMAL_CODE){
               travelModel?.getTravelData()?.value = travel
               val event = EventBusMessage.instance(REFRESH_TRAVEL_DETAIL)
               event.travel = travelModel
               EventBus.getDefault().post(event)
               showMessageAlertDialog("",getString(R.string.add_detail_E06)){_,_ ->
                   onKeyCodeBackListener()
               }
           }else{
               showMessageAlertDialog("","${getString(R.string.add_detail_E05)}($it)")
           }
       }
    }

    private fun changeMemberPlatform(isIn:Boolean){

        money_title.text = "${if(!isIn) getString(R.string.add_detail_005) else getString(R.string.add_detail_006)}${getString(R.string.add_detail_003)}"

        val moveDistance = Utils.instance().getScreenWidth() - Utils.instance().DPToPX(24f)
        val inMoveDistance = if (isIn) 0f else moveDistance
        val outMoveDistance = if (isIn) -moveDistance else 0f

        val objectAnimatorOut = ObjectAnimator.ofFloat(
            out_detail_platform,
            "translationX",
            out_detail_platform.translationX,
            outMoveDistance
        )
        objectAnimatorOut.duration = 500
        objectAnimatorOut.start()

        val objectAnimatorIn = ObjectAnimator.ofFloat(
            in_detail_platform,
            "translationX",
            in_detail_platform.translationX,
            inMoveDistance
        )
        objectAnimatorIn.duration = 500
        objectAnimatorIn.start()

    }

    private fun memberCheck(position:Int,isIn:Boolean,isChecked:Boolean){
            if(isIn){
                for (i in 0 until inDetailMembers.size) {
                    if (i == position) {
                        inDetailMembers[position].isSelected = isChecked
                        if(isChecked){
                            inDetailMembers[position].money = if(detail_money.text.isEmpty()) 0f else detail_money.text.toString().replace(",","").toFloat()
                        }
                    }else {
                        if (isChecked) {
                            inDetailMembers[i].isSelected = !isChecked
                        }
                    }
                }
            }else{
                outDetailMembers[position].isSelected = isChecked
                switchSplit(split_type_switch.isLeft)
            }
    }

    private fun memberSetMoney(position:Int,money:Float){
        outDetailMembers[position].money = money
        assignMoney = 0f
        for (detailMember in outDetailMembers){
            assignMoney += detailMember.money
        }
        notAssignMoney = inputMoney - assignMoney

        if(notAssignMoney < 0){
            detail_money.setText(Utils.instance().transMoneyToString(assignMoney))
        }else {
            val notAssignStr = Utils.instance().transMoneyToString(notAssignMoney)
            not_assign_money.text =
                String.format(getString(R.string.add_detail_012), notAssignStr)
        }
    }

    private fun initShowPlatform(detail:Detail){
        var isIn = true
        when (detail!!.type) {
            0 -> {
                isIn = true
                in_checkbox.check(true)
                out_checkbox.check(false)
            }
            1 -> {
                isIn = false
                in_checkbox.check(false)
                out_checkbox.check(true)
            }
        }

        detail_money.setText(Utils.instance().transMoneyToString(detail.money ?: 0f))
        detail_memo.setText(detail.memo)

        out_checkbox.isEnabled = false
        in_checkbox.isEnabled = false
        detail_money.isEnabled = false
        detail_memo.isEnabled = false
        split_type_switch.visibility = View.GONE

        dataManager = DataManager()
        inDetailMembers = mutableListOf()
        outDetailMembers = mutableListOf()

        detail_out_member_list.layoutManager = LinearLayoutManager(context)
        detail_in_member_list.layoutManager =  LinearLayoutManager(context)
        dataManager?.getDetailWithMemberByDetail(detail.id) { resultCode, data ->
            if (resultCode == BuildConfig.NORMAL_CODE) {

                var detailMemberAdapter:TravelDetailMemberAdapter?
                if(isIn) {
                    val moveDistance = Utils.instance().getScreenWidth() - Utils.instance().DPToPX(24f)
                    val objectAnimator = ObjectAnimator.ofFloat(
                        out_detail_platform,
                        "translationX",
                        out_detail_platform.translationX,
                        -moveDistance
                    )
                    objectAnimator.duration = 0
                    objectAnimator.start()
                }else{
                    val moveDistance = Utils.instance().getScreenWidth() - Utils.instance().DPToPX(24f)
                    val objectAnimator = ObjectAnimator.ofFloat(
                        in_detail_platform,
                        "translationX",
                        in_detail_platform.translationX,
                        moveDistance
                    )
                    objectAnimator.duration = 0
                    objectAnimator.start()
                }
                if(isIn) {
                    inDetailMembers.addAll(data)
                    detailMemberAdapter = TravelDetailMemberAdapter(this,inDetailMembers,isIn)
                    detailMemberAdapter.isShow = true
                    detail_in_member_list.adapter = detailMemberAdapter
                    val inLayout = detail_in_member_list.layoutParams as ConstraintLayout.LayoutParams
                    inLayout.height =
                        Utils.instance().DPToPX((20f + 1f + 16f + 16f + 2f) * inDetailMembers.size)
                            .toInt()
                    inLayout.width =
                        Utils.instance().getScreenWidth() - Utils.instance().DPToPX(24 * 2f).toInt()
                    detail_in_member_list.layoutParams = inLayout
                }else{
                    outDetailMembers.addAll(data)
                    detailMemberAdapter = TravelDetailMemberAdapter(this,outDetailMembers,isIn)
                    detailMemberAdapter.isShow = true
                    detail_out_member_list.adapter = detailMemberAdapter
                    val outLayout = detail_out_member_list.layoutParams as ConstraintLayout.LayoutParams
                    outLayout.height =
                        Utils.instance().DPToPX((20f + 1f + 16f + 16f + 2f) * outDetailMembers.size)
                            .toInt()
                    outLayout.width =
                        Utils.instance().getScreenWidth() - Utils.instance().DPToPX(24 * 2f).toInt()
                    detail_out_member_list.layoutParams = outLayout
                }
                detailMemberAdapter.notifyDataSetChanged()
            }else{
                showMessageAlertDialog("","${getString(R.string.DATABASE_ERROR)}($resultCode)"){_,_ ->
                    onKeyCodeBackListener()
                }
            }
        }


    }

    /**
     * 添加明细时的初始化
     */
    private fun initPlatform(isIn:Boolean,travelId:String?) {
        dataManager = DataManager()
        detail  = Detail.createDetail()
        detail.travelId = travelId ?: ""
        inDetailMembers = mutableListOf()
        outDetailMembers = mutableListOf()
        mInMemberAdapter = TravelDetailMemberAdapter(this,inDetailMembers,true){ index, mIsIn, isChecked ->
            memberCheck(index,mIsIn,isChecked)
        }
        val mOutMemberAdapter = TravelDetailMemberAdapter(this,outDetailMembers,false){ index, mIsIn, isChecked ->
            memberCheck(index,mIsIn,isChecked)
        }
        mOutMemberAdapter.mEditCallback = {index,money ->
            memberSetMoney(index,money)
        }
        detail_out_member_list.adapter = mOutMemberAdapter
        detail_in_member_list.adapter = mInMemberAdapter
        detail_out_member_list.layoutManager = LinearLayoutManager(context)
        detail_in_member_list.layoutManager =  LinearLayoutManager(context)
        if (isIn){
            val moveDistance = Utils.instance().getScreenWidth() - Utils.instance().DPToPX(24f)
            val objectAnimator = ObjectAnimator.ofFloat(
                out_detail_platform,
                "translationX",
                out_detail_platform.translationX,
                -moveDistance
            )
            objectAnimator.duration = 0
            objectAnimator.start()
        }else{
            val moveDistance = Utils.instance().getScreenWidth() - Utils.instance().DPToPX(24f)
            val objectAnimator = ObjectAnimator.ofFloat(
                in_detail_platform,
                "translationX",
                in_detail_platform.translationX,
                moveDistance
            )
            objectAnimator.duration = 0
            objectAnimator.start()
        }
        dataManager?.getPartnerByTravelId(travelId) { resultCode, data ->
            if (resultCode == BuildConfig.NORMAL_CODE) {
                for (member in data) {
                    val detailMember = DetailWithMember.createDetailWithMember()
                    detailMember.memberId = member.id
                    detailMember.memberName = member.name ?: ""
                    detailMember.travelId = travelId ?: ""
                    detailMember.memberType =
                        if (member.id.equals(UserData.instance().getLoginUserId())) 0 else 1
                    detailMember.detailId = detail.id
                    if (member.id.equals(UserData.instance().getLoginUserId())) {
                        inDetailMembers.add(0, detailMember)
                        outDetailMembers.add(0, detailMember.copy().copyInitBy(member.name ?: ""))
                    } else {
                        inDetailMembers.add(detailMember)
                        outDetailMembers.add(detailMember.copy().copyInitBy(member.name ?: ""))
                    }
                }

                val inLayout = detail_in_member_list.layoutParams as ConstraintLayout.LayoutParams
                inLayout.height =
                    Utils.instance().DPToPX((20f + 1f + 16f + 16f + 2f) * inDetailMembers.size)
                        .toInt()
                inLayout.width =
                    Utils.instance().getScreenWidth() - Utils.instance().DPToPX(24 * 2f).toInt()
                detail_in_member_list.layoutParams = inLayout

                val outLayout = detail_out_member_list.layoutParams as ConstraintLayout.LayoutParams
                outLayout.height =
                    Utils.instance().DPToPX((20f + 1f + 16f + 16f + 2f) * outDetailMembers.size)
                        .toInt()
                outLayout.width =
                    Utils.instance().getScreenWidth() - Utils.instance().DPToPX(24 * 2f).toInt()
                detail_out_member_list.layoutParams = outLayout

                mOutMemberAdapter.notifyDataSetChanged()
                mInMemberAdapter.notifyDataSetChanged()
            }else{
                showMessageAlertDialog("","${getString(R.string.DATABASE_ERROR)}($resultCode)"){_,_ ->
                    onKeyCodeBackListener()
                }
            }
        }

    }

    override fun onKeyCodeBackListener() {
        super.onKeyCodeBackListener()
        finish()
        baseStartActivity(null, ActivityMoveEnum.BACK_FROM_LEFT)
    }

    override fun onDestroy() {
        super.onDestroy()
        outDetailMembers.clear()
        inDetailMembers.clear()
        EventBus.getDefault().unregister(this)
    }
}
