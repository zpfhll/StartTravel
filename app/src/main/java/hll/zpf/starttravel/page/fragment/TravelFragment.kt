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
import hll.zpf.starttravel.common.Utils
import hll.zpf.starttravel.common.database.DataManager
import hll.zpf.starttravel.common.database.entity.Travel
import kotlinx.android.synthetic.main.fragment_travel.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class TravelFragment : Fragment() {

    lateinit var adapter:TravelItemAdapter
    lateinit var refreshHandler:Handler

    private val REFRESH_DATA = 1

    private var fragmentData:MutableList<TravelItemFragment> = mutableListOf()

    private var travelData:MutableList<Travel>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_travel, container, false)
        val pagerView:ViewPager = rootView.findViewById(R.id.travel_view_pager)
        EventBus.getDefault().register(this)
        adapter = TravelItemAdapter(activity!!.supportFragmentManager)
        adapter.callback = {position,travelModel,action ->
            HLogger.instance().d("travelTap","$position--${travelModel.getTravelData().value!!.name}---$action")
            when(action){
                0 ->{//0：启程
                    val travel = travelModel.getTravelData().value
                    val startDate = Utils.instance().getDateStringByFormat()
                    travel?.startDate = startDate
                    travel?.state = 1
                    travelModel.getTravelData().value =  travel
                    GlobalScope.launch {
                        travel?.let {
                            DataManager().insertOrReplaceTravel(it)
                        }
                    }
                }
                1 ->{//1：标记

                }
                2 ->{//2：编辑

                }
                3 ->{//3：详细

                }
                4 ->{//4:结束

                }
            }

        }
        pagerView.adapter = adapter
        pagerView.pageMargin = 60
        //预缓存页面数
        pagerView.offscreenPageLimit = 2
        refreshHandler = Handler{
            when(it.what){
                REFRESH_DATA -> {
                    travelData?.let {
                        adapter.refresh(it)
                        travel_view_pager.currentItem = 0
                        pager_index.itemOnClick = {currentItem ->
                            travel_view_pager.currentItem = currentItem
                        }
                        pager_index.refresh(it.size,0)
                        it.clear()
                    }
                }
            }
            false
        }
        pagerView.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                pager_index.changeState(position)
            }
        })
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
        travelData?.clear()
        travelData = dataManager.getNotEndTravel()
        val message = Message()
        message.what = REFRESH_DATA
        refreshHandler.sendMessage(message)
    }


}
