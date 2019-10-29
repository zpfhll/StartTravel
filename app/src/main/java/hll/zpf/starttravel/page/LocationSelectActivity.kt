package hll.zpf.starttravel.page

import android.graphics.Point
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.ListView
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MyLocationStyle
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.geocoder.*
import com.amap.api.services.help.Inputtips
import com.amap.api.services.help.InputtipsQuery
import com.amap.api.services.help.Tip
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import hll.zpf.starttravel.R
import hll.zpf.starttravel.base.BaseActivity
import hll.zpf.starttravel.common.HLogger
import hll.zpf.starttravel.common.enums.ActivityMoveEnum
import hll.zpf.starttravel.page.fragment.MapTipAdapter
import kotlinx.android.synthetic.main.activity_location_select.*
import java.util.ArrayList

class LocationSelectActivity : BaseActivity(),GeocodeSearch.OnGeocodeSearchListener,AMap.OnMyLocationChangeListener {



    private lateinit  var  myLatLng: LatLng
    private lateinit var mAMap: AMap
    private lateinit var mGeocoderSearch: GeocodeSearch

    private var isInitedMyLocation =false

    private var tips: ArrayList<Tip>? = null
    private lateinit var adapter: MapTipAdapter
    private var isShowTipsView = true
    private  var selectTip:Tip? = null
    private var city: String? = null

    private lateinit var  searchLatLng: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_select)
