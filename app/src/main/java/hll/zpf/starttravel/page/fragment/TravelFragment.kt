package hll.zpf.starttravel.page.fragment


import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import hll.zpf.starttravel.R
import hll.zpf.starttravel.base.BaseActivity
import hll.zpf.starttravel.common.EventBusMessage
import hll.zpf.starttravel.common.HLogger
import hll.zpf.starttravel.common.database.DataManager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class TravelFragment : Fragment() {

    lateinit var adapter:TravelItemAdapter
    lateinit var refreshHandler:Handler

    private val REFRESH_DATA = 1

    private var fragmentData:ArrayList<TravelItemFragment>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_travel, container, false)
        val pagerView: ViewPager = rootView.findViewById(R.id.travel_view_pager)
        EventBus.getDefault().register(this)
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

        refreshHandler = Handler{
            when(it.what){
                REFRESH_DATA -> {
                    adapter.datas = fragmentData!!
                    adapter.notifyDataSetChanged()
                }
            }
            false
        }
        val message = EventBusMessage()
        message.message = (activity as BaseActivity).REFRESH_TRAVEL_DATA
        EventBus.getDefault().post(message)
        return rootView
    }

    override fun onDestroy() {
        super.onDestroy()
        refreshHandler.removeCallbacksAndMessages(null)
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun refreshData(message: EventBusMessage){
        if(message.message.equals((activity as BaseActivity).REFRESH_TRAVEL_DATA)){
            initData()
        }
    }



    private fun initData(){
        val dataManager = DataManager()
        val travelList = dataManager.getNotEndTravel()
        fragmentData = ArrayList<TravelItemFragment>()
        travelList?.let {
            if(travelList.isNotEmpty()) {
                for (travel in travelList) {
                    val fragment = TravelItemFragment()
                    fragment.initDate = travel
                    fragment.type = 0
                    fragmentData!!.add(fragment)
                }
            }else{
                val fragment = TravelItemFragment()
                fragment.type = 1
                fragmentData!!.add(fragment)
            }
            val message = Message()
            message.what = REFRESH_DATA
            refreshHandler.sendMessage(message)
        }
    }


}
