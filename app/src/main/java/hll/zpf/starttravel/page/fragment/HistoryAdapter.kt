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

class HistoryAdapter: RecyclerView.Adapter<HistoryAdapter.HistoryViewHodler>() {

    var travelData:List<Travel> = ArrayList()
    var mContext:Context? = null
    var callback:((Int) -> Unit)? = null

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): HistoryViewHodler {
        val view = LayoutInflater.from(mContext).inflate(R.layout.history_item,p0,false)
        return HistoryViewHodler(view)
    }

    override fun getItemCount(): Int {
        return travelData.size
    }

    override fun onBindViewHolder(holder: HistoryViewHodler, position: Int) {
        val history = travelData[position]
        holder.travelNameTv.text = history.name
        holder.travelMemoTv.text = history.memo
        val numberStr = mContext!!.getString(R.string.history_010)
        holder.travelPersonNumberTv.text = "${history.memberList.size} $numberStr"
        holder.travelDateTv.text = Utils.instance().getDateStringByFormatAndDateString(history.startDate,"yyyy年MM月dd日 hh:mm")
        holder.travelMoneyTv.text = Utils.instance().transMoneyToString(history.money)

        holder.travelDetailBt.setOnClickListener {
            callback?.let {
                it(position)
            }
        }
    }

    inner class HistoryViewHodler(itemView:View): RecyclerView.ViewHolder(itemView){
         var travelNameTv:TextView = itemView.findViewById(R.id.travel_name_tv)
         var travelDateTv:TextView = itemView.findViewById(R.id.travel_date_tv)
         var travelMemoTv:TextView = itemView.findViewById(R.id.travel_memo_tv)
         var travelPersonNumberTv:TextView = itemView.findViewById(R.id.travel_person_number)
         var travelMoneyTv:TextView = itemView.findViewById(R.id.travel_money_tv)
         var travelImage:CRImageView = itemView.findViewById(R.id.travel_image)
         var travelDetailBt:Button = itemView.findViewById(R.id.travel_detail_bt)

    }
}