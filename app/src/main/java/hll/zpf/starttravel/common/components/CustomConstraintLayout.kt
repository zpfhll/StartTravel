package hll.zpf.starttravel.common.components

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import hll.zpf.starttravel.common.database.entity.Member
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class CustomConstraintLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    var update:((Member) -> Unit)? = null

    fun registEventBus(){
        EventBus.getDefault().register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateView(member: Member){
        var mMember = this.tag as Member
        if (mMember.id.equals( member.id)){
            this.tag = member
            update?.let {
                it(member)
            }
        }
    }


    fun unRegistEventBus(){
        EventBus.getDefault().unregister(this)
    }


}