package hll.zpf.starttravel.page

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import hll.zpf.starttravel.R
import hll.zpf.starttravel.base.BaseActivity
import hll.zpf.starttravel.common.HLogger
import hll.zpf.starttravel.common.components.CustomSwitchView

class AddTravelActivity : BaseActivity() {

    private lateinit var switch:CustomSwitchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_travel)
        setStatusBarColor(R.color.gray)
        switch = findViewById(R.id.travel_type_switch)
        setTitle(
            getString(R.string.add_travel_001),
            true,
            getString(R.string.add_travel_002)){
            when(it.id){
                R.id.left_button -> {//返回
                    finish()
                }
                R.id.right_button -> {//提交

                    //TODO  テスト
                    if (switch.isLeft) {
                        switch.selectRight(false)
                    }else{
                        switch.selectLeft(false)
                    }
                }
            }
        }

        switch.switchAction = {
            HLogger.instance().e("--->","switch left : $it")
        }

    }


}
