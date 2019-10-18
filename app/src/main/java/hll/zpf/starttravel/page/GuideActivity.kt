package hll.zpf.starttravel.page

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import hll.zpf.starttravel.BuildConfig
import hll.zpf.starttravel.R
import hll.zpf.starttravel.base.BaseActivity
import hll.zpf.starttravel.base.BaseApplication
import hll.zpf.starttravel.common.EventBusMessage
import hll.zpf.starttravel.common.UserData
import hll.zpf.starttravel.common.database.DataManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

class GuideActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide)
    }

    private fun moveTo(){
        val userID = UserData.instance().getLoginUserId()
        dataManager = DataManager()
        dataManager?.getUserByID(userID){resultCode, data ->
            if (resultCode == BuildConfig.NORMAL_CODE) {
                //有用户的时候，是访客的话直接跳转到HOME，不是访客的话就直接跳转到登录页面
                //没有用户的时候，跳转到注册页面
                if (data != null) {
                    if (data.isVisitor) {
                        val intent = Intent(context, HomeActivity::class.java)
                        val optins = ActivityOptions.makeSceneTransitionAnimation(context)
                        baseStartActivity(intent, optins)
                    } else {
                        EventBus.getDefault()
                            .postSticky(EventBusMessage.instance(PAGE_TYPE_NO_SKIP));
                        val intent = Intent(context, SignUpActivity::class.java)
                        val optins = ActivityOptions.makeSceneTransitionAnimation(context)
                        baseStartActivity(intent, optins)
                    }
                } else {
                    val intent = Intent(context, LoginActivity::class.java)
                    val optins = ActivityOptions.makeSceneTransitionAnimation(context)
                    baseStartActivity(intent, optins)
                }
            }else{
                showMessageAlertDialog("","${getString(R.string.DATABASE_ERROR)}($resultCode)")
            }
        }
    }

    fun onClick(view: View){
        when(view.id){
            R.id.guide_button -> {
                moveTo()
            }
        }

    }
}