//        setTitle(
//            getString(R.string.location_select_001),
//            true,
//            getString(R.string.location_select_002),
//            R.drawable.back_button_background){
//            when(it.id){
//                R.id.left_button -> {//返回
//                    onKeyCodeBackListener()
//                }
//                R.id.right_button -> {//确认
//                    onKeyCodeBackListener()
//                }
//
//            }
//        }
        map_view.onCreate(savedInstanceState)
        mAMap = map_view.map

        val myLocationStyle = MyLocationStyle()//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(2000) //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER)
        myLocationStyle.radiusFillColor(getColor(R.color.transparent)) //精度圈的颜色设定
        myLocationStyle.strokeWidth(0f)//精度圈边框宽度
        mAMap.setOnMyLocationChangeListener(this)
        mAMap.myLocationStyle = myLocationStyle//设置定位蓝点的Style
        mAMap.uiSettings.isMyLocationButtonEnabled = false//设置默认定位按钮是否显示，非必需设置。
        mAMap.isMyLocationEnabled = true// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        mAMap.uiSettings.isScaleControlsEnabled = true //显示比例尺
        mAMap.uiSettings.isZoomControlsEnabled = false //关闭缩放按钮
        mAMap.uiSettings.isGestureScaleByMapCenter = true

        mGeocoderSearch = GeocodeSearch(this)
        mGeocoderSearch.setOnGeocodeSearchListener(this)

        tips = ArrayList()
        adapter = MapTipAdapter(tips!!, this.context!!){ _, tip ->
            isShowTipsView = false
            selectTip = tip
            search_content.setText(tip.name)
        }
        tips_list.adapter = adapter
        tips_list.visibility = View.GONE

        mAMap.setOnMapClickListener {
            tips_list.visibility = View.GONE
            searchLatLng = it
            val query = RegeocodeQuery(LatLonPoint(searchLatLng.latitude,searchLatLng.longitude),100f,GeocodeSearch.AMAP)
            mGeocoderSearch.getFromLocationAsyn(query)
        }


        search_content.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                selectTip = null
                if (s.toString() == "" || !isShowTipsView) {
                    tips_list.visibility = View.GONE
                    isShowTipsView = true
                } else {
                    //第二个参数传入null或者“”代表在全国进行检索，否则按照传入的city进行检索
                    val inputquery = InputtipsQuery(s.toString(), city)

                    val query = PoiSearch.Query(s.toString(),"",city)
                    query.pageSize = 20
                    val poiSearch = PoiSearch(context,query)
                    poiSearch.bound = PoiSearch.SearchBound(LatLonPoint(searchLatLng.latitude,searchLatLng.longitude),20000,true)
                    poiSearch.setOnPoiSearchListener(object:PoiSearch.OnPoiSearchListener{
                        override fun onPoiItemSearched(p0: PoiItem?, p1: Int) {
                        }
                        override fun onPoiSearched(p0: PoiResult?, p1: Int) {
                            tips?.clear()
                            p0?.let {
                                if (p0.pois.size < 1) {
                                    tips_list.visibility = View.GONE
                                } else {
                                    for (poi in p0.pois){
                                        val tip = Tip()
                                        tip.name = poi.title
                                        tip.district = poi.adName
                                        tip.address = poi.snippet
                                        tips?.add(tip)
                                    }
                                    tips_list.visibility = View.VISIBLE
                                    adapter.notifyDataSetChanged()
                                }
                            }

                        }
                    })
                    poiSearch.searchPOIAsyn()


//                    inputquery.type
//                    val inputTips = Inputtips(context, inputquery)
//                    inputTips.setInputtipsListener { list, i ->
//                        tips?.clear()
//                        if (list.size < 1) {
//                            tips_list.visibility = View.GONE
//                        } else {
//                            tips?.addAll(list)
//                            tips_list.visibility = View.VISIBLE
//                            adapter.notifyDataSetChanged()
//                        }
//                    }
//                    inputTips.requestInputtipsAsyn()
                }
            }
        })



    }

    override fun baseClickAction(view:View){
        super.baseClickAction(view)
        when(view.id){
            R.id.my_location_button -> {
                mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 18f),500,object : AMap.CancelableCallback{
                    override fun onFinish() {
                    }
                    override fun onCancel() {
                    }
                })
            }
            R.id.search_btn -> {

            }
            R.id.back_btn -> {
                onKeyCodeBackListener()
            }
        }

    }

    override fun onMyLocationChange(location: Location?) {
        location?.let {
            myLatLng = LatLng(it.latitude,it.longitude)
//            searchLatLng = mAMap.projection.fromScreenLocation(Point(select_location.x.toInt(),select_location.y.toInt()))
//            val query = RegeocodeQuery(LatLonPoint(searchLatLng.latitude,searchLatLng.longitude),100f,GeocodeSearch.AMAP)
//            mGeocoderSearch.getFromLocationAsyn(query)
            if (!isInitedMyLocation) {
                isInitedMyLocation = true
                mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 18f))
            }
        }
    }

    override fun onRegeocodeSearched(result: RegeocodeResult?, rCode: Int) {
        if(rCode == 1000){
            result?.let {
                city = it.regeocodeAddress.city
                var formatAddress = it.regeocodeAddress.formatAddress
                formatAddress = formatAddress.replace(it.regeocodeAddress.city,"")
                    .replace(it.regeocodeAddress.district,"")
                    .replace(it.regeocodeAddress.township,"")
                    .replace(it.regeocodeAddress.province,"")
                var position = -1
                var beforeIndex = -1
                if (it.regeocodeAddress.pois.size > 1) {
                    for (poi in it.regeocodeAddress.pois) {
                        if (formatAddress.contains(poi.toString())) {
                            val index = formatAddress.indexOf(poi.toString(), 0, false)
                            if (beforeIndex < 0) {
                                beforeIndex = index
                            } else {
                                if (index < beforeIndex) {
                                    beforeIndex = index
                                    position = it.regeocodeAddress.pois.indexOf(poi)
                                }
                            }
                        }
                    }
                    if(position >= 0) {
                        formatAddress = it.regeocodeAddress.pois[position].toString()
                    }
                }

                HLogger.instance().e("------->onLocationChanged","formatAddress : $formatAddress")

            }
        }else{
            HLogger.instance().e("------->onRegeocodeSearched","获取业务地址失败")
        }
    }

    override fun onGeocodeSearched(p0: GeocodeResult?, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDestroy() {
        super.onDestroy()
        map_view.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        //重新绘制加载地图
        map_view.onResume()
    }

    override fun onPause() {
        super.onPause()
        //暂停地图的绘制
        map_view.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //保存地图当前的状态
        map_view.onSaveInstanceState(outState)
    }

    override fun onKeyCodeBackListener() {
        super.onKeyCodeBackListener()
        finish()
        baseStartActivity(null, ActivityMoveEnum.BACK_FROM_LEFT)
    }
}
