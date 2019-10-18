package hll.zpf.starttravel.page.fragment

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import hll.zpf.starttravel.R
import hll.zpf.starttravel.common.Utils
import hll.zpf.starttravel.common.components.CRImageView
import hll.zpf.starttravel.common.database.entity.Travel

class HistoryAdapter(context: Context, travelData: List<Travel>, callback: ((Int) -> Unit)): RecyclerView.Adapter<HistoryAdapter.HistoryViewHandler>() {

    var mTravelData:List<Travel> = travelData
    var mContext:Context = context
    var mCallback:((Int) -> Unit) = callback



    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): HistoryViewHandler {
        val view = LayoutInflater.from(mContext).inflate(R.layout.history_item,p0,false)
        return HistoryViewHandler(view)
    }

    override fun getItemCount(): Int {
        return mTravelData.size
    }

    override fun onBindViewHolder(holder: HistoryViewHandler, position: Int) {
        val history = mTravelData[position]
        holder.travelNameTv.text = history.name
        holder.travelMemoTv.text = history.memo
        val numberStr = mContext!!.getString(R.string.history_010)
        holder.travelPersonNumberTv.text = "${history.memberCount} $numberStr"
        holder.travelDateTv.text = Utils.instance().getDateStringByFormatAndDateString(history.startDate!!,"yyyy年MM月dd日 hh:mm")
        holder.travelMoneyTv.text = Utils.instance().transMoneyToString(history.money ?: 0f)

        holder.travelDetailBt.setOnClickListener {
            mCallback?.let {
                it(position)
            }
        }
    }

    inner class HistoryViewHandler(itemView:View): RecyclerView.ViewHolder(itemView){
         var travelNameTv:TextView = itemView.findViewById(R.id.travel_name_tv)
         var travelDateTv:TextView = itemView.findViewById(R.id.travel_date_tv)
         var travelMemoTv:TextView = itemView.findViewById(R.id.travel_memo_tv)
         var travelPersonNumberTv:TextView = itemView.findViewById(R.id.travel_person_number)
         var travelMoneyTv:TextView = itemView.findViewById(R.id.travel_money_tv)
         var travelImage:CRImageView = itemView.findViewById(R.id.travel_image)
         var travelDetailBt:Button = itemView.findViewById(R.id.travel_detail_bt)

    }
}