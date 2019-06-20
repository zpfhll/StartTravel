package hll.zpf.starttravel.page.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import hll.zpf.starttravel.common.model.TravelModel

class TravelItemAdapter(manager:FragmentManager): FragmentPagerAdapter(manager) {

    var datas:List<TravelItemFragment> = ArrayList()

    /**
     * 回调函数
     * Int：旅途的索引
     * TravelModel：旅途的情报
     * Int：动作 ⇨　0：启程 1：标记 2：编辑 3：详细
     */
    var callback:((Int,TravelModel,Int) -> Unit)? = null

    override fun getItem(position : Int): Fragment {
        val fragment = datas[position]
        fragment.callback = { travelState,action ->
            callback?.let {
                it(position,travelState,action)
            }
        }
        return fragment
    }

    override fun getCount(): Int {
        return datas.size
    }

}