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
import hll.zpf.starttravel.common.HLogger
import hll.zpf.starttravel.common.UserData
import hll.zpf.starttravel.common.database.DataManager
import hll.zpf.starttravel.internet.ApiAccess
import hll.zpf.starttravel.internet.bean.InternetResponse
import kotlinx.android.synthetic.main.activity_guide.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import kotlin.system.measureTimeMillis

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
//                moveTo()
                moveToLogin()
            }
        }

    }


    //多条流水线式请求
    @ExperimentalCoroutinesApi
    private fun step1(user:String, password:String) = GlobalScope.produce<InternetResponse> {
        HLogger.instance().e("step1:",user)
        val apiAccess = ApiAccess()
        val response = apiAccess.signIn(user,password)
        send(response)
    }

    @ExperimentalCoroutinesApi
    private fun step2(user:String,step1: ReceiveChannel<InternetResponse>) = GlobalScope.produce {
        HLogger.instance().e("step2:",user)
        if(!step1.isClosedForReceive) {
            val step1Response = step1.receive()
            if (step1Response.internetBean == null ){
                send(step1Response)
            }else {
                val apiAccess = ApiAccess()
                val response = apiAccess.signIn(user, "password")
                send(response)
            }
        }
    }

    @ExperimentalCoroutinesApi
    private suspend fun sumLogin(user:String): InternetResponse?{
        val mStep1 = step1(user,"password")
        val mStep2 = step2(user, mStep1)
        val result = mStep2.receive()
        HLogger.instance().e("$user:", result.toString())
        return result
    }


    private fun moveToLogin(){
//        val  intent = Intent(this,LoginActivity::class.java)
//        baseStartActivity(intent,ActivityMoveEnum.START_FROM_RIGHT)
        loading.visibility = View.VISIBLE
        val closeLoadingHandler = Handler{
            loading.visibility = View.GONE
            true
        }
        HLogger.instance().e("start:","moveToLogin")
        GlobalScope.launch {
            HLogger.instance().e("start:","launch")
            val time = measureTimeMillis {
                val user1 = async {  sumLogin("user1@test.com")}
                val user2 = async {  sumLogin("user2@test.com")}
                HLogger.instance().e("----->user2","${user2.await()}")
                HLogger.instance().e("----->user1","${user1.await()}")
            }
            HLogger.instance().e("time:","$time")
            val message = Message()
            message.what = 1
            closeLoadingHandler.sendMessage(message)
        }
    }


}
