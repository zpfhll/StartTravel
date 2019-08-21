package hll.zpf.starttravel.common.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hll.zpf.starttravel.common.database.entity.Travel

class TravelModel : ViewModel() {

    private lateinit  var travelData:MutableLiveData<Travel>

    fun getTravelData():MutableLiveData<Travel>{
        if (!::travelData.isInitialized) {
            travelData = MutableLiveData()
            travelData.value = null
        }
        return travelData
    }


}