package hll.zpf.starttravel.page

import android.content.Intent
import android.graphics.Point
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.AMapGestureListener
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MyLocationStyle
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.geocoder.*
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import hll.zpf.starttravel.R
import hll.zpf.starttravel.base.BaseActivity
import hll.zpf.starttravel.common.HLogger
import hll.zpf.starttravel.common.enums.ActivityMoveEnum
import kotlinx.android.synthetic.main.activity_location_select.*
import java.util.ArrayList

class LocationSelectActivity : BaseActivity(),GeocodeSearch.OnGeocodeSearchListener,AMap.OnMyLocationChangeListener {



    private lateinit  var  myLatLng: LatLng
    private lateinit var mAMap: AMap
    private lateinit var mGeocoderSearch: GeocodeSearch

    private var isInitedMyLocation =false

    private var tipPoiItems: ArrayList<PoiItem>? = null
    private lateinit var adapter: MapTipAdapter
    private var isShowTipsView = true
    private var city: String? = null

    private lateinit var  searchLatLng: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_select)
        map_view.onCreate(savedInstanceState)
        mAMap = map_view.map

        //初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        // 连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        val myLocationStyle = MyLocationStyle()
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

        tipPoiItems = ArrayList()
        adapter = MapTipAdapter(tipPoiItems!!, this.context!!) { _, poi ->
            isShowTipsView = false
            searchLatLng = LatLng(poi.latLonPoint.latitude, poi.latLonPoint.longitude)
            search_content.setText(poi.title)
            mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchLatLng, 18f))
        }
        tips_list.adapter = adapter
        tips_list.visibility = View.GONE

        mAMap.setOnPOIClickListener{
            HLogger.instance().e("------->setOnPOIClickListener","poi : ${it.name}")
            searchLatLng = it.coordinate
            mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchLatLng, 18f))
            val poiSearch = PoiSearch(context,null)
            poiSearch.setOnPoiSearchListener(object:PoiSearch.OnPoiSearchListener{
                override fun onPoiItemSearched(poiItem: PoiItem?, p1: Int) {
                    poiItem?.let {
                        isShowTipsView = false
                        search_content.setText(poiItem.title)
                    }
                }
                override fun onPoiSearched(p0: PoiResult?, p1: Int) {
                }
            })
            poiSearch.searchPOIIdAsyn(it.poiId)
            true
        }

        mAMap.setAMapGestureListener(object : AMapGestureListener{

            private var isScroll = false

            override fun onDown(p0: Float, p1: Float) {
                isScroll = false
                HLogger.instance().e("------->onDown","onDown")
            }

            override fun onDoubleTap(p0: Float, p1: Float) {
                HLogger.instance().e("------->onDoubleTap","onDoubleTap")
            }

            override fun onFling(p0: Float, p1: Float) {
                HLogger.instance().e("------->onFling","onFling")
            }

            override fun onSingleTap(p0: Float, p1: Float) {
                HLogger.instance().e("------->onSingleTap","onSingleTap")
            }

            override fun onScroll(p0: Float, p1: Float) {
                HLogger.instance().e("------->onScroll","onScroll")
                isScroll = true
            }

            override fun onMapStable() {
                HLogger.instance().e("------->onMapStable","onMapStable")
            }

            override fun onUp(p0: Float, p1: Float) {
                HLogger.instance().e("------->onUp","onUp")
                if(isScroll){
                    searchLatLng = mAMap.projection.fromScreenLocation(Point(select_location.x.toInt(),select_location.y.toInt()))
                    val query = RegeocodeQuery(LatLonPoint(searchLatLng.latitude,searchLatLng.longitude),100f,GeocodeSearch.AMAP)
                    mGeocoderSearch.getFromLocationAsyn(query)
                }
                isScroll = false
            }

            override fun onLongPress(p0: Float, p1: Float) {
                HLogger.instance().e("------->onLongPress","onLongPress")
            }

        })

        search_content.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.toString() == "" || !isShowTipsView) {
                    tips_list.visibility = View.GONE
                    isShowTipsView = true
                } else {
                    val query = PoiSearch.Query(s.toString(),"",city)
                    query.pageSize = 20
                    val poiSearch = PoiSearch(context,query)
                    poiSearch.bound = PoiSearch.SearchBound(LatLonPoint(searchLatLng.latitude,searchLatLng.longitude),20000,true)
                    poiSearch.setOnPoiSearchListener(object:PoiSearch.OnPoiSearchListener{
                        override fun onPoiItemSearched(p0: PoiItem?, p1: Int) {
                        }
                        override fun onPoiSearched(p0: PoiResult?, p1: Int) {
                            tipPoiItems?.clear()
                            p0?.let {
                                if (p0.pois.size < 1) {
                                    tips_list.visibility = View.GONE
                                } else {
                                    tipPoiItems?.addAll(p0.pois)
                                    tips_list.visibility = View.VISIBLE
                                    adapter.notifyDataSetChanged()
                                }
                            }
                        }
                    })
                    poiSearch.searchPOIAsyn()
                }
            }
        })
    }

    override fun baseClickAction(view:View){
        super.baseClickAction(view)
        when(view.id){
            R.id.my_location_button -> {
                searchLatLng = myLatLng
                val query = RegeocodeQuery(LatLonPoint(myLatLng.latitude,myLatLng.longitude),100f,GeocodeSearch.AMAP)
                mGeocoderSearch.getFromLocationAsyn(query)
                mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 18f),500,object : AMap.CancelableCallback{
                    override fun onFinish() {
                    }
                    override fun onCancel() {
                    }
                })
            }
            R.id.search_btn -> {
                val resultIntent = Intent()
                resultIntent.putExtra("location",searchLatLng)
                resultIntent.putExtra("locationName",search_content.text.toString())
                setResult(0,resultIntent)
                finish()
                baseStartActivity(null, ActivityMoveEnum.BACK_FROM_LEFT)
            }
            R.id.back_btn -> {
                finish()
                baseStartActivity(null, ActivityMoveEnum.BACK_FROM_LEFT)
            }
        }

    }

    override fun onMyLocationChange(location: Location?) {
        location?.let {
            myLatLng = LatLng(it.latitude,it.longitude)
            if (!isInitedMyLocation) {
                isInitedMyLocation = true
                searchLatLng = myLatLng
                val query = RegeocodeQuery(LatLonPoint(myLatLng.latitude,myLatLng.longitude),100f,GeocodeSearch.AMAP)
                mGeocoderSearch.getFromLocationAsyn(query)
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
                isShowTipsView = false
                search_content.setText(formatAddress)
                HLogger.instance().e("------->onLocationChanged","formatAddress : $formatAddress")

            }
        }else{
            HLogger.instance().e("------->onRegeocodeSearched","获取业务地址失败")
        }
    }

    override fun onGeocodeSearched(p0: GeocodeResult?, p1: Int) {
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
        if(tips_list.visibility == View.VISIBLE){
            tips_list.visibility = View.GONE
        }else {
            finish()
            baseStartActivity(null, ActivityMoveEnum.BACK_FROM_LEFT)
        }
    }
}
