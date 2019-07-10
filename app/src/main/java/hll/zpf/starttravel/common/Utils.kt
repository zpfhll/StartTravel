package hll.zpf.starttravel.common

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.DatePicker
import hll.zpf.starttravel.base.BaseApplication
import java.text.DecimalFormat
import java.util.*

class Utils {


    var context:Context? = null

    val SP_NAME = "HLL"
    val UUID_KEY = "UUID"

    companion object{
        @SuppressLint("StaticFieldLeak")
        private  var utils:Utils? = null
            get() {
                if (field == null){
                    field = Utils()
                    field?.context = BaseApplication.application?.applicationContext
                }
                return field
            }
        @Synchronized
        fun instance():Utils{

            return utils!!
        }
    }

    fun getUUID():String{
        var uuid:String? = getFromSharedpreferences(UUID_KEY,null)
        if (uuid == null){
            uuid = UUID.randomUUID().toString()
            saveToSharedpreferences(UUID_KEY,uuid)
        }
        return uuid!!
    }

    fun saveToSharedpreferences(key:String,value:String){
        val sp =  context?.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE)
        val editor =  sp?.edit()
        editor?.putString(key,value)
        editor?.commit()
    }

    fun getFromSharedpreferences(key:String,default:String?):String?{
        val sp =  context?.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE)
        return sp?.getString(key,default)
    }


    fun getScreenWidth() : Int{
        val sm = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        var dm: DisplayMetrics = DisplayMetrics()
        sm.defaultDisplay.getMetrics(dm)
        return dm.widthPixels
    }

    fun getScreenHeight() : Int{
        val sm = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        var dm: DisplayMetrics = DisplayMetrics()
        sm.defaultDisplay.getMetrics(dm)
        return dm.heightPixels
    }

    fun getScreenDensity() : Float{
        val sm = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        var dm: DisplayMetrics = DisplayMetrics()
        sm.defaultDisplay.getMetrics(dm)
        return dm.density
    }

    /**
     * 转换数值的格式（###,##0.0）
     */
    fun transMoneyToString(money:Float) : String{
        val df = DecimalFormat("###,##0.0")
        return df.format(money)
    }

    /**
     * 显示日期选择框
     */
    fun showDatePickerView(mContext:Context,listener:DatePickerDialog.OnDateSetListener){
        val calendar = Calendar.getInstance()
        val dialog = DatePickerDialog(mContext,listener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH))
        dialog.show()
    }

    fun DPToPX(dp:Float) : Float{
        return dp * getScreenDensity() + 0.5f
    }

}