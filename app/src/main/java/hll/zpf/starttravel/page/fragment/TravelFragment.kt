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
import hll.zpf.starttravel.common.database.entity.Travel
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


    /**
     * 裁剪
     * @param w 输出宽
     * @param h 输出高
     * @param aspectX 宽比例
     * @param aspectY 高比例
     */
    private fun crop(uri: Uri, w: Int, h: Int, aspectX: Int, aspectY: Int): Intent {
        val intent = Intent("com.android.camera.action.CROP")
        // 照片URL地址
        intent.setDataAndType(uri, "image/*")
        intent.putExtra("crop", "true")
        intent.putExtra("aspectX", aspectX)
        intent.putExtra("aspectY", aspectY)
        intent.putExtra("outputX", w)
        intent.putExtra("outputY", h)
        // 输出路径
        intent.putExtra(
            MediaStore.EXTRA_OUTPUT, Uri.fromFile(
                File(activity?.externalCacheDir, "travel-cropped")
            )
        )
        // 输出格式
        intent.putExtra("outputFormat", "JPEG")
        // 不启用人脸识别
        intent.putExtra("noFaceDetection", true)
        intent.putExtra("return-data", false)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        return intent
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
                       startActivityForResult(crop(data.data!!,width,height,aspectX,aspectY), CROP_REQUEST_CODE)

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
                        travel?.imageBitmap = image
                        it.getTravelData().value =  travel
                        GlobalScope.launch {
                            travel?.let {item ->
                                DataManager().insertOrReplaceTravel(item)
                            }
                        }
                    }
                }
            }
        }
    }


}
