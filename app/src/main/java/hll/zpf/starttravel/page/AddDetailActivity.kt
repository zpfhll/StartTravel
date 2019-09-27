package hll.zpf.starttravel.page

import android.animation.ObjectAnimator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import hll.zpf.starttravel.BuildConfig
import hll.zpf.starttravel.R
import hll.zpf.starttravel.base.BaseActivity
import hll.zpf.starttravel.common.EventBusMessage
import hll.zpf.starttravel.common.HLogger
import hll.zpf.starttravel.common.Utils
import hll.zpf.starttravel.common.database.DataManager
import hll.zpf.starttravel.common.database.entity.DetailWithMember
import hll.zpf.starttravel.common.enums.ActivityMoveEnum
import kotlinx.android.synthetic.main.activity_add_detail.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class AddDetailActivity : BaseActivity() {
    private lateinit var dataManager:DataManager
    private lateinit var inDetailMembers:MutableList<DetailWithMember>
    private lateinit var outDetailMembers:MutableList<DetailWithMember>
    private lateinit var mInMemberAdapter:TravelDetailMemberAdapter

    private var inputMoney:Float = 0f

    var notAssignMoney:Float = 0f

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

        detail_money.addTextChangedListener(object : TextWatcher {
            var beforeText = ""
            override fun afterTextChanged(s: Editable?) {
                var text = s.toString().replace(",","")
                var isTwo = false
                if(text.isEmpty()){
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
                if((!beforeText.equals(money) && beforeText.length <= money.length) || isTwo) {
                    detail_money.setText(money)
                    inputMoney = text.toFloat()
                    detail_money.setSelection(detail_money.text.length)
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
    }

    @Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN, sticky = true)
    fun initData(message: EventBusMessage){
        if(message.message.equals(ADD_DETAIL)){
            val detail = message.detail
            val travel = message.travel
            var isIn = false
            if(detail == null){
                isIn = false
                in_checkbox.check(false)
                out_checkbox.check(true)
            }else {
                when (detail.type) {
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
            }

            initPlatform(isIn,travel?.getTravelData()?.value?.id)
            val title = travel?.getTravelData()?.value?.name ?: ""
            setTitle(
                title,
                true,
                getString(R.string.add_detail_011),
                R.drawable.back_button_background){
                when(it.id){
                    R.id.left_button -> {//返回
                        finish()
                        baseStartActivity(null, ActivityMoveEnum.BACK_FROM_LEFT)
                    }
                    R.id.right_button -> {//添加

                    }
                }
            }
        }
    }

    private fun changeMemberPlatform(isIn:Boolean){
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
                            inDetailMembers[position].isSelected = !isChecked
                        }
                    }
                }
            }else{
                outDetailMembers[position].isSelected = isChecked
            }
    }

    private fun memberSetMoney(position:Int,money:Float){
        outDetailMembers[position].money = money
    }


    private fun initPlatform(isIn:Boolean,travelId:String?) {
        dataManager = DataManager()
        inDetailMembers = mutableListOf()
        outDetailMembers = mutableListOf()
        val self = DetailWithMember.createDetailWithMember()
        self.memberId = ""
        self.memberName = "自己"
        self.travelId = travelId ?: ""
        inDetailMembers.add(0,self)
        outDetailMembers.add(0,self)
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
        GlobalScope.launch {
            val members = dataManager.getPartnerByTravelId(travelId)
            for (member in members){
                val detailMember = DetailWithMember.createDetailWithMember()
                detailMember.memberId = member.id
                detailMember.memberName = member.name ?: ""
                detailMember.travelId = travelId ?: ""
                inDetailMembers.add(detailMember)
                outDetailMembers.add(detailMember)
            }

            val inLayout = detail_in_member_list.layoutParams as ConstraintLayout.LayoutParams
            inLayout.height = Utils.instance().DPToPX((20f + 1f + 16f + 16f + 2f) * inDetailMembers.size).toInt()
            inLayout.width = Utils.instance().getScreenWidth() - Utils.instance().DPToPX(24 * 2f).toInt()
            detail_in_member_list.layoutParams = inLayout

            val outLayout = detail_out_member_list.layoutParams as ConstraintLayout.LayoutParams
            outLayout.height = Utils.instance().DPToPX((20f + 1f + 16f + 16f + 2f ) * outDetailMembers.size).toInt()
            outLayout.width = Utils.instance().getScreenWidth() - Utils.instance().DPToPX(24 * 2f).toInt()
            detail_out_member_list.layoutParams = outLayout

            mOutMemberAdapter.notifyDataSetChanged()
            mInMemberAdapter.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        outDetailMembers.clear()
        inDetailMembers.clear()
        EventBus.getDefault().unregister(this)
    }
}
