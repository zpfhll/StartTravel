package hll.zpf.starttravel.page

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.recyclerview.widget.LinearLayoutManager
import hll.zpf.starttravel.BuildConfig
import hll.zpf.starttravel.R
import hll.zpf.starttravel.base.BaseActivity
import hll.zpf.starttravel.common.EventBusMessage
import hll.zpf.starttravel.common.HLogger
import hll.zpf.starttravel.common.Utils
import hll.zpf.starttravel.common.database.DataManager
import hll.zpf.starttravel.common.database.entity.Detail
import hll.zpf.starttravel.common.enums.ActivityMoveEnum
import hll.zpf.starttravel.common.model.TravelModel
import kotlinx.android.synthetic.main.activity_travel_detail.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class TravelDetailActivity : BaseActivity() {

    private var detailAdapter :TravelDetailAdapter? = null
    private lateinit var details :List<Detail>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_travel_detail)
        EventBus.getDefault().register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun initData(message: EventBusMessage){
        if(message.message.equals(TRAVEL_DETAIL)){
            setTitle(
                message.travel!!.getTravelData().value!!.name!!,
                true,
                getString(R.string.travel_detail_001),
                R.drawable.back_button_background){
                when(it.id){
                    R.id.left_button -> {//返回
                        onKeyCodeBackListener()
                    }
                    R.id.right_button -> {//添加
                        message.message = ADD_DETAIL
                        EventBus.getDefault().postSticky(message)
                        val moneyIntent = Intent(this,AddDetailActivity::class.java)
                        baseStartActivity(moneyIntent, ActivityMoveEnum.START_FROM_RIGHT)
                    }
                }
            }
            refreshData(message.travel)
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshDetail(message: EventBusMessage){
        if(message.message.equals(REFRESH_TRAVEL_DETAIL)) {
            refreshData(message.travel)
        }
    }

    private fun refreshData(travel:TravelModel?){
        if(dataManager == null){
            dataManager = DataManager()
        }

        travel?.let {
            consume_text.text  = Utils.instance().transMoneyToString(it.getTravelData().value?.outMoney ?: 0f)
            input_text.text = Utils.instance().transMoneyToString(it.getTravelData().value?.inMoney ?: 0f)
            wallet_text.text = Utils.instance().transMoneyToString((it.getTravelData().value?.inMoney ?: 0f) - (it.getTravelData().value?.outMoney ?: 0f))
        }

        dataManager?.getDetailByTravelId(travel?.getTravelData()?.value?.id ?: ""){resultCode, data ->
            if(resultCode == BuildConfig.NORMAL_CODE) {
                details = data
                if(detailAdapter == null){
                    detailAdapter = TravelDetailAdapter(this, details){
                        HLogger.instance().e("TravelDetailAdapter--->","item $it")
                        val message = EventBusMessage.instance(SHOW_DETAIL)
                        message.detail = details[it]
                        message.travel = travel
                        EventBus.getDefault().postSticky(message)
                        val moneyIntent = Intent(this,AddDetailActivity::class.java)
                        baseStartActivity(moneyIntent, ActivityMoveEnum.START_FROM_RIGHT)
                    }
                    detail_list.adapter = detailAdapter
                    detail_list.layoutManager = LinearLayoutManager(context)
                }else{
                    detailAdapter?.refreshData(data)
                }
            }else{
                showMessageAlertDialog("","${getString(R.string.DATABASE_ERROR)}($resultCode)"){_,_ ->
                    onKeyCodeBackListener()
                }
            }
        }
    }

    override fun onKeyCodeBackListener() {
        super.onKeyCodeBackListener()
        finish()
        baseStartActivity(null, ActivityMoveEnum.BACK_FROM_LEFT)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}
