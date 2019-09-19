package hll.zpf.starttravel.page.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import hll.zpf.starttravel.common.model.TravelModel
import hll.zpf.starttravel.common.database.entity.Travel

class TravelItemAdapter(manager:FragmentManager):FragmentStatePagerAdapter(manager,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    var mDatas:MutableList<TravelItemFragment> = mutableListOf()

    init {
        val fragment = TravelItemFragment()
        val travel = Travel.createTravel()
        travel.type = 1
        fragment.initDate = travel
        mDatas.add(fragment)
    }

    /**
     * 回调函数
     * Int：旅途的索引
     * TravelModel：旅途的情报
     * Int：动作 ⇨　0：启程 1：标记 2：编辑 3：详细 4:结束 5:图片
     */
    var callback:((Int,TravelModel,Int) -> Unit)? = null

    override fun getItem(position : Int): Fragment {
        val fragment = mDatas[position]
        fragment.callback = { travelState,action ->
            callback?.let {
                it(position,travelState,action)
            }
        }
        return fragment
    }

    override fun getCount(): Int {
        return mDatas.size
    }


    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    fun refresh(travelData:MutableList<Travel>){
        if(travelData.size > 0){
            if (mDatas.size == travelData.size){
                for (i in 0 until travelData.size){
                   mDatas[i].initDate = travelData[i]
                }
            }else if (mDatas.size > travelData.size){
                for (i in 0 until travelData.size){
                    mDatas[i].initDate = travelData[i]
                }
                for (i in travelData.size until mDatas.size){
                    mDatas.removeAt(i)
                }
            }else{
                for (i in 0 until mDatas.size){
                    mDatas[i].initDate = travelData[i]
                }
                for (i in mDatas.size until travelData.size){
                    val fragment = TravelItemFragment()
                    fragment.initDate = travelData[i]
                    mDatas.add(fragment)
                }
            }
        }else{
            if(mDatas[0].travelModel?.getTravelData()?.value == null
                || mDatas[0].travelModel?.getTravelData()?.value?.type != 1 ){
                val travel = Travel.createTravel()
                travel.type = 1
                mDatas[0].initDate = travel
            }
        }
        notifyDataSetChanged()
    }

}