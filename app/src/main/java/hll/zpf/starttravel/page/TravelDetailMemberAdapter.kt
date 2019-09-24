package hll.zpf.starttravel.page

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import hll.zpf.starttravel.R
import hll.zpf.starttravel.common.components.CustomCheckboxView
import hll.zpf.starttravel.common.database.entity.Member

class TravelDetailMemberAdapter(context: Context, memberData: List<Member>,isIn:Boolean,callback: ((Int) -> Unit)) :
    RecyclerView.Adapter<TravelDetailMemberAdapter.MemberItemViewHandler>() {

    var mMemberData:List<Member> = memberData
    var mContext:Context = context
    var mCallback:((Int) -> Unit) = callback
    var mIsIn:Boolean = isIn


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
        if (mIsIn){
            holder.inCheckBox.setText(member.name!!)
        }else{
            holder.outCheckBox.setText(member.name!!)
        }
    }

    inner class MemberItemViewHandler(itemView: View,isIn: Boolean): RecyclerView.ViewHolder(itemView){

        lateinit  var inCheckBox: CustomCheckboxView
        lateinit var outCheckBox:CustomCheckboxView
        lateinit var memberMoney:EditText

        init {
            if (!isIn){
                outCheckBox = itemView.findViewById(R.id.out_checkbox)
                memberMoney = itemView.findViewById(R.id.detail_member_money)
            }else{
                inCheckBox = itemView.findViewById(R.id.in_checkbox)
            }
        }

    }
}