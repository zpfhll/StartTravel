package hll.zpf.starttravel.page

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.transition.Fade
import hll.zpf.starttravel.base.BaseApplication
import hll.zpf.starttravel.R
import hll.zpf.starttravel.base.BaseActivity
import hll.zpf.starttravel.internet.ApiAccess
import kotlinx.coroutines.delay


class SplashActivity : BaseActivity() {



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

    private fun loginByUserName() {
        val intent = Intent(this,GuideActivity::class.java)
        val optins = ActivityOptions.makeSceneTransitionAnimation(this)
        baseStartActivity(intent, optins)
    }

}
