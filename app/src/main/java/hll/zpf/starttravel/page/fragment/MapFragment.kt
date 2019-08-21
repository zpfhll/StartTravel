package hll.zpf.starttravel.page.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.amap.api.maps.AMap
import com.amap.api.maps.MapView

import hll.zpf.starttravel.R
import com.amap.api.maps.model.MyLocationStyle
import com.amap.api.services.geocoder.*
import hll.zpf.starttravel.common.HLogger
import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.location.Location
import android.text.Editable
import android.text.TextWatcher
import android.widget.ListView
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.help.Inputtips
import com.amap.api.services.help.InputtipsQuery
import com.amap.api.services.help.Tip
import hll.zpf.starttravel.common.bean.StepBean
import kotlinx.android.synthetic.main.fragment_map.*
import java.util.ArrayList


class MapFragment : Fragment(),GeocodeSearch.OnGeocodeSearchListener,AMap.OnMyLocationChangeListener {

    private lateinit var mMapView:MapView

    private lateinit var mAMap:AMap

    private lateinit var mSearchContent:EditText

    private lateinit var mSearchBtn:Button

    private lateinit var mGeocoderSearch: GeocodeSearch

    private var city: String? = null

    private lateinit var tipView:ListView

    private var tips: ArrayList<Tip>? = null

    private lateinit var adapter: MapTipAdapter

    private var isShowTipsView = true

    private var isInitedMyLocation = false

    private lateinit  var  myLatLng: LatLng

    private  var selectTip:Tip? = null

    /**
     * 0:显示标记点 1：添加标记点时使用
     */
    var type:Int = 0

    var steps:List<StepBean>? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_map, container, false)
        //获取地图控件引用
        mMapView = rootView.findViewById(R.id.map_view)
        mMapView.onCreate(savedInstanceState)
        mAMap = mMapView.map

        val myLocationStyle = MyLocationStyle()//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(2000) //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER)
        mAMap.setOnMyLocationChangeListener(this)
        mAMap.myLocationStyle = myLocationStyle//设置定位蓝点的Style
        mAMap.uiSettings.isMyLocationButtonEnabled = false//设置默认定位按钮是否显示，非必需设置。
        mAMap.isMyLocationEnabled = true// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        mAMap.uiSettings.isScaleControlsEnabled = true //显示比例尺
        mAMap.uiSettings.isZoomControlsEnabled = false //关闭缩放按钮

        mSearchBtn = rootView.findViewById(R.id.search_btn)
        mSearchContent = rootView.findViewById(R.id.search_content)
        mGeocoderSearch = GeocodeSearch(this.context)
        mGeocoderSearch.setOnGeocodeSearchListener(this)
        tipView = rootView.findViewById(R.id.tips_list)
        tips = ArrayList()
        adapter = MapTipAdapter(tips!!, this.context!!){ _, tip ->
            isShowTipsView = false
            selectTip = tip
            mSearchContent.setText(tip.name)
        }
        tipView.adapter = adapter
        tipView.visibility = View.GONE
        mSearchBtn.setOnClickListener {
            val query = GeocodeQuery(mSearchContent.text.toString(),city)
            mGeocoderSearch.getFromLocationNameAsyn(query)
        }
        mSearchContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                selectTip = null
                if (s.toString() == "" || !isShowTipsView) {
                    tipView.visibility = View.GONE
                    isShowTipsView = true
                } else {
                    //第二个参数传入null或者“”代表在全国进行检索，否则按照传入的city进行检索
                    val inputquery = InputtipsQuery(s.toString(), city)
                    inputquery.cityLimit = true//限制在当前城市
                    val inputTips = Inputtips(context, inputquery)
                    inputTips.setInputtipsListener { list, i ->
                        tips?.clear()
                        if (list.size < 1) {
                            tipView.visibility = View.GONE
                        } else {
                            tips?.addAll(list)
                            tipView.visibility = View.VISIBLE
                            adapter.notifyDataSetChanged()
                        }
                    }
                    inputTips.requestInputtipsAsyn()
                }
            }
        })
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        my_location_button?.setOnClickListener {
            mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 18f),500,object : AMap.CancelableCallback{
                override fun onFinish() {
                }
                override fun onCancel() {
                }
            })
        }

        when(type){
            1 -> {

            }
            0 -> {
                search_view.visibility = View.GONE
                search_content.visibility = View.GONE
                search_btn.visibility = View.GONE
                tips_list.visibility = View.GONE
            }
        }

        steps?.let {
            for (item in it) {
                setMarker(LatLng(item.latitude,item.longitude),item.stepName,item.stepMemo,R.mipmap.marker)
            }
        }
    }

    private fun setMarker(psition:LatLng,title:String,content:String,id:Int){
        var markerOption = MarkerOptions()
        markerOption.position(psition)
        markerOption.title(title)
        markerOption.snippet(content)
        markerOption.draggable(false)
        markerOption.anchor(0.9f,0.9f)
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(resources,id)))
        mAMap.addMarker(markerOption)
    }

    override fun onMyLocationChange(location: Location?) {
        location?.let {
            myLatLng = LatLng(it.latitude,it.longitude)
            val query = RegeocodeQuery(LatLonPoint(it.latitude,it.longitude),100f,GeocodeSearch.AMAP)
            mGeocoderSearch.getFromLocationAsyn(query)

            if (!isInitedMyLocation) {
                isInitedMyLocation = true
                mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 18f))
            }
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        mMapView.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        //重新绘制加载地图
        mMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        //暂停地图的绘制
        mMapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //保存地图当前的状态
        mMapView.onSaveInstanceState(outState)
    }


    override fun onRegeocodeSearched(result: RegeocodeResult?, rCode: Int) {
        if(rCode == 1000){
            result?.let {
            city = it.regeocodeAddress.city
            HLogger.instance().e("------->onLocationChanged","${it.regeocodeQuery.point.longitude}: ${it.regeocodeQuery.point.latitude} : ${it.regeocodeAddress.district} : ${it.regeocodeAddress.formatAddress}")
            }
        }else{
            HLogger.instance().e("------->onRegeocodeSearched","获取业务地址失败")
        }
    }

    override fun onGeocodeSearched(p0: GeocodeResult?, p1: Int) {
        HLogger.instance().e("---->",p0.toString())
    }

}
