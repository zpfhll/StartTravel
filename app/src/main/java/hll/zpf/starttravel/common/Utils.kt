package hll.zpf.starttravel.common

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.util.DisplayMetrics
import android.view.WindowManager
import hll.zpf.starttravel.R
import hll.zpf.starttravel.base.BaseApplication
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.graphics.Canvas


class Utils {


    var context:Context? = null

    private val SP_NAME = "HLL"
    private val UUID_KEY = "UUID"

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
        return uuid
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
        var result = df.format(money)
        if(result.split(".")[1].equals("0")){
            result = result.split(".")[0]
        }
        return result
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


    /**
     * 图片转为二进制数据
     * @param bitmap 画像
     * @return
     */
    fun bitmapToBytes(bitmap: Bitmap): ByteArray {
        //将图片转化为位图
        val size = bitmap.width * bitmap.height * 4
        //创建一个字节数组输出流,流的大小为size
        val baos = ByteArrayOutputStream(size)
        try {
            //设置位图的压缩格式，质量为100%，并放入字节数组输出流中
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            //将字节数组输出流转化为字节数组byte[]
            return baos.toByteArray()
        } catch (e: Exception) {
        } finally {
            try {
                bitmap.recycle()
                baos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return ByteArray(0)
    }

    /**
     *　转换日期格式
     * 默认格式：yyyyMMddHHmmsssss
     * 默认日期：现在
     */
    fun getDateStringByFormat(format:String? = null,specialDate:Date? = null):String{
        var dateString:String
        var mFormat  = format
        if(mFormat == null || mFormat.equals("")){
            mFormat = "yyyyMMddHHmmssSSS"
        }
        val dateFormat = SimpleDateFormat(mFormat, Locale.CHINESE)
        if(specialDate != null){
            dateString = dateFormat.format(specialDate)
        }else {
            dateString = dateFormat.format(Date())
        }
        return dateString
    }

    /**
     *　转换日期格式
     * 默认格式：yyyyMMddHHmmsssss
     * 默认日期：现在
     */
    fun getDateStringByFormatAndDateString(specialDate:String,format:String? = null):String{
        var dateString:String
        var mFormat  = format
        if(mFormat == null || mFormat.equals("")){
            mFormat = "yyyyMMddHHmmssSSS"
        }
        val dateFormat = SimpleDateFormat(mFormat, Locale.CHINESE)
        if(!specialDate.equals("")){
            val data = SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.CHINESE).parse(specialDate)
            dateString = dateFormat.format(data)
        }else {
            dateString = dateFormat.format(Date())
        }
        return dateString
    }

    fun getMessageByCode(code:String?):String{
        var message:String? = null
        when(code){
            "E00001" -> {
                message = context?.getString(R.string.E00001)
            }
            "E00002" -> {
                message = context?.getString(R.string.E00002)
            }
            "E00003" -> {
                message = context?.getString(R.string.E00003)
            }
            null -> {
                message = "null"
            }
        }
        return "$message($code)";
    }


    /**
     * 判断属性的健全性
     * obj：判断的对象
     */
    fun reflectObject(obj:Any?) : Boolean{
        if(obj == null){
          return false
        }
        obj.javaClass.declaredFields.forEach {
            val annotation = it.getAnnotation(HLLAny::class.java)
            if(annotation != null && annotation.isMust){
                val methodName = "get${it.name.substring(0,1).toUpperCase()}${it.name.substring(1)}"
                val method = obj.javaClass.getMethod(methodName)
                if (method.invoke(obj) == null || method.invoke(obj) == ""){
                    return false
                }
            }
        }
        return true
    }

    /**
     * 金额的有效性检查
     */
    fun checkMoney(moneyStr:String?):Boolean{
        if(moneyStr.isNullOrEmpty() || moneyStr.startsWith(".") ){
            return false
        }
        try {
            moneyStr.toFloat()
        }catch (e:NumberFormatException){
            return false
        }
        return true
    }

    /**
     * 将Drawable转化为Bitmap
     */
    fun drawableToBitmap(drawable: Drawable?): Bitmap? {

        if (drawable == null){
            return null
        }

        // 取 drawable 的长宽
        val w = drawable.intrinsicWidth
        val h = drawable.intrinsicHeight

        // 取 drawable 的颜色格式
        val config = if (drawable.opacity != PixelFormat.OPAQUE)
            Bitmap.Config.ARGB_8888
        else
            Bitmap.Config.RGB_565
        // 建立对应 bitmap
        val bitmap = Bitmap.createBitmap(w, h, config)
        // 建立对应 bitmap 的画布
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, w, h)
        // 把 drawable 内容画到画布中
        drawable.draw(canvas)
        return bitmap
    }
}