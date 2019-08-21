package hll.zpf.starttravel.page.fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.services.help.Tip
import hll.zpf.starttravel.R


class MapTipAdapter internal constructor(datas: ArrayList<Tip>, context: Context,callback:((Int,Tip)->Unit)? = null) : BaseAdapter() {

    private var mDatas = ArrayList<Tip>()

    private var mContext:Context

    private var mCallback:((Int,Tip)->Unit)?

    init {
        mDatas = datas
        mContext = context
        mCallback = callback
    }

    override fun getCount(): Int {
        return mDatas.size
    }

    override fun getItem(position: Int): Tip {
        return mDatas[position]
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
        holder.tipLocation.text = "${mDatas[position].district} ${mDatas[position].address}"
        holder.tipName.text = mDatas[position].name

        convertView.setOnClickListener{
            mCallback?.let {
                it(position,mDatas[position])
            }
        }


        return convertView
    }

    internal class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tipName: TextView = itemView.findViewById(R.id.tip_name)
        var tipLocation: TextView = itemView.findViewById(R.id.tip_location)

    }
}
