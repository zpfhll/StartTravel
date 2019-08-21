package hll.zpf.starttravel.page.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import hll.zpf.starttravel.R
import hll.zpf.starttravel.common.HLogger
import hll.zpf.starttravel.common.Utils
import hll.zpf.starttravel.common.database.DataManager
import hll.zpf.starttravel.common.database.entity.Member
import hll.zpf.starttravel.common.database.entity.Travel
import kotlinx.android.synthetic.main.fragment_travel.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TravelFragment : Fragment() {

    lateinit var adapter:TravelItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_travel, container, false)
        val pagerView: ViewPager = rootView.findViewById(R.id.travel_view_pager)
        adapter = TravelItemAdapter(activity!!.supportFragmentManager)
        adapter.callback = {position,travelModel,action ->
            HLogger.instance().d("travelTap","$position--${travelModel.getTravelData().value!!.name}---$action")

            //TODO カード更新の処理
//            val travel = travelModel.getTravelData().value!!
//            travel.travelName = "旅途1234"
//            travelModel.getTravelData().value =  travel
        }
        pagerView.adapter = adapter
        pagerView.pageMargin = 60
        //预缓存页面数
        pagerView.offscreenPageLimit = 2

        initData()
        return rootView
    }


    private fun initData(){
        GlobalScope.launch {
            val dataManager = DataManager()
            val travelList = dataManager.getNotEndTravel()
            val data = ArrayList<TravelItemFragment>()
            if(travelList.isNotEmpty()) {
                for (travel in travelList) {
                    val fragment = TravelItemFragment()
                    fragment.initDate = travel
                    fragment.type = 0
                    data.add(fragment)
                }
            }else{
                val fragment = TravelItemFragment()
                fragment.type = 1
                data.add(fragment)
            }
            adapter.datas = data
            adapter.notifyDataSetChanged()
        }
    }


}
