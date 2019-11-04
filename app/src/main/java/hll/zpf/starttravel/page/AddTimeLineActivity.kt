package hll.zpf.starttravel.page

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.core.content.ContextCompat
import com.amap.api.maps.model.LatLng
import hll.zpf.starttravel.BuildConfig
import hll.zpf.starttravel.R
import hll.zpf.starttravel.base.BaseActivity
import hll.zpf.starttravel.common.EventBusMessage
import hll.zpf.starttravel.common.Utils
import hll.zpf.starttravel.common.database.DataManager
import hll.zpf.starttravel.common.database.entity.Step
import hll.zpf.starttravel.common.enums.ActivityMoveEnum
import kotlinx.android.synthetic.main.activity_add_time_line.*
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.util.*

class AddTimeLineActivity : BaseActivity() {

    lateinit var utils:Utils

    private val SELECT_LOCATION = 0

    private val IMAGE_CODE = 1

    private val CROP_REQUEST_CODE = 2

    lateinit var  step :Step

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_time_line)
        val date = Date()
        utils = Utils.instance()
        step = Step.createStep()
        step.startDate = utils.getDateStringByFormat(getString(R.string.data_format_2), date)
        step.travelId = intent.getStringExtra("travelId")
        step_time.text = utils.getDateStringByFormat(getString(R.string.data_format_1), date)
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
                    step.memo = step_memo_editText.text.toString()
                    val name = step_name_editText.text.toString()
                    if(name.isNotBlank()){
                        step.name = step_name_editText.text.toString()
                    }else{
                        step.name = if (getString(R.string.add_step_010) != location_name.text.toString()) location_name.text.toString() else ""
                    }
                    if(step.name.isNullOrBlank()){
                        showMessageAlertDialog("",getString(R.string.time_line_E01))
                    }else {
                        dataManager = DataManager()
                        dataManager?.insertStep(step) { resultCode ->
                            if (resultCode == BuildConfig.NORMAL_CODE) {
                                val eventBusMessage = EventBusMessage()
                                eventBusMessage.message = TIME_LINE_REFRESH
                                EventBus.getDefault().post(eventBusMessage)
                                finish()
                                baseStartActivity(null, ActivityMoveEnum.BACK_FROM_LEFT)
                            } else {
                                showMessageAlertDialog(
                                    "",
                                    "${getString(R.string.DATABASE_ERROR)}($resultCode)"
                                ) { _, _ ->
                                    onKeyCodeBackListener()
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    override fun baseClickAction(view: View) {
        super.baseClickAction(view)
        when(view.id){
            R.id.location_name,R.id.location_button ->{
                val locationIntent = Intent(this,LocationSelectActivity::class.java)
                baseStartActivityForResult(locationIntent, SELECT_LOCATION,ActivityMoveEnum.START_FROM_RIGHT)
            }
            R.id.background_image -> {
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                    this.showMessageAlertDialog(getString(R.string.common_001),getString(R.string.travel_007)){ _, _ ->
                        val localIntent = Intent()
                        localIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                        localIntent.data = Uri.fromParts("package", this.packageName, null)
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



    override fun baseActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.baseActivityResult(requestCode, resultCode, data)
        when(requestCode){
            SELECT_LOCATION -> {
                data?.let {
                    location_name.text = data.getStringExtra("locationName")
                    val latLng = data.getParcelableExtra("location") as LatLng
                    step.latitude = latLng.latitude.toFloat()
                    step.longitude = latLng.longitude.toFloat()
                }
            }
            IMAGE_CODE -> {
                if(data != null && data.data != null){
                    try {
                        val width = Utils.instance().DPToPX(210f).toInt()
                        val height = Utils.instance().DPToPX(150f).toInt()
                        val aspectX = 7
                        val aspectY = 5
                        startActivityForResult(utils.crop(this.externalCacheDir,data.data!!,width,height,aspectX,aspectY), CROP_REQUEST_CODE)
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }
            CROP_REQUEST_CODE -> {
                if(data != null && data.data != null) {
                    val parcelFileDescriptor = this.contentResolver.openFileDescriptor(data.data!!, "r")
                    val fileDescriptor = parcelFileDescriptor?.fileDescriptor
                    val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
                    step.setImageBitmap(image)
                    background_image.setImageBitmap(step.getImageBitmap())
                }
            }
        }
    }

    override fun onKeyCodeBackListener() {
        super.onKeyCodeBackListener()
        finish()
        baseStartActivity(null, ActivityMoveEnum.BACK_FROM_LEFT)
    }

}
