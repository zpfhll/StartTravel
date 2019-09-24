package hll.zpf.starttravel.page

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import hll.zpf.starttravel.R
import hll.zpf.starttravel.base.BaseActivity
import hll.zpf.starttravel.common.EventBusMessage
import hll.zpf.starttravel.common.HLogger
import hll.zpf.starttravel.common.Utils
import hll.zpf.starttravel.common.database.DataManager
import hll.zpf.starttravel.common.database.entity.Member
import hll.zpf.starttravel.common.enums.ActivityMoveEnum
import kotlinx.android.synthetic.main.activity_add_detail.*
import kotlinx.android.synthetic.main.activity_travel_detail.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class AddDetailActivity : BaseActivity() {
    private lateinit var dataManager:DataManager
    private lateinit var members:MutableList<Member>

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
    }

    @Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN, sticky = true)
    fun initData(message: EventBusMessage){
        if(message.message.equals(ADD_DETAIL)){
            val detail = message.detail
            val travel = message.travel
            var isIn = false
            travel?.let {
                travel_name.text = it.getTravelData().value!!.name
            }
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

    private fun memberCheck(isIn:Boolean,position:Int){

    }


    private fun initPlatform(isIn:Boolean,travelId:String?) {
        dataManager = DataManager()
        members = mutableListOf()
        val self = Member("","自己",0f,null,null)
        members.add(0,self)
        var mInMemberAdapter = TravelDetailMemberAdapter(this,members,true){
            memberCheck(true,it)
        }
        var mOutMemberAdapter = TravelDetailMemberAdapter(this,members,false){
            memberCheck(false,it)
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
            val datas = dataManager.getPartnerByTravelId(travelId)
            members.addAll(datas)

            val inLayout = detail_in_member_list.layoutParams as ConstraintLayout.LayoutParams
            inLayout.height = Utils.instance().DPToPX((20f + 1f + 16f + 16f + 2f) * members.size).toInt()
            inLayout.width = Utils.instance().getScreenWidth() - Utils.instance().DPToPX(24 * 2f).toInt()
            detail_in_member_list.layoutParams = inLayout

            val outLayout = detail_out_member_list.layoutParams as ConstraintLayout.LayoutParams
            outLayout.height = Utils.instance().DPToPX((20f + 1f + 16f + 16f + 2f ) * members.size).toInt()
            outLayout.width = Utils.instance().getScreenWidth() - Utils.instance().DPToPX(24 * 2f).toInt()
            detail_out_member_list.layoutParams = outLayout

            mOutMemberAdapter.notifyDataSetChanged()
            mInMemberAdapter.notifyDataSetChanged()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}
