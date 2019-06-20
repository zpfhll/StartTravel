package hll.zpf.starttravel.page.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import hll.zpf.starttravel.R
import hll.zpf.starttravel.common.HLogger
import hll.zpf.starttravel.common.bean.TravelBean
import hll.zpf.starttravel.common.model.TravelModel

class TravelFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_travel, container, false)
        val pagerView: ViewPager = rootView.findViewById(R.id.travel_view_pager)
        val adapter = TravelItemAdapter(activity!!.supportFragmentManager)
        adapter.datas = initData()
        adapter.callback = {position,travelModel,action ->
            HLogger.instance().d("travelTap","$position--${travelModel.getTravelData().value!!.travelName}---$action")

            //TODO カード更新の処理
//            val travel = travelModel.getTravelData().value!!
//            travel.travelName = "旅途1234"
//            travelModel.getTravelData().value =  travel
        }
        pagerView.adapter = adapter
        pagerView.pageMargin = 60
        //预缓存页面数
        pagerView.offscreenPageLimit = 2
        return rootView
    }


    private fun initData() : ArrayList<TravelItemFragment>{
        val data = ArrayList<TravelItemFragment>()
//        for (it in 1..6){
//            val travel = TravelBean()
//            travel.travelDate = "2019年02月0${it}日"
//            travel.travelName = "旅途$it"
//            travel.travelMemo = "一次难忘的旅行"
//            travel.travelMoney = 3000.8f
//            travel.travelPersonNumber = 4
//            val fragment = TravelItemFragment()
//            fragment.initDate = travel
//            data.add(fragment)
//        }

        val fragment = TravelItemFragment()
        fragment.type = 1
        data.add(fragment)

        return  data
    }


}
