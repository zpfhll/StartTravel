package hll.zpf.starttravel.common.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hll.zpf.starttravel.common.bean.TravelBean

class TravelModel : ViewModel() {

    private lateinit  var travelData:MutableLiveData<TravelBean>

    fun getTravelData():MutableLiveData<TravelBean>{
        if (!::travelData.isInitialized) {
            travelData = MutableLiveData()
            travelData.value = null
        }
        return travelData
    }


}