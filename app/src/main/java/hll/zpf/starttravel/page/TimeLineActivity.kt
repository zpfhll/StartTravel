package hll.zpf.starttravel.page

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import hll.zpf.starttravel.R
import hll.zpf.starttravel.base.BaseActivity
import hll.zpf.starttravel.common.database.entity.Step
import hll.zpf.starttravel.common.enums.ActivityMoveEnum
import kotlinx.android.synthetic.main.activity_time_line.*

class TimeLineActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_line)
        setTitleWithColor(
            "心路历程",
            false,
            null,
            null,
            R.color.baseColor,
            R.color.white){
            when(it.id){
                R.id.left_button -> {//返回
                    onKeyCodeBackListener()
                }
            }
        }
        val datas = mutableListOf<Step>()
        for (index in 0..9){
            datas.add(Step.createStep())
        }
        val stepAdapter = TimeLineAdapter(this, datas){type,index ->
        }
        time_line.adapter = stepAdapter
        time_line.layoutManager = LinearLayoutManager(context)
    }

    override fun onKeyCodeBackListener() {
        super.onKeyCodeBackListener()
        finish()
        baseStartActivity(null, ActivityMoveEnum.BACK_FROM_LEFT)
    }
}
