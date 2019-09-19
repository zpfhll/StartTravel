package hll.zpf.starttravel.page

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
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

    private lateinit var moveHandler:Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide)
        moveHandler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                when(msg.what){
                    1 -> {
                        val intent = Intent(context,HomeActivity::class.java)
                        val optins = ActivityOptions.makeSceneTransitionAnimation(context)
                        baseStartActivity(intent, optins)
                    }
                    2 -> {
                        EventBus.getDefault().postSticky(EventBusMessage.instance(PAGE_TYPE_NO_SKIP));
                        val intent = Intent(context,SignUpActivity::class.java)
                        val optins = ActivityOptions.makeSceneTransitionAnimation(context)
                        baseStartActivity(intent, optins)
                    }
                    3 -> {
                        val intent = Intent(context,LoginActivity::class.java)
                        val optins = ActivityOptions.makeSceneTransitionAnimation(context)
                        baseStartActivity(intent, optins)
                    }
                }
            }
        }
    }

    fun onClick(view: View){
        val userID = UserData.instance().getLoginUserId()

        val dao= BaseApplication.application!!.travelDatabase.detailDao()
        GlobalScope.launch {
            //有用户的时候，是访客的话直接跳转到HOME，不是访客的话就直接跳转到登录页面
            //没有用户的时候，跳转到注册页面
            val manager = DataManager()
            val user = manager.getUserByID(userID)
            var moveFlag = 0
            if(user != null){
                if(user.isVisitor){
                    moveFlag = 1
                }else{
                    moveFlag = 2
                }
            }else{
                moveFlag = 3

            }
            moveHandler.sendEmptyMessage(moveFlag)
        }
    }
}
