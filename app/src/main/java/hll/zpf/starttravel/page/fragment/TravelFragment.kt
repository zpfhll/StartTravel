package hll.zpf.starttravel.page.fragment


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.MediaStore
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
import kotlinx.android.synthetic.main.fragment_travel.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import android.graphics.BitmapFactory
import android.net.Uri
import hll.zpf.starttravel.common.model.TravelModel
import java.io.File
import androidx.core.content.ContextCompat
import hll.zpf.starttravel.BuildConfig
import hll.zpf.starttravel.common.database.entity.Travel
import hll.zpf.starttravel.common.enums.ActivityMoveEnum
import hll.zpf.starttravel.common.enums.TravelTypeEnum
import hll.zpf.starttravel.page.TimeLineActivity
import hll.zpf.starttravel.page.TimeLineAdapter
import hll.zpf.starttravel.page.TravelDetailActivity


class TravelFragment : Fragment() {

    lateinit var adapter:TravelItemAdapter
    lateinit var refreshHandler:Handler

    private val REFRESH_DATA = 1

    private var travelData:MutableList<Travel>? = null

    private val IMAGE_CODE = 2

    private val CROP_REQUEST_CODE = 3

    private var currentTravelModel:TravelModel? = null

    var travelNumber:Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_travel, container, false)
        val pagerView:ViewPager = rootView.findViewById(R.id.travel_view_pager)
        EventBus.getDefault().register(this)
        adapter = TravelItemAdapter(activity!!.supportFragmentManager)
        adapter.callback = {position,travelModel,action ->
            currentTravelModel = travelModel
            HLogger.instance().d("travelTap","$position--${travelModel.getTravelData().value!!.name}---$action")
            when(action){
                0 ->{//0：启程
                    val travel = travelModel.getTravelData().value
                    val startDate = Utils.instance().getDateStringByFormat()
                    travel?.startDate = startDate
                    travel?.state = 1
                    travel?.let { it ->
                        DataManager().insertOrReplaceTravel(it){ resultCode ->
                            if(resultCode == BuildConfig.NORMAL_CODE){
                                travelModel.getTravelData().value =  it
                            }else{
                                (activity as BaseActivity).showMessageAlertDialog("","${getString(R.string.DATABASE_ERROR)}($it)")
                            }
                        }
                    }
                }
                1 ->{//1：标记
                    val event = EventBusMessage.instance((activity as BaseActivity).TIME_LINE)
                    event.travel = travelModel
                    EventBus.getDefault().postSticky(event)
                    val stepIntent = Intent(activity,TimeLineActivity::class.java)
                    (activity as BaseActivity).baseStartActivity(stepIntent, ActivityMoveEnum.START_FROM_RIGHT)
                }
                2 ->{//2：编辑

                }
                3 ->{//3：明细
                    val event = EventBusMessage.instance((activity as BaseActivity).TRAVEL_DETAIL)
                    event.travel = travelModel
                    EventBus.getDefault().postSticky(event)
                    val freeIntent = Intent(activity,TravelDetailActivity::class.java)
                    (activity as BaseActivity).baseStartActivity(freeIntent, ActivityMoveEnum.START_FROM_RIGHT)
                }
                4 ->{//4:结束

                }
                5 ->{//5:图片
                    if(ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                        (activity as BaseActivity).showMessageAlertDialog(getString(R.string.common_001),getString(R.string.travel_007)){ _, _ ->
                            val localIntent = Intent()
                            localIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                            localIntent.data = Uri.fromParts("package", activity!!.packageName, null)
                            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(localIntent)
                        }
                    }else {
                        val intent = Intent(Intent.ACTION_PICK, null)
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                        startActivityForResult(intent, IMAGE_CODE)
                    }
                }
            }

        }
        pagerView.adapter = adapter
        pagerView.pageMargin = 50
        //预缓存页面数
        pagerView.offscreenPageLimit = 2
        refreshHandler = Handler{
            when(it.what){
                REFRESH_DATA -> {

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshData(message: EventBusMessage){
        if(message.message.equals((activity as BaseActivity).REFRESH_TRAVEL_DATA)){
            initData()
        }else if(message.message.equals((activity as BaseActivity).REFRESH_TRAVEL_DETAIL)) {//添加明细后，刷新支出与结余的情报
            currentTravelModel?.getTravelData()?.value = message.travel?.getTravelData()?.value
        }
    }

    private fun initData(){
        val dataManager = DataManager()
        travelData?.clear()
        dataManager.getNotEndTravel{resultCode, data ->
            if(resultCode == BuildConfig.NORMAL_CODE){
                travelData = data
                travelData?.let{ travels ->
                    travelNumber = travels.size
                    adapter.refresh(travels)
                    travel_view_pager.currentItem = 0
                    pager_index.itemOnClick = {currentItem ->
                        travel_view_pager.currentItem = currentItem
                    }
                    pager_index.refresh(travels.size,0)
                    travels.clear()
                }
            }else{
                (activity as BaseActivity).showMessageAlertDialog("","${getString(R.string.DATABASE_ERROR)}($resultCode)")
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            IMAGE_CODE -> {
               if(data != null && data.data != null){
                   try {
                       val width = Utils.instance().DPToPX(210f).toInt()
                       val height = Utils.instance().DPToPX(150f).toInt()
                       val aspectX = 7
                       val aspectY = 5
                       startActivityForResult(Utils.instance().crop(activity?.externalCacheDir,data.data!!,width,height,aspectX,aspectY), CROP_REQUEST_CODE)

                   }catch (e:Exception){
                       e.printStackTrace()
                   }
                }
            }
            CROP_REQUEST_CODE -> {
                if(data != null && data.data != null) {
                   val parcelFileDescriptor = activity!!.contentResolver.openFileDescriptor(data.data!!, "r")
                   val fileDescriptor = parcelFileDescriptor?.fileDescriptor
                   val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
                    currentTravelModel?.let {
                        val travel = it.getTravelData().value
                        travel?.setImageBitmap(image)
                        travel?.let {item ->
                            DataManager().insertOrReplaceTravel(item){resultCode ->
                                if(resultCode == BuildConfig.NORMAL_CODE){
                                    it.getTravelData().value =  item
                                }else{
                                    (activity as BaseActivity).showMessageAlertDialog("","${getString(R.string.DATABASE_ERROR)}($resultCode)")
                                }
                            }
                        }
                    }
                }
            }
        }
    }


}
