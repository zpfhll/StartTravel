package hll.zpf.starttravel.common.bean

import android.media.Image

class StepBean {
    var stepid = ""
    /**
     * 纬度
     */
    var latitude:Double = 0.0
    /**
     * 经度
     */
    var longitude:Double = 0.0

    var stepName = ""
    var stepMemo = ""
    var travelImage: Image? = null



}