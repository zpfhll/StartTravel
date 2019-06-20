package hll.zpf.starttravel

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.transition.Fade
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import hll.zpf.starttravel.base.BaseApplication
import hll.zpf.starttravel.internet.ApiAccess
import hll.zpf.starttravel.page.LoginActivity
import hll.zpf.starttravel.page.SignUpActivity
import android.transition.Slide
import hll.zpf.starttravel.base.BaseActivity


class MainActivity : BaseActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        BaseApplication.application = application as BaseApplication?

        val handler =  Handler{
                when(it?.what){
                    1 -> loginByUserName()
                    else -> {}
                }
                false
        }
        handler.sendEmptyMessageDelayed(1,1500)
        setStatusBarColor(R.color.white)

    }

    override fun onEnterAnimationComplete() {
        super.onEnterAnimationComplete()
        val fade = Fade()
        fade.duration = 1000
        window.exitTransition = fade
    }


    fun loginByUserName(){
        val intent = Intent(this,SignUpActivity::class.java)
//        val pair1 =  android.util.Pair<View, String>(findViewById(R.id.foot_left),"foot_left")
//        val pair2 =  android.util.Pair<View, String>(findViewById(R.id.foot_right),"foot_right")
        val optins = ActivityOptions.makeSceneTransitionAnimation(this)
        baseStartActivity(intent, optins)
//        val apiAccess = ApiAccess()
//        apiAccess.login("user1@qq.com","password","password"){isSuccess, response, errorMessage ->
//            if (isSuccess) {
//                when (response?.code()) {
//                    200 -> _contentTv?.text = response?.body()?.accessToken
//                    else -> {
//                        _contentTv?.text = "请求失败"
//                    }
//                }
//
//            }else{
//                _contentTv?.setText(errorMessage)
//            }
//        }
    }
}
