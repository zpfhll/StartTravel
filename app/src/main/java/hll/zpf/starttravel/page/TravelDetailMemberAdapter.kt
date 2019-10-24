package hll.zpf.starttravel.page

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import hll.zpf.starttravel.BuildConfig
import hll.zpf.starttravel.R
import hll.zpf.starttravel.common.EventBusMessage
import hll.zpf.starttravel.common.HLogger
import hll.zpf.starttravel.common.Utils
import hll.zpf.starttravel.common.components.CustomCheckboxView
import hll.zpf.starttravel.common.database.entity.DetailWithMember
import hll.zpf.starttravel.common.database.entity.Member
import kotlinx.android.synthetic.main.activity_travel_detail.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class TravelDetailMemberAdapter(context: AddDetailActivity, memberData: List<DetailWithMember>,isIn:Boolean,checkCallback: ((Int,Boolean,Boolean) -> Unit)? = null) :
    RecyclerView.Adapter<TravelDetailMemberAdapter.MemberItemViewHandler>() {

    var mMemberData:List<DetailWithMember> = memberData
    var mContext:AddDetailActivity = context
    var mCheckCallback:((Int,Boolean,Boolean) -> Unit)? = checkCallback
    var mEditCallback:((Int,Float) -> Unit)? = null
    var mIsIn:Boolean = isIn

    var isShow:Boolean = false

    private val EVENTBUS_MESSAGE_REFRESH = "REFRESH_ITEM_CHECKBOX"
    private val EVENTBUS_MESSAGE_SWITCH_SPLIT = "SWITCH_SPLIT"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberItemViewHandler {
        var rootId = if (mIsIn) R.layout.detail_member_in_item else R.layout.detail_member_out_item
        val view = LayoutInflater.from(mContext).inflate(rootId,parent,false)
        return MemberItemViewHandler(view,mIsIn)
    }

    override fun getItemCount(): Int {
        return mMemberData.size
    }

    override fun onBindViewHolder(holder: MemberItemViewHandler, position: Int) {
        val member = mMemberData[position]
        holder.index = position
        if (mIsIn){
            holder.inCheckBox?.setText(member.memberName)
            holder.inCheckBox?.check(member.isSelected)
        }else{
            holder.outCheckBox?.setText(member.memberName)
            holder.memberMoney?.setText("${member.money}")
            holder.outCheckBox?.check(member.isSelected)
            holder.memberMoney?.isEnabled = !isShow && member.isSelected
        }
    }

    inner class MemberItemViewHandler(itemView: View,isIn: Boolean): RecyclerView.ViewHolder(itemView){

        var inCheckBox: CustomCheckboxView? = null
        var outCheckBox:CustomCheckboxView? = null
        var memberMoney:EditText? = null
        var index:Int = 0
        var isSplit = true
        init {
            EventBus.getDefault().register(this)
            if (!isIn){
                outCheckBox = itemView.findViewById(R.id.out_checkbox)
                outCheckBox?.isEnabled = !isShow
                outCheckBox?.checkCallback = {
                    memberMoney?.isEnabled = it && !isSplit
                    if (!it){
                        memberMoney?.setText("0")
                    }
                    mCheckCallback?.let{callback ->
                        callback(index,isIn,it)
                    }
                }
                memberMoney = itemView.findViewById(R.id.detail_member_money)
                memberMoney?.isEnabled = !isShow
                memberMoney?.addTextChangedListener(object : TextWatcher{
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

                        val beforeNumberStr =  beforeText.replace(",","")
                        if(text.toFloat() > BuildConfig.MAX_MONEY || (mContext.assignMoney - (if (beforeNumberStr == "") 0f else beforeNumberStr.toFloat()) + text.toFloat())  > mContext.walletMoney && !isShow){
                            text = beforeNumberStr
                            isTwo = true
                        }

                        if(text.isEmpty()){
                            text = "0"
                        }
                        val money = Utils.instance().transMoneyToString(text.toFloat())
                        HLogger.instance().e("afterTextChanged",money)
                        HLogger.instance().e("afterTextChanged beforeText",beforeText)
                        if((!beforeText.equals(money) && beforeText.length <= money.length) || isTwo || beforeText.length > money.length) {
                            memberMoney?.setText(money)
                            memberMoney?.setSelection(memberMoney?.text?.length ?: 0)
                        }
                        mEditCallback?.let {
                            it(index,text.toFloat())
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
            }else{
                inCheckBox = itemView.findViewById(R.id.in_checkbox)
                inCheckBox?.isEnabled = !isShow
                inCheckBox?.checkCallback = {
                    mCheckCallback?.let{callback ->
                        callback(index,isIn,it)
                    }
                    if(it){
                        val message = EventBusMessage()
                        message.message = EVENTBUS_MESSAGE_REFRESH
                        message.memberCheckIndex = index
                        EventBus.getDefault().post(message)
                    }

                }

            }
        }

        @Subscribe
        fun changeCheck(message: EventBusMessage){
            when(message.message){
                EVENTBUS_MESSAGE_REFRESH -> {
                    message.memberCheckIndex?.let {
                        if(it != index && inCheckBox?.isChecked == true){
                            inCheckBox?.check(false)
                        }
                    }
                }
                EVENTBUS_MESSAGE_SWITCH_SPLIT -> {
                    isSplit = message.memberIsSplit
                    if(isSplit){
                        memberMoney?.isEnabled = false
                        if(outCheckBox?.isChecked == true) memberMoney?.setText(message.memberSplitMoney)
                    }else{
                        memberMoney?.isEnabled  = outCheckBox?.isChecked ?: false
                    }
                }
            }
        }
    }
}