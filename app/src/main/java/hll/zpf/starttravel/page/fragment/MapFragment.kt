package hll.zpf.starttravel.page.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.MapView

import hll.zpf.starttravel.R
import com.amap.api.maps2d.model.MyLocationStyle
import com.amap.api.services.geocoder.*
import hll.zpf.starttravel.common.HLogger
import android.annotation.SuppressLint
import android.graphics.Point
import com.amap.api.services.core.LatLonPoint


class MapFragment : Fragment(),GeocodeSearch.OnGeocodeSearchListener {


    private lateinit var mMapView:MapView

    private lateinit var mAMap:AMap

    private lateinit var mSearchContent:EditText

    private lateinit var mSearchBtn:Button

    private lateinit var mGeocoderSearch: GeocodeSearch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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
        mAMap.setMyLocationStyle(myLocationStyle)//设置定位蓝点的Style
        mAMap.uiSettings.isMyLocationButtonEnabled = true//设置默认定位按钮是否显示，非必需设置。
        mAMap.isMyLocationEnabled = true// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        mAMap.uiSettings.isScaleControlsEnabled = true //显示比例尺


        mSearchBtn = rootView.findViewById(R.id.search_btn)
        mSearchContent = rootView.findViewById(R.id.search_content)
        mGeocoderSearch = GeocodeSearch(this.context)
        mGeocoderSearch.setOnGeocodeSearchListener(this)

        mSearchBtn.setOnClickListener {
            val query = GeocodeQuery(mSearchContent.text.toString(),"010")
            mGeocoderSearch.getFromLocationNameAsyn(query)
        }

        mMapView.setOnTouchListener { _, event ->
            val x = event.x.toInt()
            val y = event.y.toInt()
            val point = Point(x,y)
            val latlng = mAMap.projection.fromScreenLocation(point)
            val gecode = GeocodeSearch(context)
            gecode.setOnGeocodeSearchListener(this)
            gecode.getFromLocationAsyn(RegeocodeQuery(LatLonPoint(latlng.latitude,latlng.longitude),200f,GeocodeSearch.AMAP))
            false
        }

        return rootView
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

    override fun onRegeocodeSearched(p0: RegeocodeResult?, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onGeocodeSearched(p0: GeocodeResult?, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        HLogger.instance().e("---->",p0.toString())
    }

}
