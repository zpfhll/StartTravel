package hll.zpf.starttravel.common

import android.util.Log
import hll.zpf.starttravel.BuildConfig

class HLogger  private  constructor(){

    companion object{
        private  var loger:HLogger? = null
        get() {
            if (field == null){
                field = HLogger()
            }
            return field
        }
        @Synchronized
        fun instance():HLogger{
            return loger!!
        }
    }


    fun d(tag:String = "-------->",log:String){
        if(BuildConfig.DEBUG){
            if (!tag.equals("-------->")){
                Log.d("$tag-------->", log)
            }else {
                Log.d(tag, log)
            }
        }
    }

    fun e(tag:String = "-------->",log:String){
        if (!tag.equals("-------->")){
            Log.e("$tag-------->", log)
        }else {
            Log.e(tag, log)
        }
    }


}