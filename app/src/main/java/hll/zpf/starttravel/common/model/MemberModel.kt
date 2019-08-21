package hll.zpf.starttravel.common.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hll.zpf.starttravel.common.database.entity.Member

class MemberModel : ViewModel()  {
    private lateinit  var memberData: MutableLiveData<Member>

    fun getMemberData(): MutableLiveData<Member> {
        if (!::memberData.isInitialized) {
            memberData = MutableLiveData()
            memberData.value = null
        }
        return memberData
    }
}