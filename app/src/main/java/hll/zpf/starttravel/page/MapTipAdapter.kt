package hll.zpf.starttravel.page

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.services.core.PoiItem
import hll.zpf.starttravel.R


class MapTipAdapter internal constructor(data: ArrayList<PoiItem>, context: Context, callback:((Int, PoiItem)->Unit)? = null) : BaseAdapter() {

    private var mData = ArrayList<PoiItem>()

    private var mContext:Context

    private var mCallback:((Int,PoiItem)->Unit)?

    init {
        mData = data
        mContext = context
        mCallback = callback
    }

    override fun getCount(): Int {
        return mData.size
    }

    override fun getItem(position: Int): PoiItem {
        return mData[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val holder: ViewHolder
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext)
                .inflate(R.layout.map_tips_item, parent, false)
            holder = ViewHolder(convertView)
            convertView!!.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }
        holder.tipLocation.text = "${mData[position].adName} ${mData[position].snippet}"
        holder.tipName.text = mData[position].title

        convertView.setOnClickListener{
            mCallback?.let {
                it(position,mData[position])
            }
        }


        return convertView
    }

    internal class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tipName: TextView = itemView.findViewById(R.id.tip_name)
        var tipLocation: TextView = itemView.findViewById(R.id.tip_location)

    }
}
