package hll.zpf.starttravel.page

import android.Manifest
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.transition.Fade
import hll.zpf.starttravel.base.BaseApplication
import hll.zpf.starttravel.R
import hll.zpf.starttravel.base.BaseActivity
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat


class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        BaseApplication.application = application as BaseApplication?

        val handler =  Handler{
                when(it?.what){
                    1 -> loginByUserName()//checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,CODE_FOR_WRITE_PERMISSION)
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

    private fun checkPermission(permisson:String,requestCode: Int) {
        val hasPermission =
            ContextCompat.checkSelfPermission(application, permisson)
        if (hasPermission == PackageManager.PERMISSION_GRANTED) {
            //拥有权限，执行操作
            when(permisson){
                Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
                    checkPermission(Manifest.permission.READ_PHONE_STATE,CODE_FOR_READ_PHONE_STATE_PERMISSION)
                }
                Manifest.permission.READ_PHONE_STATE -> {
                    checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION,CODE_FOR_LOCATION_PERMISSION)
                }
                Manifest.permission.ACCESS_COARSE_LOCATION -> {
                    loginByUserName()
                }
            }
        } else {
            //没有权限，向用户请求权限
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permisson),
                requestCode
            )
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        //通过requestCode来识别是否同一个请求
        when(requestCode){
            CODE_FOR_WRITE_PERMISSION -> {
                checkPermission(Manifest.permission.READ_PHONE_STATE,CODE_FOR_READ_PHONE_STATE_PERMISSION)
            }
            CODE_FOR_READ_PHONE_STATE_PERMISSION -> {
                checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION,CODE_FOR_LOCATION_PERMISSION)
            }
            CODE_FOR_LOCATION_PERMISSION -> {
                loginByUserName()
            }
        }
    }

}
