package hll.zpf.starttravel.page

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.amap.api.maps.model.LatLng
import hll.zpf.starttravel.R
import hll.zpf.starttravel.base.BaseActivity
import hll.zpf.starttravel.common.Utils
import hll.zpf.starttravel.common.enums.ActivityMoveEnum
import kotlinx.android.synthetic.main.activity_add_time_line.*
import java.util.*

class AddTimeLineActivity : BaseActivity() {

    lateinit var utils:Utils

    private val SELECT_LOCATION = 0

    private var positionName:String = ""
    private var latLng:LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_time_line)
        utils = Utils.instance()
        setTitle(
            getString(R.string.add_step_001),
            true,
            getString(R.string.add_step_007),
            R.drawable.back_button_background){
            when(it.id){
                R.id.left_button -> {//返回
                    onKeyCodeBackListener()
                }
                R.id.right_button ->{

                }
            }
        }
        step_time.text = utils.getDateStringByFormat(getString(R.string.data_format_1), Date())

    }

    override fun baseClickAction(view: View) {
        super.baseClickAction(view)
        when(view.id){
            R.id.location_name,R.id.location_button ->{
                val locationIntent = Intent(this,LocationSelectActivity::class.java)
                baseStartActivityForResult(locationIntent, SELECT_LOCATION,ActivityMoveEnum.START_FROM_RIGHT)
            }
        }
    }

    override fun baseActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.baseActivityResult(requestCode, resultCode, data)
        if(requestCode == SELECT_LOCATION){
            data?.let {
                positionName = data.getStringExtra("locationName")
                location_name.text = positionName
                latLng = data.getParcelableExtra("location") as LatLng
             }
        }
    }

    override fun onKeyCodeBackListener() {
        super.onKeyCodeBackListener()
        finish()
        baseStartActivity(null, ActivityMoveEnum.BACK_FROM_LEFT)
    }

}
