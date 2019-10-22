package hll.zpf.starttravel.page

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hll.zpf.starttravel.R
import hll.zpf.starttravel.common.Utils
import hll.zpf.starttravel.common.database.entity.Detail

class TravelDetailAdapter(context: Context, detailData: List<Detail>, callback: ((Int) -> Unit)) :
    RecyclerView.Adapter<TravelDetailAdapter.DetailItemViewHandler>() {

    var mDetailData:ArrayList<Detail> = arrayListOf(*detailData.toTypedArray())
    var mContext:Context = context
    var mCallback:((Int) -> Unit) = callback


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailItemViewHandler {
        val view = LayoutInflater.from(mContext).inflate(R.layout.detail_item,parent,false)
        return DetailItemViewHandler(view)
    }

    override fun getItemCount(): Int {
        return mDetailData.size
    }

    fun refreshData(data:List<Detail>){
        mDetailData.clear()
        mDetailData.addAll(data)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: DetailItemViewHandler, position: Int) {
        val detail = mDetailData[position]
        when(detail.type){
            0 -> {
                holder.detailType.setBackgroundResource(R.drawable.circle_green)
                holder.detailMoney.setTextColor(mContext.getColor(R.color.baseColor))
            }
            1 -> {
                holder.detailType.setBackgroundResource(R.drawable.circle_red)
                holder.detailMoney.setTextColor(mContext.getColor(R.color.red))
            }
        }
        holder.detailName.text = detail.memo
        holder.detailDate.text = Utils.instance().getDateStringByFormatAndDateString(detail.date,"yyyy年MM月dd日 hh:mm")
        holder.detailMoney.text = String.format(mContext.getString(R.string.money_label),Utils.instance().transMoneyToString(detail.money ?: 0f))
        holder.rootView.setOnClickListener{
            mCallback(position)
        }
    }


    inner class DetailItemViewHandler(itemView: View): RecyclerView.ViewHolder(itemView){
        var detailType: TextView = itemView.findViewById(R.id.detail_type)
        var detailName:TextView = itemView.findViewById(R.id.detail_name)
        var detailMoney:TextView = itemView.findViewById(R.id.detail_money)
        var detailDate:TextView = itemView.findViewById(R.id.detail_date)
        var rootView:View = itemView.findViewById(R.id.root_view)

    }
}